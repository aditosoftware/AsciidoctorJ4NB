package org.netbeans.asciidoc.util;

import java.util.Iterator;

public interface PeekableIterator<T> extends Iterator<T> {
  T tryPeekNext();
}
