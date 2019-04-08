package org.netbeans.asciidoc.structure;

import org.jtrim.utils.ExceptionHelper;

import java.util.Objects;

public final class AsciidoctorToken {
  private final AsciidoctorTokenId id;
  private final int startIndex;
  private final int endIndex;

  public AsciidoctorToken(AsciidoctorTokenId id, int startIndex, int endIndex) {
    ExceptionHelper.checkArgumentInRange(endIndex, startIndex, Integer.MAX_VALUE, "endIndex");

    this.id = Objects.requireNonNull(id, "id");
    this.startIndex = startIndex;
    this.endIndex = endIndex;
  }

  public AsciidoctorTokenId getId() {
    return id;
  }

  public int getLength() {
    return endIndex - startIndex;
  }

  public int getStartIndex() {
    return startIndex;
  }

  public int getEndIndex() {
    return endIndex;
  }

  public String getName(CharSequence input) {
    return id.getName(input, startIndex, endIndex);
  }
}
