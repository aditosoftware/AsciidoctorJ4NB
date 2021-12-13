package org.netbeans.asciidoc.actions;


import org.jetbrains.annotations.NotNull;
import org.netbeans.api.editor.settings.SimpleValueNames;
import org.netbeans.modules.editor.indent.spi.CodeStylePreferences;
import org.openide.text.DataEditorSupport;
import org.openide.util.*;

import javax.swing.*;
import javax.swing.text.*;
import java.util.prefs.Preferences;

public class LineWrapAction extends JToggleButton
{
  private static final String _DISPLAY_NAME = NbBundle.getMessage(LineWrapAction.class, "LBL_LINE_WRAP_ACTION");
  private static final String _LINE_WRAP_WORDS = "words";
  private static final String _LINE_WRAP_NONE = "none";
  private static final String _TEXT_LINE_WRAP = SimpleValueNames.TEXT_LINE_WRAP;
  private final StyledDocument activeDocument;

  public LineWrapAction(@NotNull Lookup pLookup)
  {
    super(_DISPLAY_NAME);
    DataEditorSupport dataEditorSupport = pLookup.lookup(DataEditorSupport.class);
    activeDocument = dataEditorSupport.getDocument();
    this.setSelected(CodeStylePreferences.get(activeDocument).getPreferences().get(_TEXT_LINE_WRAP, "").equals("words")
                         || CodeStylePreferences.get(activeDocument).getPreferences().get(_TEXT_LINE_WRAP, "").equals("chars"));

    addActionListener(e -> {
      AbstractButton button = (AbstractButton) e.getSource();
      Preferences pref = CodeStylePreferences.get(activeDocument).getPreferences();
      if (button.isSelected())
      {
        //both put functions are necessary. First one puts the key and second one refreshes the documents properties
        pref.put(_TEXT_LINE_WRAP, _LINE_WRAP_WORDS);
        activeDocument.putProperty(_TEXT_LINE_WRAP, _LINE_WRAP_WORDS);
      }
      else
      {
        //both put functions are necessary. First one puts the key and second one refreshes the documents properties
        pref.put(_TEXT_LINE_WRAP, _LINE_WRAP_NONE);
        activeDocument.putProperty(_TEXT_LINE_WRAP, _LINE_WRAP_NONE);
      }
    });
  }
}
