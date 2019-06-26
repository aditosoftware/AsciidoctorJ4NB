package org.netbeans.asciidoc.converters;

import org.asciidoctor.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

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

    handleSpecialBehaviours(optionsMap, attributesMap, pParams);

    return optionsMap;
  }

  /**
   * @param pFile       File whose fileEnding should be checked for conformity with pFileEnding
   * @param pFileEnding the fileEnding the passed file should have
   * @return the File itself if the fileEnding was correct or no fileEnding could be determined, the file with adjusted fileEnding otherwise
   */
  File adjustFileEnding(@NotNull File pFile, @NotNull String pFileEnding)
  {
    if (!pFile.getAbsolutePath().endsWith("." + pFileEnding))
    {
      String path = pFile.getAbsolutePath();
      if (path.lastIndexOf(".") != -1)
      {
        return new File(path.substring(0, path.lastIndexOf(".")) + "." + pFileEnding);
      }
    }
    return pFile;
  }

  /**
   * Handles all special behaviours, if an attribute or option is set in params
   *
   * @param pOptions    Options
   * @param pAttributes Attributes
   * @param pParams     User Params
   */
  protected void handleSpecialBehaviours(Map<String, Object> pOptions, Map<Object, Object> pAttributes, Map<Object, Object> pParams)
  {
  }

  /**
   * converts the safeMode from string to SafeMode object
   *
   * @param pSafeMode safeMode given as String
   * @return corresponding SafeMode
   */
  private SafeMode convert(String pSafeMode)
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
