package org.netbeans.asciidoc.converters;

import org.asciidoctor.*;
import org.jetbrains.annotations.*;
import org.netbeans.asciidoc.AsciidoctorConverter;

import java.io.*;
import java.util.*;

/**
 * Converter for AsciiDoc files to html files
 *
 * Possible parameters for the pParams map (For all options see {@link org.asciidoctor.Options}):
 * "safe":
 * unsafe
 * safe
 * server
 * secure
 * "in_place":
 * true
 * false
 *
 * @author m.kaspera, 08.04.2019
 */
public class ADocToHTMLConverter extends BaseAsciiDocConverter
{

  private static final String CONVERTER_FILE_TYPE = "html";

  private static List<String> asciiDocMimeTypes = List.of("text/asciidoc", "text/x-asciidoc");
  private static List<String> asciiDocFileEndings = List.of("adoc", "asciidoc");
  private static List<String> htmlMimeType = List.of("text/html");
  private static List<String> htmlFileEndings = List.of(CONVERTER_FILE_TYPE);

  public boolean canConvert(@NotNull String pSourceType, @NotNull String pTargetType, @NotNull Map<Object, Object> pParams)
  {
    return (asciiDocFileEndings.contains(pSourceType) || asciiDocMimeTypes.contains(pSourceType))
        && (htmlFileEndings.contains(pTargetType) || htmlMimeType.contains(pTargetType));
  }

  public Object convert(@NotNull File pSourceLocation, @NotNull File pTargetLocation, @NotNull String pSourceType, @NotNull String pTargetType,
                        @NotNull Map<Object, Object> pParams)
  {
    if (canConvert(pSourceType, pTargetType, Map.of()))
    {
      Options options = OptionsBuilder.options()
          .docType(CONVERTER_FILE_TYPE)
          .mkDirs(true)
          .attributes(AttributesBuilder.attributes()
                          .noFooter(true)
                          .unsetStyleSheet())
          .toFile(adjustFileEnding(pTargetLocation, CONVERTER_FILE_TYPE)).get();
      AsciidoctorConverter.getDefault().getDoctor().convertFile(pSourceLocation, fillOptions(options, pParams));
    }
    return null;
  }

  @Nullable
  public Object convert(@NotNull Reader pSource, @NotNull Writer pTarget, @NotNull String pSourceType, @NotNull String pTargetType,
                        @NotNull Map<Object, Object> pParams) throws IOException
  {
    if (canConvert(pSourceType, pTargetType, Map.of()))
    {
      Options options = OptionsBuilder.options()
          .docType(CONVERTER_FILE_TYPE)
          .attributes(AttributesBuilder.attributes()
                          .noFooter(true)
                          .showTitle(true))
          .get();
      AsciidoctorConverter.getDefault().getDoctor().convert(pSource, pTarget, fillOptions(options, pParams));
    }
    return null;
  }

  @Override
  protected void handleSpecialBehaviours(Map<String, Object> pOptions, Map<Object, Object> pAttributes, Map<Object, Object> pParams)
  {
    super.handleSpecialBehaviours(pOptions, pAttributes, pParams);

    if (pParams.containsKey("ATTRIBUTE_stylesheet"))
      pOptions.put(Options.HEADER_FOOTER, true);
    else
      pAttributes.put(Attributes.NOT_STYLESHEET_NAME, "");
  }

}
