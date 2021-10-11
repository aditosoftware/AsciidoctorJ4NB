package org.netbeans.asciidoc.images;

import org.jdom2.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.net.URLConnection;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Container for all relevant information about an userhelp image
 *
 * @author s.seemann, 07.10.2021
 */
public class UserhelpImageContainer
{
  public static final String DEFAULT_APPENDIX_IMAGE_PATH = File.separator + "userhelp" + File.separator + ".liquibase" + File.separator + "generated";
  public static final String IMAGES_TARGET_DIRECTORY = "images";

  private final String author;
  private final File sourceImage;
  private final File targetDirectory;
  private final String targetImageName;
  private final String idFile;
  private Date changesetDate;
  private String changesetId;
  private String alternativeText;

  public UserhelpImageContainer(@NotNull String pAuthor, @NotNull File pSourceImage, @NotNull File pTargetDirectory, @NotNull String pTargetImageName,
                                @Nullable String pAlternativeText)
  {
    author = pAuthor;
    sourceImage = pSourceImage;
    targetDirectory = pTargetDirectory;
    targetImageName = pTargetImageName;
    alternativeText = pAlternativeText;
    if (alternativeText == null)
      alternativeText = "";
    idFile = UUID.randomUUID().toString();
  }

  public String getAuthor()
  {
    return author;
  }

  public File getSourceImage()
  {
    return sourceImage;
  }

  public File getTargetDirectory()
  {
    return targetDirectory;
  }

  public String getTargetImageName()
  {
    return targetImageName;
  }

  /**
   * @return the id of the file
   */
  String getIdFile()
  {
    return idFile;
  }

  /**
   * @return value of "DATE_NEW" from the generated liquibase changeset
   */
  String getChangesetDate()
  {
    if (changesetDate == null)
      changesetDate = new Date();
    return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(changesetDate);
  }

  /**
   * @return id of the generated changeset
   */
  public String getChangesetId()
  {
    if (changesetId == null)
      changesetId = UUID.randomUUID().toString();
    return changesetId;
  }

  /**
   * @return liquibase changeset for inserting an userhelp image
   */
  public Element getChangeSet()
  {
    Element root = new Element("changeSet", Namespace.NO_NAMESPACE);
    root.setAttribute("author", getAuthor());
    root.setAttribute("id", getChangesetId());

    Element insert = new Element("insert");

    insert.setAttribute("tableName", "ASYS_BINARIES");
    insert.addContent(new Element("column").setAttribute("name", "CONTAINERNAME").setAttribute("value", "DOCUMENT"));
    insert.addContent(new Element("column").setAttribute("name", "USER_NEW").setAttribute("value", "userhelp"));
    insert.addContent(new Element("column").setAttribute("name", "DATE_NEW")
                          .setAttribute("valueDate", getChangesetDate()));
    insert.addContent(new Element("column").setAttribute("name", "USER_NEW").setAttribute("value", "userhelp"));
    insert.addContent(new Element("column").setAttribute("name", "MIMETYPE")
                          .setAttribute("value", URLConnection.guessContentTypeFromName(sourceImage.getName())));
    insert.addContent(new Element("column").setAttribute("name", "ID").setAttribute("value", idFile));

    String size = "0";
    try
    {
      size = Long.toString(Files.size(sourceImage.toPath()));
    }
    catch (IOException pE)
    {
      // use fallback
    }
    insert.addContent(new Element("column").setAttribute("name", "DATASIZE").setAttribute("value", size));
    insert.addContent(new Element("column").setAttribute("name", "TABLENAME").setAttribute("value", "USERHELP"));
    insert.addContent(new Element("column").setAttribute("name", "FILENAME").setAttribute("value", idFile));
    insert.addContent(new Element("column").setAttribute("name", "BINDATA")
                          .setAttribute("value", IMAGES_TARGET_DIRECTORY + "/" + targetImageName));
    insert.addContent(new Element("column").setAttribute("name", "PREVIEW")
                          .setAttribute("value", IMAGES_TARGET_DIRECTORY + "/" + targetImageName));

    root.addContent(insert);
    return root;
  }

  /**
   * @return the string inserted into the adoc file for referencing a userhelp image
   */
  public String getAdocString()
  {
    return "image::/client/binary?id=" + idFile + "[" + alternativeText + ", 100%, 100%]";
  }
}
