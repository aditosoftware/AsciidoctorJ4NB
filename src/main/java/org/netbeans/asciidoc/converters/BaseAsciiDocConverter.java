package org.netbeans.asciidoc.converters;

import org.asciidoctor.*;

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
        optionsMap.put((String) entry.getKey(), convert((String) entry.getValue()));
      }
      else
      {
        optionsMap.put((String) entry.getKey(), entry.getValue());
      }
    }
    return optionsMap;
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
