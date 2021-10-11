package org.netbeans.asciidoc.images;

import de.adito.swing.*;
import info.clearthought.layout.*;
import org.jetbrains.annotations.NotNull;
import org.netbeans.api.project.Project;
import org.openide.DialogDescriptor;
import org.openide.filesystems.*;
import org.openide.util.NbBundle;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.nio.file.*;

/**
 * Panel for selecting all relevant information about adding a userhelp image.
 *
 * @author s.seemann, 07.10.2021
 */
public class AddUserhelpImagePanel extends JPanel
{
  private final JTextField author;
  private final PathSelectionPanel sourceImagePath;
  private final PathSelectionPanel targetImagePath;
  private final JTextField targetImageName;
  private final JTextField alternativeText;
  private final JLabel errorText;
  private DialogDescriptor desc;

  public AddUserhelpImagePanel(@NotNull Project pProject)
  {
    super(new BorderLayout());
    setBorder(new EmptyBorder(5, 5, 5, 5));

    double fill = TableLayoutConstants.FILL;
    double pref = TableLayoutConstants.PREFERRED;
    int gap = 5;

    double[] cols = {pref, gap, fill};
    double[] rows = {pref,
                     gap,
                     pref,
                     gap,
                     pref,
                     gap,
                     pref,
                     gap,
                     pref,
                     gap * 2,
                     pref,
                     fill};

    setLayout(new TableLayout(cols, rows));
    TableLayoutUtil tlu = new TableLayoutUtil(this);

    setPreferredSize(new Dimension(800, 200));
    DocumentListener listener = new _DocumentListener();

    tlu.add(0, 0, new JLabel(NbBundle.getMessage(AddUserhelpImagePanel.class, "LBL_PANEL_AUTHOR")));
    author = new JTextField(System.getProperty("user.name"));
    author.getDocument().addDocumentListener(listener);
    tlu.add(2, 0, author);

    FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("Images", ImageIO.getReaderFileSuffixes());

    String imagePathLabel = NbBundle.getMessage(AddUserhelpImagePanel.class, "LBL_PANEL_IMAGE_PATH");
    tlu.add(0, 2, new JLabel(imagePathLabel));
    sourceImagePath = new PathSelectionPanel(imagePathLabel, JFileChooser.FILES_ONLY, fileFilter);
    sourceImagePath.addDocumentListener(listener);
    tlu.add(2, 2, sourceImagePath);

    String targetPathLabel = NbBundle.getMessage(AddUserhelpImagePanel.class, "LBL_PANEL_TARGET_PATH");
    tlu.add(0, 4, new JLabel(targetPathLabel));
    targetImagePath = new PathSelectionPanel(targetPathLabel, JFileChooser.DIRECTORIES_ONLY);

    FileObject projectDir = pProject.getProjectDirectory();
    targetImagePath.setValue(FileUtil.getFileDisplayName(projectDir) + UserhelpImageContainer.DEFAULT_APPENDIX_IMAGE_PATH);

    targetImagePath.addDocumentListener(listener);
    tlu.add(2, 4, targetImagePath);

    tlu.add(0, 6, new JLabel(NbBundle.getMessage(AddUserhelpImagePanel.class, "LBL_PANEL_TARGET_IMAGE")));
    targetImageName = new JTextField();
    targetImageName.getDocument().addDocumentListener(listener);
    tlu.add(2, 6, targetImageName);

    tlu.add(0, 8, new JLabel(NbBundle.getMessage(AddUserhelpImagePanel.class, "LBL_PANEL_ALTERNATIVE_TEXT")));
    alternativeText = new JTextField();
    alternativeText.getDocument().addDocumentListener(listener);
    tlu.add(2, 8, alternativeText);

    errorText = new JLabel();
    errorText.setForeground(Color.RED);
    tlu.add(0, 10, 2, 10, errorText);
  }

  public void setDescriptor(@NotNull DialogDescriptor pDesc)
  {
    desc = pDesc;
    _updateStates();
  }

  public UserhelpImageContainer getResult()
  {
    return new UserhelpImageContainer(author.getText(), new File(sourceImagePath.getValue()), new File(targetImagePath.getValue()),
                                      targetImageName.getText(), alternativeText.getText());
  }

  private void _updateStates()
  {
    errorText.setText("");

    // prefill target image name if not already set with the name of the source image
    if (_isValidPath(sourceImagePath.getValue(), true) && targetImageName.getText().equals(""))
    {
      targetImageName.setText(new File(sourceImagePath.getValue()).getName());
    }

    // check required fields
    if (author.getText().equals(""))
      errorText.setText(NbBundle.getMessage(AddUserhelpImagePanel.class, "LBL_ERR_AUTHOR"));
    else if (!_isValidPath(sourceImagePath.getValue(), true))
      errorText.setText(NbBundle.getMessage(AddUserhelpImagePanel.class, "LBL_ERR_IMAGE_PATH"));
    else if (!_isValidPath(targetImagePath.getValue(), false))
      errorText.setText(NbBundle.getMessage(AddUserhelpImagePanel.class, "LBL_ERR_TARGET_PATH"));
    else if (targetImageName.getText().equals(""))
      errorText.setText(NbBundle.getMessage(AddUserhelpImagePanel.class, "LBL_ERR_TARGET_IMAGE"));
    else if (new File(targetImagePath.getValue(), UserhelpImageContainer.IMAGES_TARGET_DIRECTORY + File.separator + targetImageName.getText()).exists())
      errorText.setText(NbBundle.getMessage(AddUserhelpImagePanel.class, "LBL_ERR_TARGET_IMAGE_EXISTS"));

    if (desc != null && errorText.getText().equals(""))
      desc.setValid(true);
  }

  private static boolean _isValidPath(String pPath, boolean pFile)
  {
    try
    {
      if (pPath.trim().isEmpty())
        return false;
      Path path = Paths.get(pPath);

      if (pFile)
        return path.toFile().isFile();
      return true;
    }
    catch (InvalidPathException | NullPointerException ex)
    {
      return false;
    }
  }

  /**
   * Triggers always {@link #_updateStates()}
   */
  private class _DocumentListener implements DocumentListener
  {
    @Override
    public void insertUpdate(DocumentEvent e)
    {
      _updateStates();
    }

    @Override
    public void removeUpdate(DocumentEvent e)
    {
      _updateStates();
    }

    @Override
    public void changedUpdate(DocumentEvent e)
    {
      _updateStates();
    }
  }
}
