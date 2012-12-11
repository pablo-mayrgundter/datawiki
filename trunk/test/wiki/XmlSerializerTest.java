package wiki;

import junit.framework.TestCase;
import java.io.ByteArrayInputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Test serlialization to and from XML.
 *
 * @author Pablo Mayrgundter
 */
public class XmlSerializerTest extends TestCase {

  static final String FORMAT_NAME = "Format_name";
  static final String FORMAT_NS = "http://datawiki.googlelabs.com/wiki/Format_name";

  static String makeTestSchema() {
    final Document schema =
      XmlSerializer.getBuilder().getDOMImplementation().createDocument("http://www.w3.org/2001/XMLSchema",
                                                                       "schema", null);
    final Element root = schema.getDocumentElement();
    root.setAttribute("targetNamespace", FORMAT_NS);
    final Element elt = schema.createElement("element");
    elt.setAttribute("name", FORMAT_NAME);
    //elt.setAttribute("type", "string");
    root.appendChild(elt);
    try {
      return XmlSerializer.toXml(schema);
    } catch(Exception e) {
      // Should not happen.
      throw new RuntimeException(e);
    }
  }

  static String makeTestDoc(final String schema) {
    final Document doc = XmlSerializer.getBuilder(schema)
      .getDOMImplementation().createDocument(FORMAT_NS, FORMAT_NAME, null);
    final Element root = doc.getDocumentElement();
    // root.appendChild(doc.createElement("node"));
    try {
      return XmlSerializer.toXml(doc);
    } catch(Exception e) {
      // Should not happen.
      throw new RuntimeException(e);
    }
  }

  String schema;
  String testXml;

  public XmlSerializerTest() {
    super(XmlSerializerTest.class.getName());
  }

  public void setUp() {
    schema = makeTestSchema();
    testXml = makeTestDoc(schema);
  }

  public void tearDown() {
    schema = testXml = null;
  }

  public static void main(final String [] args) {
    junit.textui.TestRunner.run(XmlSerializerTest.class);
  }
}
