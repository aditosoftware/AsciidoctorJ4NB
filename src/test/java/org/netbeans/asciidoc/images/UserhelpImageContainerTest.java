package org.netbeans.asciidoc.images;

import org.jdom2.output.XMLOutputter;
import org.junit.Test;

import java.io.*;
import java.nio.file.*;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * @author s.seemann, 08.10.2021
 */
public class UserhelpImageContainerTest
{

  @Test
  public void shouldProperlyCreateAdocString()
  {
    String alternativeText = "Alternative Text blabla";
    UserhelpImageContainer container = new UserhelpImageContainer("Author", new File(""), new File(""), "", alternativeText);

    String id = container.getIdFile();

    // check if it is an uuid
    UUID uuid = UUID.fromString(id);

    String expected = "image::/client/binary?id=" + uuid.toString() + "[" + alternativeText + ", 100%, 100%]";
    assertEquals(expected, container.getAdocString());
  }

  @Test
  public void shouldProperlyCreateChangeset() throws IOException
  {
    Path targetFile = Files.createTempFile("temp", ".png");
    String targetFileName = targetFile.getFileName().toString();
    UserhelpImageContainer container = new UserhelpImageContainer("Author", new File("abc.png"), targetFile.getParent().toFile(), targetFileName, "Text");


    String expected = "<changeSet author=\"Author\" id=\"" + container.getChangesetId() + "\">" +
        "<insert tableName=\"ASYS_BINARIES\">" +
        "<column name=\"CONTAINERNAME\" value=\"DOCUMENT\" />" +
        "<column name=\"USER_NEW\" value=\"userhelp\" />" +
        "<column name=\"DATE_NEW\" valueDate=\"" + container.getChangesetDate() + "\" />" +
        "<column name=\"MIMETYPE\" value=\"image/png\" />" +
        "<column name=\"ID\" value=\"" + container.getIdFile() + "\" />" +
        "<column name=\"DATASIZE\" value=\"0\" />" +
        "<column name=\"TABLENAME\" value=\"USERHELP\" />" +
        "<column name=\"FILENAME\" value=\"" + container.getIdFile() + "\" />" +
        "<column name=\"BINDATA\" valueBlobFile=\"" + UserhelpImageContainer.IMAGES_TARGET_DIRECTORY + "/" + targetFileName + "\" />" +
        "<column name=\"PREVIEW\" valueBlobFile=\"" + UserhelpImageContainer.IMAGES_TARGET_DIRECTORY + "/" + targetFileName + "\" />" +
        "</insert></changeSet>";

    assertEquals(expected, new XMLOutputter().outputString(container.getChangeSet()));
  }
}