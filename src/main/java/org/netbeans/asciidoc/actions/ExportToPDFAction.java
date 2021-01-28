package org.netbeans.asciidoc.actions;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.lang3.SystemUtils;
import org.jetbrains.annotations.NotNull;
import org.netbeans.api.progress.*;
import org.netbeans.asciidoc.converters.standalone.ExportPdfStandalone;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.modules.Modules;
import org.openide.util.*;
import org.openide.windows.WindowManager;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Locale;
import java.util.concurrent.*;
import java.util.logging.*;

/**
 * @author m.kaspera, 18.06.2019
 */
public class ExportToPDFAction extends AbstractAction
{
  private static final Logger logger = Logger.getLogger(ExportToPDFAction.class.getName());

  @NotNull
  private final Lookup lookup;
  private final ExecutorService executorService;

  public ExportToPDFAction(@NotNull Lookup pLookup)
  {
    super("Export To PDF");
    putValue(SHORT_DESCRIPTION, "Export to PDF");
    putValue(SMALL_ICON, new ImageIcon(getClass().getResource(NbBundle.getMessage(ExportToPDFAction.class, "exportToPDFIcon"))));
    lookup = pLookup;
    executorService = Executors.newCachedThreadPool(new ThreadFactoryBuilder()
                                                        .setNameFormat("tAsciidocExporter")
                                                        .setDaemon(true)
                                                        .build());
    _initLogger();
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Export to PDF");
    fileChooser.setMultiSelectionEnabled(false);
    fileChooser.setFileFilter(new FileNameExtensionFilter("PDF", "pdf"));
    int returnValue = fileChooser.showSaveDialog(WindowManager.getDefault().getMainWindow());
    if (returnValue == JFileChooser.APPROVE_OPTION)
    {
      // Find file to convert
      FileObject fileToConvert = lookup.lookup(FileObject.class);
      if (fileToConvert == null)
        fileToConvert = lookup.lookup(DataObject.class).getPrimaryFile();

      // Find target file
      File exportToFile = fileChooser.getSelectedFile();
      if (!exportToFile.getName().toLowerCase(Locale.ROOT).endsWith(".pdf"))
        exportToFile = new File(exportToFile.getParentFile(), exportToFile.getName() + ".pdf");

      // copy to make it final
      final FileObject source = fileToConvert;
      final File target = exportToFile;

      // execute
      if(source != null)
        executorService.execute(() -> _exportTo(source, target));
    }
  }

  @Override
  public boolean isEnabled()
  {
    //noinspection ConstantConditions
    return ExportPdfStandalone.class.getCanonicalName() != null;
  }

  /**
   * Executes the export process and waits synchronously
   *
   * @param pTarget target to export to
   */
  private void _exportTo(@NotNull FileObject pSource, @NotNull File pTarget)
  {
    ProgressHandle handle = ProgressHandleFactory.createSystemHandle("Exporting '" + pSource.getNameExt() + "' as PDF...");
    handle.start();
    handle.progress(pTarget.getAbsolutePath());

    try
    {
      File javaFile = _getJavaFile();
      if (javaFile.exists())
      {
        final String javaExePath = javaFile.getAbsolutePath();
        final String fileToConvertPath = pSource.getPath();
        final String standaloneCP = _getFullClassPath();
        String exportFilePath = pTarget.getAbsolutePath();
        logger.log(Level.INFO, String.format("AsciiDocPlugin standalone call is: %s %s %s %s %s %s", javaExePath, "-cp", standaloneCP,
                                             ExportPdfStandalone.class.getCanonicalName(), fileToConvertPath, exportFilePath));
        ProcessBuilder builder = new ProcessBuilder(javaExePath, "-cp", standaloneCP, ExportPdfStandalone.class.getCanonicalName(),
                                                    fileToConvertPath, exportFilePath);
        builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        builder.start().waitFor(5, TimeUnit.MINUTES);
      }
      else if (!javaFile.exists())
      {
        logger.log(Level.INFO, String.format("AsciiDocPlugin could not find java exe, java.home property returns %s",
                                             System.getProperty("java.home")));
      }
    }
    catch(Exception e)
    {
      logger.log(Level.WARNING, "", e);
    }
    finally
    {
      handle.finish();
    }

    try
    {
      // open after export
      if (pTarget.exists() && Desktop.isDesktopSupported())
        Desktop.getDesktop().open(pTarget);
    }
    catch(Exception e)
    {
      // ignored
    }
  }

  /**
   * @return the full classpath to execute the standalone class
   */
  @NotNull
  private String _getFullClassPath()
  {
    String prefix = _getClasspathFolder();
    String separator = SystemUtils.IS_OS_WINDOWS ? ";" : ":";
    StringBuilder builder = new StringBuilder();

    // add all files from "root" folder
    builder.append(prefix).append("/*");
    builder.append(separator);

    // add all files from "ext" folder
    try
    {
      prefix += "/ext/" + Modules.getDefault().ownerOf(getClass()).getCodeName();
      Files.walkFileTree(new File(prefix).toPath(), new SimpleFileVisitor<>()
      {
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
        {
          if (attrs.isDirectory())
          {
            builder.append(dir.toAbsolutePath().toString()).append("/*");
            builder.append(separator);
          }

          return FileVisitResult.CONTINUE;
        }
      });
    }
    catch (Exception e)
    {
      logger.log(Level.WARNING, "", e);
    }

    return builder.toString();
  }

  /**
   * retrieve the path to the folder that the jar file containing the StandAlone class is in
   *
   * @return path to the folder containing the jar file of the StandAlone class
   */
  private String _getClasspathFolder()
  {
    // get the path of the jarFile. Is something along the line file:/.*
    String jarFilePath = ExportPdfStandalone.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    // remove the file: to get the file path
    String filePath = jarFilePath.substring(5);
    // if the OS is Windows, also remove the / because /C:.* is not a valid path. The / is needed in Linux and Mac however
    if (SystemUtils.IS_OS_WINDOWS)
      filePath = filePath.substring(1);
    return Paths.get(filePath).getParent().toString();
  }

  /**
   * Determines the location of the java file that was used to start the current application
   *
   * @return File that represents the java application file used to run the current application
   */
  private File _getJavaFile()
  {
    String javaFileName = SystemUtils.IS_OS_WINDOWS ? "java.exe" : "java";
    File javaFile = new File(System.getProperty("java.home") + "/lib/" + javaFileName);
    if (!javaFile.exists())
      javaFile = new File(System.getProperty("java.home") + "/bin/" + javaFileName);
    return javaFile;
  }

  /**
   * Sets the LogLevel in such a way that the logs are shown instead of swallowed by Netbeans
   */
  private void _initLogger()
  {
    Handler[] handlers = logger.getHandlers();
    logger.setLevel(Level.INFO);
    for (Handler h : handlers)
    {
      if (h instanceof FileHandler)
        h.setLevel(Level.INFO);
    }
  }
}
