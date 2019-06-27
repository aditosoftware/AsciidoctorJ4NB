package org.netbeans.asciidoc.converters.standalone;

import org.netbeans.asciidoc.converters.*;

import java.io.File;
import java.util.*;

/**
 * @author m.kaspera, 19.06.2019
 */
public class ExportPdfStandalone extends BaseAsciiDocConverter
{

  private static final String CONVERTER_FILE_TYPE = "pdf";
  private static final String CONVERTER_SOURCE_TYPE = "adoc";

  /**
   * args parameters:
   * sourceFile     path to the asciidoc file that should be converted
   * targetFile     path to the location that the resulting pdf file should be placed at. (path to the file, not directory)
   * options        optional arguments, should be pairs of key and value
   *
   * example:
   * path/to/adocFile.adoc dir/result.pdf safe unsafe
   *
   * @param args see above
   */
  public static void main(String[] args)
  {
    if (args.length >= 2 && args.length % 2 == 0)
    {
      File sourceFile = new File(args[0]);
      File targetFile = new File(args[1]);
      if (!sourceFile.exists() || targetFile.isDirectory())
        return;

      Map<Object, Object> parameters = new HashMap<>();
      // put in safety mode as unsafe as default value in case the file should be put into a location that is outside of the directory java was called from
      parameters.put("safe", "unsafe");
      for (int index = 2; index < args.length; index = index + 2)
      {
        parameters.put(args[index], args[index + 1]);
      }
      new ADocToPdfConverter().convert(sourceFile, targetFile, CONVERTER_SOURCE_TYPE, CONVERTER_FILE_TYPE, parameters);
    }
  }

}
