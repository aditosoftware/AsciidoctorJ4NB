package org.netbeans.asciidoc.actions;

import de.adito.aditoweb.nbm.vaadinicons.IVaadinIconsProvider;
import de.adito.notification.INotificationFacade;
import de.adito.swing.icon.IconAttributes;
import lombok.NonNull;
import org.jetbrains.annotations.*;
import org.netbeans.api.editor.mimelookup.*;
import org.netbeans.api.project.*;
import org.netbeans.asciidoc.images.*;
import org.netbeans.spi.editor.codegen.CodeGenerator;
import org.openide.*;
import org.openide.filesystems.FileObject;
import org.openide.text.CloneableEditorSupport;
import org.openide.util.*;
import org.openide.windows.TopComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Adds an image to the adoc-File. Additionally the corresponding liquibase changelog files are created and the image reference in the adoc inserted.
 *
 * @author s.seemann, 07.10.2021
 */
public class AddUserhelpImageAction extends AbstractAction implements CodeGenerator
{
  private static final String _DISPLAY_NAME = NbBundle.getMessage(AddUserhelpImageAction.class, "LBL_ADD_IMAGE_ACTION");
  private Lookup lookup;

  public AddUserhelpImageAction(@NonNull Lookup pLookup)
  {
    super(_DISPLAY_NAME);
    putValue(SHORT_DESCRIPTION, _DISPLAY_NAME);
    lookup = pLookup;

    // Add icon if available
    IVaadinIconsProvider iconProvider = Lookup.getDefault().lookup(IVaadinIconsProvider.class);
    if (iconProvider != null)
      putValue(Action.SMALL_ICON, new ImageIcon(iconProvider.getImage(IVaadinIconsProvider.VaadinIcon.PICTURE, new IconAttributes(16f))));
  }

  @Override
  public void actionPerformed(@Nullable ActionEvent e)
  {
    FileObject fo = lookup.lookup(FileObject.class);
    Project project = FileOwnerQuery.getOwner(fo);

    // Show dialog
    AddUserhelpImagePanel panel = new AddUserhelpImagePanel(project);
    DialogDescriptor desc = new DialogDescriptor(panel, NbBundle.getMessage(AddUserhelpImageAction.class, "LBL_ADD_IMAGE_ACTION"));
    panel.setDescriptor(desc);

    desc.setButtonListener(evt ->
                           {
                             if (desc.getValue() == DialogDescriptor.OK_OPTION)
                             {
                               try
                               {
                                 // start execution
                                 new AddUserhelpImageExecutor(panel.getResult(), _findPane()).process();
                               }
                               catch (IOException pE)
                               {
                                 INotificationFacade.INSTANCE.error(pE);
                               }
                             }
                           });

    Dialog dlg = DialogDisplayer.getDefault().createDialog(desc);
    dlg.setVisible(true);
  }

  @Override
  @NonNull
  public String getDisplayName()
  {
    return _DISPLAY_NAME;
  }

  @Override
  public void invoke()
  {
    lookup = TopComponent.getRegistry().getActivated().getLookup();
    actionPerformed(null);
  }

  /**
   * @return the currently opened editor pane with the adoc
   */
  @Nullable
  private static JEditorPane _findPane()
  {
    TopComponent activatedTc = TopComponent.getRegistry().getActivated();
    if (activatedTc == null)
      return null;

    if (!(activatedTc instanceof CloneableEditorSupport.Pane))
      return null;

    return ((CloneableEditorSupport.Pane) activatedTc).getEditorPane();
  }

  /**
   * Factory for Alt-Insert-Menu
   */
  @MimeRegistrations({
      @MimeRegistration(mimeType = "text/x-asciidoc", service = CodeGenerator.Factory.class)
  })
  public static class AddUserhelpGeneratorFactory implements CodeGenerator.Factory
  {
    public List<? extends CodeGenerator> create(Lookup context)
    {
      return Collections.singletonList(new AddUserhelpImageAction(context));
    }
  }
}
