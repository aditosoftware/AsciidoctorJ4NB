package org.netbeans.asciidoc.util;

import lombok.NonNull;

import java.util.regex.Pattern;

/**
 * @author w.glanzer, 28.01.2021
 */
public class HTMLUtility
{

  /**
   * Removes a single tag from pText
   *
   * @param pText        Text
   * @param pTagToRemove Tag to remove
   * @param pReplacement Replacement string
   * @return the new replaced string
   */
  @NonNull
  public static String removeTag(@NonNull String pText, @NonNull String pTagToRemove, @NonNull String pReplacement)
  {
    return Pattern.compile("(?:<" + pTagToRemove + "[^>]*)(?:(?:\\/>)|(?:>.*?<\\/" + pTagToRemove + ">))", Pattern.DOTALL)
        .matcher(pText)
        .replaceAll(pReplacement);
  }

}
