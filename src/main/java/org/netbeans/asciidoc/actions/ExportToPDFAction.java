package org.netbeans.asciidoc.actions;

import org.apache.commons.lang3.SystemUtils;
import org.jetbrains.annotations.NotNull;
import org.netbeans.asciidoc.converters.standalone.ExportPdfStandalone;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.*;
import org.openide.windows.WindowManager;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.Paths;
import java.util.logging.*;

/**
 * @author m.kaspera, 18.06.2019
 */
public class ExportToPDFAction extends AbstractAction
{

  @NotNull
  private final Lookup lookup;
  private final Logger logger = Logger.getLogger(ExportToPDFAction.class.getName());

  public ExportToPDFAction(@NotNull Lookup pLookup)
  {
    super("Export To PDF");
    putValue(SHORT_DESCRIPTION, "Export to PDF");
    putValue(SMALL_ICON, new ImageIcon(getClass().getResource(NbBundle.getMessage(ExportToPDFAction.class, "exportToPDFIcon"))));
    lookup = pLookup;
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
      File exportToFile = fileChooser.getSelectedFile();
      try
      {
        FileObject fileToConvert = lookup.lookup(FileObject.class);
        if (fileToConvert == null)
          fileToConvert = lookup.lookup(DataObject.class).getPrimaryFile();
        File javaFile = _getJavaFile();
        if (javaFile.exists() && fileToConvert != null)
        {
          final String javaExePath = javaFile.getAbsolutePath();
          final String fileToConvertPath = fileToConvert.getPath();
          final String standaloneCP = _getStandaloneCP() + "/*";
          String exportFilePath = exportToFile.getAbsolutePath();
          logger.log(Level.INFO, () -> String.format("AsciiDocPlugin standalone call is: %s %s %s %s %s %s", javaExePath, "-cp", standaloneCP,
                                                     ExportPdfStandalone.class.getCanonicalName(), fileToConvertPath, exportFilePath));
          ProcessBuilder builder = new ProcessBuilder(javaExePath, "-cp", standaloneCP, ExportPdfStandalone.class.getCanonicalName(), fileToConvertPath, exportFilePath);
          builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
          builder.start();
        }
        else if (!javaFile.exists())
        {
          logger.log(Level.INFO, () -> String.format("AsciiDocPlugin could not find java exe, java.home property returns %s", System.getProperty("java.home")));
        }
      }
      catch (IOException pE)
      {
        pE.printStackTrace();
      }
    }
  }

  @Override
  public boolean isEnabled()
  {
    return ExportPdfStandalone.class.getCanonicalName() != null;
  }

  /**
   * retrieve the path to the folder that the jar file containing the StandAlone class is in
   *
   * @return path to the folder containing the jar file of the StandAlone class
   */
  private String _getStandaloneCP()
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
