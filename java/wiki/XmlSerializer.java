package wiki;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.BufferedWriter;
import java.util.List;
import java.util.Stack;
import java.util.LinkedHashMap;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * The XmlSerializer class implements the XML-to-Format conversion
 * operations described at:
 *
 *   http://code.google.com/p/datawiki/wiki/XMLSerialization
 *
 * @author "Pablo Mayrgundter" <pmy@google.com>
 */
public class XmlSerializer {

  static final Logger logger = Logger.getLogger(XmlSerializer.class.getName());

  static String toXml(final Document doc) throws TransformerException {
    final StringWriter outputWriter = new StringWriter();
    try {
      // http://forums.sun.com/thread.jspa?forumID=34&threadID=562510
      final TransformerFactory tf = TransformerFactory.newInstance();
      try {
        tf.setAttribute("indent-number", "2");
      } catch (Exception e) {}
      final Transformer trans = tf.newTransformer();
      trans.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
      trans.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
      trans.transform(new DOMSource(doc), new StreamResult(new BufferedWriter(outputWriter)));
    } catch (TransformerConfigurationException e) {
      // Shouldn't happen.
      logger.severe("Can't get XML Transformer: "+ e);
      throw new RuntimeException("Cannot initialize XML subsystem.");
    }
    return outputWriter.toString();
  }

  /**
   * Intermediate helper for creating a MultiPartDocument from an
   * InputStream of XML.  Called by #docFromXml(InputStream).
   */
  static Element fromXml(final String xml,
                         final String schema,
                         final LinkedHashMap<String,String> fieldValues,
                         final boolean includeValues) throws SAXParseException, SAXException, IOException {
    final Document doc = getBuilder(schema).parse(new java.io.ByteArrayInputStream(xml.getBytes()));
    final Element root = doc.getDocumentElement();
    fromXml(fieldValues, includeValues, root, "/");
    return root;
  }

  /**
   * Depth-first recursive traversal of curElt to accumulate
   * name/value fields where the name is the XML path.
   */
  static void fromXml(final LinkedHashMap<String,String> fieldValues, final boolean includeValues,
                      final Element curElt, final String path) {
    // TODO(pmy): the method of constructing names from paths isn't
    // great.
    final NodeList childNodes = curElt.getChildNodes();
    final String curNodePath = path + curElt.getLocalName();
    if (childNodes.getLength() == 0) {
      fieldValues.put(curNodePath, "");
    }
    for (int i = 0; i < childNodes.getLength(); i++) {
      final Node n = childNodes.item(i);
      if (n.getNodeType() == Node.ELEMENT_NODE) {
        fromXml(fieldValues, includeValues, (Element)n, curNodePath + "/");
      } else if (n.getNodeType() == Node.TEXT_NODE) {
        // TODO(pmy): newlines turning into lots of whitespace.  Trimming should be correct?
        final String value = n.getNodeValue().trim();
        if (value.trim().length() > 0) {
          fieldValues.put(curNodePath, includeValues ? value : "");
        }
      }
    }
  }

  /**
   * A path is converted to a field name by dropping the leading
   * "/root/"... prefix.
   */
  static String pathToName(final String path) {
    if (path.length() <= 2 || path.indexOf('/') != 0 || path.indexOf('/', 1) <= 1) {
      throw new IllegalArgumentException("Path must begin with slash and contain"
                                         + " a non-empty root element name"
                                         + " between two slash characters.");
    }
    return path.substring(path.indexOf("/", 1) + 1);
  }

  static DocumentBuilder getBuilder() {
    return getBuilder(null);
  }

  static DocumentBuilder getBuilder(final String schema) {
    final DocumentBuilderFactory factory =  DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    if (schema != null) {
      factory.setValidating(true);
      factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                           "http://www.w3.org/2001/XMLSchema");
      factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource",
                           new java.io.ByteArrayInputStream(schema.getBytes()));
    }
    try {
      return factory.newDocumentBuilder();
    } catch(ParserConfigurationException e) {
      throw new Error("Cannot configure XML document builder: "+ e);
    }
  }
  /*
  public static void main(final String [] args) throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte [] buf = new byte[10];
    int len;
    while ((len = System.in.read(buf)) != -1) {
      baos.write(buf, 0, len);
    }
    final Format format = formatFromXml(new String(baos.toByteArray()));
    System.out.println(toXml(format));

    final MultiPartDocument doc = docFromXml(new String(baos.toByteArray()));
    System.out.println(toXml(doc, format));
  }
  */
}
