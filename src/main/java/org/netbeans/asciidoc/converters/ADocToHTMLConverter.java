package org.netbeans.asciidoc.converters;

import de.adito.aditoweb.nbm.nbide.nbaditointerface.conversions.IFileConverterParameterProvider;
import org.asciidoctor.*;
import org.jetbrains.annotations.*;
import org.netbeans.asciidoc.AsciidoctorConverter;
import org.openide.util.Lookup;

import java.io.*;
import java.nio.charset.StandardCharsets;
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

  private static final List<String> asciiDocMimeTypes = List.of("text/asciidoc", "text/x-asciidoc");
  private static final List<String> asciiDocFileEndings = List.of("adoc", "asciidoc");
  private static final List<String> htmlMimeType = List.of("text/html");
  private static final List<String> htmlFileEndings = List.of(CONVERTER_FILE_TYPE);

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
      // Adjust encoding
      pSourceLocation = adjustFileEncoding(pSourceLocation, StandardCharsets.UTF_8);

      // Fill Options
      Map<String, Object> params = fillOptions(OptionsBuilder.options()
                                                   .docType(CONVERTER_FILE_TYPE)
                                                   .mkDirs(true)
                                                   .toFile(adjustFileEnding(pTargetLocation, CONVERTER_FILE_TYPE)).get(),
                                               pParams);

      // Params modify
      for (IFileConverterParameterProvider prov : Lookup.getDefault().lookupAll(IFileConverterParameterProvider.class))
        prov.modifyParameters(pSourceLocation, pTargetLocation, pSourceType, pTargetType, params);

      // Convert
      AsciidoctorConverter.getDefault().getDoctor().convertFile(pSourceLocation, params);
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
          .get();
      AsciidoctorConverter.getDefault().getDoctor().convert(pSource, pTarget, fillOptions(options, pParams));
    }
    return null;
  }

}
