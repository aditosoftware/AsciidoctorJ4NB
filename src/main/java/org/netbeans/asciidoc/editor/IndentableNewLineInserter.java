package org.netbeans.asciidoc.editor;

public interface IndentableNewLineInserter {
  String tryGetLineToAdd(String prevLine, int nonSpaceIndex);
}
