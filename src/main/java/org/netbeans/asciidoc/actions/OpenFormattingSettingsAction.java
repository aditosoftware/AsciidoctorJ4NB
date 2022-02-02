package org.netbeans.asciidoc.actions;



import de.adito.aditoweb.nbm.vaadinicons.IVaadinIconsProvider;
import de.adito.swing.icon.IconAttributes;
import org.netbeans.api.options.OptionsDisplayer;
import org.openide.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class OpenFormattingSettingsAction extends AbstractAction
{
  private static final String _DISPLAY_NAME = NbBundle.getMessage(OpenFormattingSettingsAction.class, "LBL_LINE_WRAP_ACTION");

  public OpenFormattingSettingsAction()
  {
    super(_DISPLAY_NAME);
    IVaadinIconsProvider vaadinProvider = Lookup.getDefault().lookup(IVaadinIconsProvider.class);
    putValue(SHORT_DESCRIPTION, _DISPLAY_NAME);
    if (vaadinProvider != null)
      putValue(Action.SMALL_ICON, new ImageIcon(vaadinProvider.getImage(IVaadinIconsProvider.VaadinIcon.COG, new IconAttributes(16f, Color.white))));
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    OptionsDisplayer.getDefault().open("Editor/Formatting");
  }
}
