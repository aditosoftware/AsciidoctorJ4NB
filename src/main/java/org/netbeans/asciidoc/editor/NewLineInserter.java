package org.netbeans.asciidoc.editor;

public interface NewLineInserter {
  String tryGetLineToAdd(String prevLine);
}
