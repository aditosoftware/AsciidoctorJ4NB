package org.netbeans.asciidoc.images;

import com.google.common.base.Charsets;
import de.adito.notification.INotificationFacade;
import org.apache.commons.io.*;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.*;
import org.jdom2.output.support.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.io.*;
import java.nio.file.Files;
import java.util.*;

/**
 * Executor for copying the image, creating the changelog if not available, adding changeset to changelog and adding the adoc-line to the editor pane.
 *
 * @author s.seemann, 07.10.2021
 */
public class AddUserhelpImageExecutor
{
  private static final String _TEMPLATE_CHANGELOG;
  static final String CHANGELOG_XML = "changelog.xml";

  private final UserhelpImageContainer container;
  private final JEditorPane pane;

  static
  {
    try
    {
      _TEMPLATE_CHANGELOG = IOUtils.toString(AddUserhelpImageExecutor.class.getResource("changelog_template.xml"), Charsets.UTF_8);
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }

  public AddUserhelpImageExecutor(@NotNull UserhelpImageContainer pContainer, @Nullable JEditorPane pPane)
  {
    container = pContainer;
    pane = pPane;
  }

  public void process() throws IOException
  {
    File targetDirectory = container.getTargetDirectory();
    //noinspection ResultOfMethodCallIgnored
    targetDirectory.mkdirs();

    _processChangelog(targetDirectory);
    _processImage(targetDirectory);
    _insertIntoAdoc();
  }

  /**
   * Inserts {@link UserhelpImageContainer#getAdocString()} into the editor pane at caret position.
   */
  private void _insertIntoAdoc()
  {
    if (pane == null)
      return;

    try
    {
      // insert string into the pane
      pane.getDocument().insertString(pane.getCaretPosition(), container.getAdocString(), null);
    }
    catch (BadLocationException pE)
    {
      // Try it at the beginning of the document
      try
      {
        pane.getDocument().insertString(0, container.getAdocString(), null);
      }
      catch (BadLocationException pE2)
      {
        // Now give up and print stacktrace
        INotificationFacade.INSTANCE.error(pE2);
      }
    }
  }

  /**
   * Copy the source image into the sub folder {@link UserhelpImageContainer#IMAGES_TARGET_DIRECTORY} of the target folder
   *
   * @param pTargetFolder target folder
   */
  private void _processImage(@NotNull File pTargetFolder) throws IOException
  {
    File targetImageFolder = new File(pTargetFolder, UserhelpImageContainer.IMAGES_TARGET_DIRECTORY);
    FileUtils.copyFile(container.getSourceImage(), new File(targetImageFolder, container.getTargetImageName()), true);
  }

  /**
   * Creates the changelog (if not available) and adds the changeset {@link UserhelpImageContainer#getChangeSet()}.
   *
   * @param pTargetFolder the folder, where the changelog is located
   */
  private void _processChangelog(@NotNull File pTargetFolder) throws IOException
  {
    File changelogFile = new File(pTargetFolder, CHANGELOG_XML);
    if (!changelogFile.exists())
    {
      //noinspection ResultOfMethodCallIgnored
      changelogFile.createNewFile();
      Files.write(changelogFile.toPath(), List.of(_TEMPLATE_CHANGELOG));
    }

    try
    {
      // add changeset to the changelog
      SAXBuilder builder = new SAXBuilder();
      Document doc = builder.build(changelogFile);
      doc.getRootElement().addContent(container.getChangeSet());

      XMLOutputter out = new XMLOutputter(new AbstractXMLOutputProcessor()
      {
        // Remove empty namespaces, they should not be printed into the changelog file
        @Override
        protected void printNamespace(Writer out, FormatStack fstack, Namespace ns) throws IOException
        {
          if (!ns.getPrefix().equals("") || !ns.getURI().equals(""))
            super.printNamespace(out, fstack, ns);
        }
      });

      // Write it back to the file
      out.setFormat(Format.getPrettyFormat());
      out.output(doc, new FileWriter(changelogFile));
    }
    catch (JDOMException pE)
    {
      INotificationFacade.INSTANCE.error(pE);
    }
  }
}
