package org.netbeans.asciidoc.util;

import org.jetbrains.annotations.NotNull;

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
  @NotNull
  public static String removeTag(@NotNull String pText, @NotNull String pTagToRemove, @NotNull String pReplacement)
  {
    return Pattern.compile("(?:<" + pTagToRemove + "[^>]*)(?:(?:\\/>)|(?:>.*?<\\/" + pTagToRemove + ">))", Pattern.DOTALL)
        .matcher(pText)
        .replaceAll(pReplacement);
  }

}
