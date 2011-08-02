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

  /**
   * Creates a Format from the given XML template of a document in the
   * target format.
   *
   * @TODO(pmy): Use only the schema instead of the template.
   */
  public static Format formatFromXml(final String name, final String schemaXml) throws SAXException, IOException {
    final LinkedHashMap<String,String> fieldValues = new LinkedHashMap<String,String>();
    final Element root = fromXml(schemaXml, null, fieldValues, false);
    final Format format = new Format(name, root.getAttribute("targetNamespace"));
    format.setSchema(schemaXml);
    return format;
  }

  /**
   * Creates a MultiPartDocument from an XML string using the path to
   * nodes as the field names.  The root node prefix is stripped from
   * path names.
   */
  public static MultiPartDocument docFromXml(final String xml, final String schema) throws SAXException, IOException {
    final LinkedHashMap<String,String> fieldValues = new LinkedHashMap<String,String>();
    final Node root = fromXml(xml, schema, fieldValues, true);
    final MultiPartDocument doc = new MultiPartDocument(root.getLocalName());
    for (String fieldName : fieldValues.keySet()) {
      final String value = fieldValues.get(fieldName);
      fieldName = fieldName.substring(fieldName.indexOf("/", 1) + 1, fieldName.length());
      doc.fields.add(new DocumentField(fieldName, value));
    }
    doc.setXml(xml);
    return doc;
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
   * Converts the given MultiPartDocument of the given Format to an
   * XML encoded string.
   */
  public static String toXml(final MultiPartDocument doc, final Format format) throws TransformerException {
    final LinkedHashMap<String,String> fieldValues = new LinkedHashMap<String,String>();
    for (final DocumentField field : doc.getFields()) {
      fieldValues.put(field.getName(), field.getValue());
    }
    return toXml(format, format.getName(), format.getNamespace(), fieldValues, true);
  }

  /**
   * Converts the given Format to an XML encoded string.
   */
  public static String toXml(final Format format) throws TransformerException {
    final LinkedHashMap<String,String> fieldValues = new LinkedHashMap<String,String>();
    for (final FormField field : format.getFields()) {
      fieldValues.put(field.getName(), null);
    }
    return toXml(format, format.getName(), format.getNamespace(), fieldValues, false);
  }

  /**
   * Actual conversion method for both #toXml(MultiPartDocument,
   * Format) and #toXml(Format).
   */
  static String toXml(final Format format, final String formatName, final String formatNamespace,
                      final LinkedHashMap<String,String> fieldValues, final boolean emitValues)
    throws TransformerException {
    final Document doc = getBuilder(emitValues ? format.getSchema() : null).getDOMImplementation().createDocument(formatNamespace,
                                                                                                                  formatName, null);

    final Stack<Node> nodeStack = new Stack<Node>();
    nodeStack.push(doc.getDocumentElement());

    for (final String fieldName : fieldValues.keySet()) {
      logger.fine("Placing field: %s"+ fieldName);
      // Note, split on a string that starts with the split character
      // yields an empty first array element, so handling separately.
      final String [] fieldPath = fieldName.startsWith("/") ? fieldName.substring(1).split("/") : fieldName.split("/");

      // First unroll the stack if new field isn't a child of previous field.
      int i = 0;
      for (;i < fieldPath.length && i < nodeStack.size(); i++) {
        final String nodeName = nodeStack.get(i).getNodeName();
        final String fieldPart = fieldPath[i];
        logger.fine(String.format("Comparing: %s, %s\n", nodeName, fieldPart));
        if (!nodeName.equals(fieldPart)) {
          logger.fine("New path found in last compare, popping until match.");
          // Pop until last match but leave root node for the case
          // that fields are not absolute paths.
          while (nodeStack.size() > i && nodeStack.size() > 1) {
            final Node n = nodeStack.pop();
          }
          break;
        }
        logger.fine("nodeStack[i] matches: "+ fieldPath[i]);
      }

      // Push what's left onto the stack as new nodes.
      Element elt = null;
      for (; i < fieldPath.length; i++) {
        elt = doc.createElement(fieldPath[i]);
        nodeStack.peek().appendChild(elt);
        nodeStack.add(elt);
        logger.fine("added to stack. stack now: "+ nodeStack);
      }
      final String value = fieldValues.get(fieldName);
      if (emitValues && elt != null && value != null)
        elt.appendChild(doc.createTextNode(value));
    }
    return toXml(doc);
  }

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

  // TODO(pmy): it may be possible to have a jsp output escaped HTML.
  public static String toFindForm(final Format format, final String hostName, final List<FormField> fields)
    throws TransformerException {
    String html = String.format("<form action=\"%s/wiki/%s\" method=\"GET\">\n", hostName, format.getName());
    for (final FormField field : fields) {
      html += String.format("  %s: <input name=\"%s\"><br/>\n",
                            field.getText(), field.getName());
    }
    html += "  <input type=\"Submit\"><input type=\"Reset\">\n";
    html += "  <input name=\"q\" type=\"hidden\">\n";
    html += "</form>\n";
    return html;
  }

  public static String toCreateForm(final Format format, final String hostName, final List<FormField> fields)
    throws TransformerException {
    String html = String.format("<form action=\"%s/wiki/%s\" method=\"POST\" enctype=\"multipart/form-data\">\n",
                                hostName, format.getName());
    for (final FormField field : fields) {
      html += String.format("  %s: <input name=\"%s\"><br/>\n",
                            field.getText(), field.getName());
    }
    html += "  <input type=\"Submit\"><input type=\"Reset\">\n";
    html += "</form>\n";
    return html;
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
