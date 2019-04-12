package org.netbeans.asciidoc.converters;

import org.junit.Test;

import java.io.*;
import java.nio.file.*;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author m.kaspera, 12.04.2019
 */
public class ADocToHTMLConverterTest
{

  /**
   * Test if a document can be converted to HTML and put in a folder in the default temporary folder
   * The converter can only create the file if the "unsafe" option is chosen, all other safety settings lead to no file created and no exception thrown
   *
   * @throws IOException if the temp directory cannot be created
   */
  @Test
  public void testConvertHTMLTmpDir() throws IOException
  {
    File tmpFolder;
    Path tempDirectory = Files.createTempDirectory("htmlConversionTest");
    tmpFolder = tempDirectory.toFile();
    tmpFolder.deleteOnExit();
    ADocToHTMLConverter htmlConverter = new ADocToHTMLConverter();
    File htmlFile = new File(tmpFolder, "testHTML.html");
    assertFalse(htmlFile.exists());
    File testFile = new File("src/test/resources/org/netbeans/asciidoc/structure/test_skip_header_levels.adoc");
    htmlConverter.convert(testFile, htmlFile, "adoc", "html", Map.of("safe", "unsafe"));
    assertTrue(htmlFile.exists());
    htmlFile.deleteOnExit();
  }

  /**
   * Test if a document can be converted to HTML in the top level directory of wherever the JVM is started from
   */
  @Test
  public void testConvertHTML()
  {
    ADocToHTMLConverter htmlConverter = new ADocToHTMLConverter();
    File htmlFile = new File("testHTML.html");
    assertFalse(htmlFile.exists());
    File testFile = new File("src/test/resources/org/netbeans/asciidoc/structure/test_skip_header_levels.adoc");
    htmlConverter.convert(testFile, htmlFile, "adoc", "html", Map.of());
    assertTrue(htmlFile.exists());
    htmlFile.deleteOnExit();
  }

}
