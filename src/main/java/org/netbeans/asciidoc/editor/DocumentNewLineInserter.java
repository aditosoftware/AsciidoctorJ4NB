package org.netbeans.asciidoc.editor;

import javax.swing.text.Document;

public interface DocumentNewLineInserter {
  String tryGetLineToAdd(Document document, int caretOffset);
}
