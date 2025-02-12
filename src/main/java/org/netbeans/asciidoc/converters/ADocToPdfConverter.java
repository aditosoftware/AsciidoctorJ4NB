package org.netbeans.asciidoc.converters;

import lombok.NonNull;
import org.asciidoctor.*;
import org.jetbrains.annotations.*;
import org.netbeans.asciidoc.AsciidoctorConverter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Converter for AsciiDoc to pdf files
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
public class ADocToPdfConverter extends BaseAsciiDocConverter
{

  private static final String CONVERTER_FILE_TYPE = "pdf";

  private static List<String> asciiDocMimeTypes = List.of("text/asciidoc", "text/x-asciidoc");
  private static List<String> asciiDocFileEndings = List.of("adoc", "asciidoc");
  private static List<String> htmlMimeType = List.of("application/pdf", "application/x-pdf");
  private static List<String> pdfFileEndings = List.of(CONVERTER_FILE_TYPE);

  public boolean canConvert(@NonNull String pSourceType, @NonNull String pTargetType, @NonNull Map<Object, Object> pParams)
  {
    return (asciiDocFileEndings.contains(pSourceType) || asciiDocMimeTypes.contains(pSourceType))
        && (pdfFileEndings.contains(pTargetType) || htmlMimeType.contains(pTargetType));
  }

  public Object convert(@NonNull File pSourceLocation, @NonNull File pTargetLocation, @NonNull String pSourceType, @NonNull String pTargetType,
                        @NonNull Map<Object, Object> pParams)
  {
    if(canConvert(pSourceType, pTargetType, Map.of()))
    {
      // Adjust encoding
      pSourceLocation = adjustFileEncoding(pSourceLocation, StandardCharsets.UTF_8);

      File targetFile = adjustFileEnding(pTargetLocation, CONVERTER_FILE_TYPE);

      // delete if exists, because it will be regenerated
      if(targetFile.exists() && targetFile.canRead())
        //noinspection ResultOfMethodCallIgnored
        targetFile.delete();

      Options options = OptionsBuilder.options()
          .backend(CONVERTER_FILE_TYPE)
          .mkDirs(true)
          .toFile(targetFile).get();
      AsciidoctorConverter.getDefault().getDoctor().convertFile(pSourceLocation, fillOptions(options, pParams));
    }
    return null;
  }

  @Nullable
  public Object convert(@NonNull Reader pSource, @NonNull Writer pTarget, @NonNull String pSourceType, @NonNull String pTargetType,
                        @NonNull Map<Object, Object> pParams) throws IOException
  {
    if(canConvert(pSourceType, pTargetType, Map.of()))
    {
      Options options = OptionsBuilder.options()
          .backend(CONVERTER_FILE_TYPE)
          .get();
      AsciidoctorConverter.getDefault().getDoctor().convert(pSource, pTarget, fillOptions(options, pParams));
    }
    return null;
  }
}
