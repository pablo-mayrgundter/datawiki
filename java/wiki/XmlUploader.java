package wiki;

import java.io.InputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Stack;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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

public class XmlUploader {

  static String toXml(final Format format) throws TransformerException {
    Document doc;
    try {
      final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      doc = factory.newDocumentBuilder().newDocument();
    } catch (ParserConfigurationException e) {
      // Shouldn't happen.
      e.printStackTrace();
      throw new RuntimeException("Cannot initialize XML subsystem.");
    }
    doc.appendChild(doc.createElementNS(format.getNamespace(), format.getName()));
    final Stack<Node> nodeStack = new Stack<Node>();
    nodeStack.push(doc);
    String [] lastPath = new String[10]; // FIXME
/*
    for (final FormField field : format.fields) {
      final String [] curPath = field.getName().split("\\/");
      int len = 0; // will be equalPrefixLength after loop.
      for (len = 0; i < curPath.length && len < lastPath.length; len++) {
        if (!curPath[len].equals(lastPath[len]))
          break;
      }
      for (int pop = nodeStack.size(); pop > len; pop--)
        nodeStack.pop();
      // Note, split on a string that starts with the split character
      // yields an empty first array element, so the for loop skips
      // the first element.
      for (int p = 1 + len; p < curPath.length; p++) {
        final String part = curPath[p];
        final NodeList childNodes = nodeStack.peek().getChildNodes();
        boolean found = false;
        for (int c = 0; c < childNodes.getLength(); c++) {
          final Node n = childNodes.item(c);
          if (n.getNodeType() == Node.ELEMENT_NODE) {
            if (n.getNodeName().equals(part)) {
              nodeStack.push(n);
              found = true;
              break;
            }
          }
        }
        if (!found) {
          final Element newElt = doc.createElement(part);
          nodeStack.peek().appendChild(newElt);
          nodeStack.add(newElt);
        }
      }
      nodeStack.peek().appendChild(doc.createTextNode(field.getValue()));
    }
*/
    final StringWriter outputWriter = new StringWriter();
    try {
      TransformerFactory.newInstance().newTransformer().transform(new DOMSource(doc),
                                                                  new StreamResult(outputWriter));
    } catch (TransformerConfigurationException e) {
      // Shouldn't happen.
      e.printStackTrace();
      throw new RuntimeException("Cannot initialize XML subsystem.");
    }
    return outputWriter.toString();
  }

  static Format fromXml(final InputStream is) throws SAXException, IOException {
    Document doc;
    try {
      final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      doc = factory.newDocumentBuilder().parse(is);
    } catch (ParserConfigurationException e) {
      // Shouldn't happen.
      e.printStackTrace();
      throw new RuntimeException("Cannot initialize XML subsystem.");
    }
    final Element root = doc.getDocumentElement();
    final Format format = new Format(root.getLocalName(), root.getNamespaceURI(), "");
    parse(format, root, "/");
    return format;
  }

  // TODO(pmy): set to default visability again after dependency removed in TableViz.
  public static String pathToName(final String path) {
    if (path.length() <= 2 || path.indexOf('/') != 0 || path.indexOf('/', 1) <= 1)
      throw new IllegalArgumentException("Path must begin with slash and contain a non-empty root element"
                                         + "between two slash characters.");
    return path.substring(path.indexOf("/", 1) + 1).replaceAll("/", "_");
  }

  // TODO(pmy): the method of constructing names from paths isn't
  // great.
  static void parse(final Format format, final Element curElt, final String path) {
    final NodeList childNodes = curElt.getChildNodes();
    final String curNodePath = path + curElt.getLocalName();
    System.err.println("Cur path: "+ curNodePath);
    if (childNodes.getLength() == 0)
      format.fields.add(new FormField(pathToName(curNodePath), curNodePath, ""));
    for (int i = 0; i < childNodes.getLength(); i++) {
      final Node n = childNodes.item(i);
      if (n.getNodeType() == Node.ELEMENT_NODE)
        parse(format, (Element)n, curNodePath + "/");
      else if (n.getNodeType() == Node.TEXT_NODE) {
        final String value = n.getNodeValue();
        if (value.trim().length() > 0)
          format.fields.add(new FormField(pathToName(curNodePath), curNodePath, ""));
      }
    }
  }

  public static void main(final String [] args) throws Exception {
    final Format format = fromXml(System.in);
    System.out.println(format);
    System.out.println(toXml(format));
  }
}
