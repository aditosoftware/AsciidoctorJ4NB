package org.netbeans.asciidoc.images;

import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * @author s.seemann, 08.10.2021
 */
public class AddUserhelpImageExecutorTest
{
  @Test
  public void shouldCreateChangelogAndCopyImage() throws IOException
  {
    // setting up demo data
    String author = "author";
    File sourceFile = Files.createTempFile("image", ".png").toFile();
    File targetDir = Files.createTempDirectory("target").toFile();
    String targetImageName = "targetImage.png";
    String alternativeText = "alternativ";

    // process everything
    UserhelpImageContainer container = new UserhelpImageContainer(author, sourceFile, targetDir, targetImageName, alternativeText);
    new AddUserhelpImageExecutor(container, null).process();

    // check if copy was successful
    File targetImage = new File(targetDir, UserhelpImageContainer.IMAGES_TARGET_DIRECTORY + File.separator + targetImageName);
    assertTrue(targetImage.exists());

    // check if changelog was created
    File changelog = new File(targetDir, AddUserhelpImageExecutor.CHANGELOG_XML);
    assertTrue(changelog.exists());

    // check content of changelog
    String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<databaseChangeLog xmlns=\"http://www.liquibase.org/xml/ns/dbchangelog\" xmlns:ext=\"http://www.liquibase.org/xml/ns/dbchangelog-ext\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.liquibase.org/xml/ns/dbchangelog-ext http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-ext.xsd http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.6.xsd\">\n" +
        "  <changeSet author=\"" + author + "\" id=\"" + container.getChangesetId() + "\">\n" +
        "    <insert tableName=\"ASYS_BINARIES\">\n" +
        "      <column name=\"CONTAINERNAME\" value=\"DOCUMENT\" />\n" +
        "      <column name=\"USER_NEW\" value=\"userhelp\" />\n" +
        "      <column name=\"DATE_NEW\" valueDate=\"" + container.getChangesetDate() + "\" />\n" +
        "      <column name=\"MIMETYPE\" value=\"image/png\" />\n" +
        "      <column name=\"ID\" value=\"" + container.getIdFile() + "\" />\n" +
        "      <column name=\"DATASIZE\" value=\"0\" />\n" +
        "      <column name=\"TABLENAME\" value=\"USERHELP\" />\n" +
        "      <column name=\"FILENAME\" value=\"" + container.getIdFile() + "\" />\n" +
        "      <column name=\"BINDATA\" valueBlobFile=\"" + UserhelpImageContainer.IMAGES_TARGET_DIRECTORY + "/" + targetImageName + "\" />\n" +
        "      <column name=\"PREVIEW\" valueBlobFile=\"" + UserhelpImageContainer.IMAGES_TARGET_DIRECTORY + "/" + targetImageName + "\" />\n" +
        "    </insert>\n" +
        "  </changeSet>\n" +
        "</databaseChangeLog>";

    assertEquals(expected, Files.lines(changelog.toPath()).collect(Collectors.joining("\n")));
  }
}