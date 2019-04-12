package org.netbeans.asciidoc.converters;

import org.asciidoctor.*;
import org.jetbrains.annotations.*;

import java.io.File;
import java.util.Map;

/**
 * Basic class for the Converter classes that has some functions all of them need
 *
 * @author m.kaspera, 08.04.2019
 */
class BaseAsciiDocConverter
{

  /**
   * create a map that contains options and parameters from pOptions and pParams
   *
   * @param pOptions Options object with pre-filled options (such as backend)
   * @param pParams params passed in from outside
   * @return map with both options and params in it
   */
  Map<String, Object> fillOptions(Options pOptions, Map<Object, Object> pParams) {
    Map<String, Object> optionsMap = pOptions.map();
    for (Map.Entry<Object, Object> entry : pParams.entrySet())
    {
      if ("safe".equals(entry.getKey()))
      {
        optionsMap.put((String) entry.getKey(), convert((String) entry.getValue()).getLevel());
      }
      else
      {
        optionsMap.put((String) entry.getKey(), entry.getValue());
      }
    }
    return optionsMap;
  }

  /**
   *
   * @param pFile File whose fileEnding should be checked for conformity with pFileEnding
   * @param pFileEnding the fileEnding the passed file should have
   * @return the File itself if the fileEnding was correct or no fileEnding could be determined, the file with adjusted fileEnding otherwise
   */
  File adjustFileEnding(@NotNull File pFile, @NotNull String pFileEnding) {
    if(!pFile.getAbsolutePath().endsWith("." + pFileEnding)) {
      String path = pFile.getAbsolutePath();
      if(path.lastIndexOf(".") != -1) {
        return new File(path.substring(0, path.lastIndexOf(".")) + "." + pFileEnding);
      }
    }
    return pFile;
  }

  /**
   * converts the safeMode from string to SafeMode object
   *
   * @param pSafeMode safeMode given as String
   * @return corresponding SafeMode
   */
  private SafeMode convert(String pSafeMode) {
    switch (pSafeMode) {
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
