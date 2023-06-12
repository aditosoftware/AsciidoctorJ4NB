package org.netbeans.asciidoc.converters;

import lombok.NonNull;
import org.apache.commons.io.FileUtils;
import org.asciidoctor.*;
import org.netbeans.api.queries.FileEncodingQuery;
import org.openide.filesystems.FileUtil;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;

/**
 * Basic class for the Converter classes that has some functions all of them need
 *
 * @author m.kaspera, 08.04.2019
 */
public class BaseAsciiDocConverter
{

  /**
   * create a map that contains options and parameters from pOptions and pParams
   *
   * @param pOptions Options object with pre-filled options (such as backend)
   * @param pParams  params passed in from outside
   * @return map with both options and params in it
   */
  Map<String, Object> fillOptions(Options pOptions, Map<Object, Object> pParams)
  {
    Map<String, Object> optionsMap = pOptions.map();

    //noinspection unchecked
    Map<Object, Object> attributesMap = (Map<Object, Object>) optionsMap.computeIfAbsent(Options.ATTRIBUTES, (e) -> new HashMap<>());

    pParams.forEach((pKey, pValue) -> {
      if ("safe".equals(pKey))
        optionsMap.put((String) pKey, convert((String) pValue).getLevel());
      else if (pKey != null && String.valueOf(pKey).startsWith("ATTRIBUTE_"))
        attributesMap.put(String.valueOf(pKey).substring("ATTRIBUTE_".length()), pValue);
      else
        optionsMap.put((String) pKey, pValue);
    });

    return optionsMap;
  }

  /**
   * @param pFile       File whose fileEnding should be checked for conformity with pFileEnding
   * @param pFileEnding the fileEnding the passed file should have
   * @return the File itself if the fileEnding was correct or no fileEnding could be determined, the file with adjusted fileEnding otherwise
   */
  static File adjustFileEnding(@NonNull File pFile, @NonNull String pFileEnding)
  {
    if (!pFile.getAbsolutePath().endsWith("." + pFileEnding))
    {
      String path = pFile.getAbsolutePath();
      if (path.lastIndexOf(".") != -1)
        return new File(path.substring(0, path.lastIndexOf(".")) + "." + pFileEnding);
      else
        return new File(path + "." + pFileEnding);
    }
    return pFile;
  }

  /**
   * Tries to convert the file encoding
   *
   * @param pFile           File to convert
   * @param pTargetEncoding target encoding
   * @return the converted file copy
   */
  @NonNull
  static File adjustFileEncoding(@NonNull File pFile, @NonNull Charset pTargetEncoding)
  {
    Charset current = null;

    try
    {
      File file = FileUtil.normalizeFile(pFile);
      current = FileEncodingQuery.getEncoding(FileUtil.toFileObject(file));
      if(!Objects.equals(current, pTargetEncoding))
      {
        File tmp = Files.createTempFile("adoc_encoding", pFile.getName()).toFile();
        String content = FileUtils.readFileToString(file, current);
        FileUtils.write(tmp, content, pTargetEncoding);
        return tmp;
      }
    }
    catch(Throwable e)
    {
      Logger.getLogger(BaseAsciiDocConverter.class.getName())
          .log(Level.WARNING, "Failed to adjust file encoding from " + current + " to " + pTargetEncoding, e);
    }

    return pFile;
  }

  /**
   * converts the safeMode from string to SafeMode object
   *
   * @param pSafeMode safeMode given as String
   * @return corresponding SafeMode
   */
  private static SafeMode convert(String pSafeMode)
  {
    switch (pSafeMode)
    {
      case "safe":
        return SafeMode.SAFE;
      case "unsafe":
        return SafeMode.UNSAFE;
      case "server":
        return SafeMode.SERVER;
      default:
        return SafeMode.SECURE;
    }
  }

}
