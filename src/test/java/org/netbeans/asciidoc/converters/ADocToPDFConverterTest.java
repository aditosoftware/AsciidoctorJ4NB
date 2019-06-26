package org.netbeans.asciidoc.converters;

import org.junit.Test;

import java.io.*;
import java.nio.file.*;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author m.kaspera, 18.06.2019
 */
public class ADocToPDFConverterTest
{

  /**
   * Test if a document can be converted to PDF and put in a folder in the default temporary folder
   * The converter can only create the file if the "unsafe" option is chosen, all other safety settings lead to no file created and no exception thrown
   *
   * @throws IOException if the temp directory cannot be created
   */
  @Test
  public void testConvertPDFTmpDir() throws IOException
  {
    File tmpFolder;
    Path tempDirectory = Files.createTempDirectory("htmlConversionTest");
    tmpFolder = tempDirectory.toFile();
    tmpFolder.deleteOnExit();
    ADocToPdfConverter pdfConverter = new ADocToPdfConverter();
    File htmlFile = new File(tmpFolder, "testPDF.pdf");
    assertFalse(htmlFile.exists());
    File testFile = new File("src/test/resources/org/netbeans/asciidoc/structure/test_skip_header_levels.adoc");
    pdfConverter.convert(testFile, htmlFile, "adoc", "pdf", Map.of("safe", "unsafe"));
    assertTrue(htmlFile.exists());
    htmlFile.deleteOnExit();
  }

  /**
   * Test if a document can be converted to PDF in the top level directory of wherever the JVM is started from
   */
  @Test
  public void testConvertPDF()
  {
    ADocToPdfConverter aDocToPdfConverter = new ADocToPdfConverter();
    File pdfFile = new File("testPDF.pdf");
    assertFalse(pdfFile.exists());
    File testFile = new File("src/test/resources/org/netbeans/asciidoc/structure/test_skip_header_levels.adoc");
    aDocToPdfConverter.convert(testFile, pdfFile, "adoc", "pdf", Map.of());
    assertTrue(pdfFile.exists());
    pdfFile.deleteOnExit();
  }
}
