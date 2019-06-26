package org.netbeans.asciidoc.actions;

import de.adito.aditoweb.nbm.nbide.nbaditointerface.conversions.IFileConverter;
import org.jetbrains.annotations.NotNull;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;

/**
 * @author m.kaspera, 18.06.2019
 */
public class ExportToPDFAction extends AbstractAction
{

  @NotNull
  private final Lookup lookup;
  private static final String SOURCE_FILE_TYPE = "adoc";
  private static final String TARGET_FILE_TYPE ="pdf";

  public ExportToPDFAction(@NotNull Lookup pLookup)
  {
    super("Export To PDF");
    putValue(SMALL_ICON, new ImageIcon(getClass().getResource(NbBundle.getMessage(ExportToPDFAction.class, "exportToPDFIcon"))));
    lookup = pLookup;
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setMultiSelectionEnabled(false);
    fileChooser.setFileFilter(fileChooser.getAcceptAllFileFilter());
    int returnValue = fileChooser.showOpenDialog(null);
    if(returnValue == JFileChooser.APPROVE_OPTION)
    {
      File exportToFile = fileChooser.getSelectedFile();
      IFileConverter converter = Lookup.getDefault().lookupAll(IFileConverter.class)
          .stream()
          .filter(pConverter -> pConverter.canConvert(SOURCE_FILE_TYPE, TARGET_FILE_TYPE, new HashMap<>()))
          .findFirst()
          .orElse(null);
      FileObject fileToConvert = lookup.lookup(FileObject.class);
      if (fileToConvert == null)
        fileToConvert = lookup.lookup(DataObject.class).getPrimaryFile();
      if (fileToConvert != null && converter != null)
      {
        try
        {
          converter.convert(new File(fileToConvert.getPath()), exportToFile, SOURCE_FILE_TYPE, TARGET_FILE_TYPE, new HashMap<>());
        }
        catch (IOException pE)
        {
          pE.printStackTrace();
        }
      }
    }
  }

  @Override
  public boolean isEnabled()
  {
    return Lookup.getDefault().lookupAll(IFileConverter.class).stream().anyMatch(pConverter -> pConverter.canConvert(SOURCE_FILE_TYPE, TARGET_FILE_TYPE, new HashMap<>()))
        && (lookup.lookup(FileObject.class) != null || lookup.lookup(DataObject.class).getPrimaryFile() != null);
  }
}
