package org.netbeans.asciidoc.converters;

import de.adito.aditoweb.nbm.nbide.nbaditointerface.conversions.IFileConverter;
import org.asciidoctor.*;
import org.jetbrains.annotations.NotNull;
import org.netbeans.asciidoc.AsciidoctorConverter;
import org.openide.util.lookup.ServiceProvider;

import java.io.File;
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
@ServiceProvider(service = IFileConverter.class)
public class ADocToHTMLConverter extends BaseAsciiDocConverter implements IFileConverter
{

  private static final String CONVERTER_FILE_TYPE = "html";

  private static List<String> asciiDocMimeTypes = List.of("text/asciidoc", "text/x-asciidoc");
  private static List<String> asciiDocFileEndings = List.of("adoc", "asciidoc");
  private static List<String> htmlMimeType = List.of("text/html");
  private static List<String> htmlFileEndings = List.of(CONVERTER_FILE_TYPE);

  @Override
  public boolean canConvert(@NotNull String pSourceType, @NotNull String pTargetType, @NotNull Map<Object, Object> pParams)
  {
    return (asciiDocFileEndings.contains(pSourceType) || asciiDocMimeTypes.contains(pSourceType))
        && (htmlFileEndings.contains(pTargetType) || htmlMimeType.contains(pTargetType));
  }

  @Override
  public Object convert(@NotNull File pSourceLocation, @NotNull File pTargetLocation, @NotNull String pSourceType, @NotNull String pTargetType, @NotNull Map<Object, Object> pParams)
  {
    if (canConvert(pSourceType, pTargetType, Map.of()))
    {
      Options options = OptionsBuilder.options()
          .docType(CONVERTER_FILE_TYPE)
          .mkDirs(true)
          .attributes(AttributesBuilder.attributes()
                          .noFooter(true)
                          .unsetStyleSheet())
          .toFile(this.adjustFileEnding(pTargetLocation, CONVERTER_FILE_TYPE)).get();
      AsciidoctorConverter.getDefault().getDoctor().convertFile(pSourceLocation, fillOptions(options, pParams));
    }
    return null;
  }
}
