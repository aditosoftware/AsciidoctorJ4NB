package org.netbeans.asciidoc.actions;

import org.jetbrains.annotations.NotNull;
import org.netbeans.asciidoc.converters.standalone.ExportPdfStandalone;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.Paths;

/**
 * @author m.kaspera, 18.06.2019
 */
public class ExportToPDFAction extends AbstractAction
{

  @NotNull
  private final Lookup lookup;

  public ExportToPDFAction(@NotNull Lookup pLookup)
  {
    super("Export To PDF");
    putValue(SHORT_DESCRIPTION, "Export to PDF");
    putValue(SMALL_ICON, new ImageIcon(getClass().getResource(NbBundle.getMessage(ExportToPDFAction.class, "exportToPDFIcon"))));
    lookup = pLookup;
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setMultiSelectionEnabled(false);
    fileChooser.setFileFilter(new FileNameExtensionFilter("PDF", "pdf"));
    int returnValue = fileChooser.showOpenDialog(null);
    if(returnValue == JFileChooser.APPROVE_OPTION)
    {
      File exportToFile = fileChooser.getSelectedFile();
      try
      {
        FileObject fileToConvert = lookup.lookup(FileObject.class);
        if (fileToConvert == null)
          fileToConvert = lookup.lookup(DataObject.class).getPrimaryFile();
        File javaExe = new File(System.getProperty("java.home") + "/lib/java.exe");
        if (!javaExe.exists())
          javaExe = new File(System.getProperty("java.home") + "/bin/java.exe");
        if (javaExe.exists() && fileToConvert != null)
        {
          ProcessBuilder builder = new ProcessBuilder(javaExe.getAbsolutePath(), "-cp", _getStandaloneCP() + "/*", ExportPdfStandalone.class.getCanonicalName(),
                                                      fileToConvert.getPath(), exportToFile.getAbsolutePath());
          builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
          builder.start();
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

  private String _getStandaloneCP()
  {
    return Paths.get(ExportPdfStandalone.class.getProtectionDomain().getCodeSource().getLocation().getPath().substring(6)).getParent().toString();
  }
}
