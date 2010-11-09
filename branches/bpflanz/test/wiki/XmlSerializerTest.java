package wiki;

import junit.framework.TestCase;
import java.io.ByteArrayInputStream;

/**
 * Test serlialization to and from XML.
 *
 * @author Pablo Mayrgundter
 */
public class XmlSerializerTest extends TestCase {

  static final String FORMAT_NAME = "test";
  static final String FORMAT_NS = "http://datawiki.googlelabs.com/wiki/test";
  static final String SIMPLE_XML_TEMPLATE =
    "<%s xmlns=\"%s\">\n  <node/>\n</%s>\n";
  static final String NESTED_XML_TEMPLATE =
    "<%s xmlns=\"%s\">\n  <node>\n    <foo>bar</foo>\n  </node>\n</%s>\n";
  static final String XML_STUB =
    makeTestXmlDoc(SIMPLE_XML_TEMPLATE, FORMAT_NAME, FORMAT_NS);
  static final String XML_STUB_VALUE =
    makeTestXmlDoc(NESTED_XML_TEMPLATE, FORMAT_NAME, FORMAT_NS);

  static String makeTestXmlDoc(final String body, final String name, final String ns) {
    return String.format(body, name, ns, name);
  }

  public XmlSerializerTest() {
    super(XmlSerializerTest.class.getName());
  }

  public void testFormatFromAndToXml() throws Exception {
    final Format format = 
      XmlSerializer.formatFromXml(new ByteArrayInputStream(XML_STUB.getBytes()));
    final String xml = XmlSerializer.toXml(format);
    assertEquals(XML_STUB, xml);
  }

  public void testMPDFromAndToXml() throws Exception {
    final Format format =
      XmlSerializer.formatFromXml(new ByteArrayInputStream(XML_STUB_VALUE.getBytes()));
    final MultiPartDocument doc =
      XmlSerializer.docFromXml(new ByteArrayInputStream(XML_STUB_VALUE.getBytes()));
    final String xml = XmlSerializer.toXml(doc, format);
    assertEquals(XML_STUB_VALUE, xml);
  }

  public void testFlatXML() throws Exception {
    final String flat = "<test xmlns=\""+ FORMAT_NS +"\">\n  <a>b</a>\n</test>\n";
    final Format format =
      XmlSerializer.formatFromXml(new ByteArrayInputStream(flat.getBytes()));
    final MultiPartDocument doc =
      XmlSerializer.docFromXml(new ByteArrayInputStream(flat.getBytes()));
    final String xml = XmlSerializer.toXml(doc, format);
    assertEquals(flat, xml);
  }

  public void testLinearMPDToXml() throws Exception {
    // MPD fields may be of the form a, or /a, or /a/a or ...
    final Format format = new Format(FORMAT_NAME, FORMAT_NS);
    final MultiPartDocument doc = new MultiPartDocument(FORMAT_NAME);
    doc.fields.add(new DocumentField("attr1", "val1"));
    doc.fields.add(new DocumentField("attr2", "val2"));
    assertEquals(String.format("<test xmlns=\"%s\">\n  <attr1>val1</attr1>\n  <attr2>val2</attr2>\n</test>\n",
                               FORMAT_NS),
                 XmlSerializer.toXml(doc, format));
  }

  public void testTagInjection() throws Exception {
    final String script = "<script>alert('Injection bug')</script>";
    final String badXml = makeTestXmlDoc(SIMPLE_XML_TEMPLATE, script, script);
    try {
      final Format format = XmlSerializer.formatFromXml(new ByteArrayInputStream(badXml.getBytes()));
      fail("Should have rejected script injection in format definition.");
    } catch (Exception e) {
      // Expected.
    }
  }

  public static void main(final String [] args) {
    junit.textui.TestRunner.run(XmlSerializerTest.class);
  }
}
