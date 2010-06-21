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

public class XmlSerializer {

  static final Logger logger = Logger.getLogger(XmlSerializer.class.getName());

  public static Format formatFromXml(final InputStream is) throws SAXException, IOException {
    final LinkedHashMap<String,String> fieldValues = new LinkedHashMap<String,String>();
    final Node root = fromXml(is, fieldValues, false);
    final Format format = new Format(root.getLocalName(), root.getNamespaceURI());
    for (final String fieldName : fieldValues.keySet()) {
      format.fields.add(new FormField(pathToName(fieldName), fieldName, fieldValues.get(fieldName)));
    }
    return format;
  }

  public static MultiPartDocument docFromXml(final InputStream is) throws SAXException, IOException {
    final LinkedHashMap<String,String> fieldValues = new LinkedHashMap<String,String>();
    final Node root = fromXml(is, fieldValues, true);
    final MultiPartDocument doc = new MultiPartDocument(root.getLocalName());
    for (final String fieldName : fieldValues.keySet()) {
      final String value = fieldValues.get(fieldName);
      doc.fields.add(new DocumentField(fieldName, value));
    }
    return doc;
  }

  static Node fromXml(final InputStream is,
                      final LinkedHashMap<String,String> fieldValues,
                      final boolean includeValues) throws SAXException, IOException {
    Document doc;
    try {
      final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      doc = factory.newDocumentBuilder().parse(is);
    } catch (ParserConfigurationException e) {
      // Shouldn't happen.
      logger.severe("Can't get XML DocumentBuilder: "+ e);
      throw new RuntimeException("Cannot initialize XML subsystem.");
    }
    final Node root = doc.getDocumentElement();
    fromXml(fieldValues, includeValues, doc.getDocumentElement(), "/");
    return root;
  }

  // TODO(pmy): the method of constructing names from paths isn't
  // great.
  static void fromXml(final LinkedHashMap<String,String> fieldValues, final boolean includeValues,
                      final Element curElt, final String path) {
    final NodeList childNodes = curElt.getChildNodes();
    final String curNodePath = path + curElt.getLocalName();
    if (childNodes.getLength() == 0)
      fieldValues.put(curNodePath, "");
    for (int i = 0; i < childNodes.getLength(); i++) {
      final Node n = childNodes.item(i);
      if (n.getNodeType() == Node.ELEMENT_NODE)
        fromXml(fieldValues, includeValues, (Element)n, curNodePath + "/");
      else if (n.getNodeType() == Node.TEXT_NODE) {
        final String value = n.getNodeValue();
        if (value.trim().length() > 0) {
          fieldValues.put(curNodePath, includeValues ? value : "");
        }
      }
    }
  }

  public static String toXml(final Format format) throws TransformerException {
    final LinkedHashMap<String,String> fieldValues = new LinkedHashMap<String,String>();
    for (final FormField field : format.getFields())
      fieldValues.put(field.getName(), null);
    return toXml(format.getName(), format.getNamespace(), fieldValues, false);
  }

  public static String toXml(final MultiPartDocument doc, final Format format) throws TransformerException {
    final LinkedHashMap<String,String> fieldValues = new LinkedHashMap<String,String>();
    for (final DocumentField field : doc.fields) {
      fieldValues.put(field.getName(), field.getValue());
    }
    return toXml(format.getName(), format.getNamespace(), fieldValues, true);
  }

  static String toXml(final String formatName, final String formatNamespace,
                      final LinkedHashMap<String,String> fieldValues, final boolean emitValues)
    throws TransformerException {
    Document doc;
    try {
      final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      doc = factory.newDocumentBuilder().getDOMImplementation().createDocument(formatNamespace, formatName, null);
    } catch (ParserConfigurationException e) {
      // Shouldn't happen.
      logger.severe("Can't get XML DocumentBuilder: "+ e);
      throw new RuntimeException("Cannot initialize XML subsystem.");
    }

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
  public static String toFindForm(final String formatName, final String hostName, final List<FormField> fields)
    throws TransformerException {
    String html = String.format("<form action=\"%s/wiki/documents?format=%s\" method=\"GET\">\n", hostName, formatName);
    for (final FormField field : fields) {
      html += String.format("  %s: <input name=\"%s\"><br/>\n",
                            field.getText(), field.getName());
    }
    html += "  <input type=\"Submit\"><input type=\"Reset\">\n";
    html += "  <input name=\"q\" type=\"hidden\">\n";
    html += "  <input name=\"format\" value=\""+ formatName +"\" type=\"hidden\">\n";
    html += "</form>\n";
    return html;
  }

  public static String toCreateForm(final String formatName, final String hostName, final List<FormField> fields)
    throws TransformerException {
    String html = String.format("<form action=\"%s/wiki/documents?format=%s\" method=\"POST\" enctype=\"multipart/form-data\">\n", hostName, formatName);
    for (final FormField field : fields) {
      html += String.format("  %s: <input name=\"%s\"><br/>\n",
                            field.getText(), field.getName());
    }
    html += "  <input type=\"Submit\"><input type=\"Reset\">\n";
    html += "  <input name=\"format\" value=\""+ formatName +"\" type=\"hidden\">\n";
    html += "</form>\n";
    return html;
  }

  static String pathToName(final String path) {
    if (path.length() <= 2 || path.indexOf('/') != 0 || path.indexOf('/', 1) <= 1)
      throw new IllegalArgumentException("Path must begin with slash and contain a non-empty root element"
                                         + "between two slash characters.");
    return path.substring(path.indexOf("/", 1) + 1).replaceAll("/", "_");
  }

  public static void main(final String [] args) throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte [] buf = new byte[10];
    int len;
    while ((len = System.in.read(buf)) != -1)
      baos.write(buf, 0, len);
    final Format format = formatFromXml(new ByteArrayInputStream(baos.toByteArray()));
    System.out.println(toXml(format));

    final MultiPartDocument doc = docFromXml(new ByteArrayInputStream(baos.toByteArray()));
    System.out.println(toXml(doc, format));
  }
}
