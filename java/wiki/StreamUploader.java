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

public class StreamUploader {

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
  }
}
