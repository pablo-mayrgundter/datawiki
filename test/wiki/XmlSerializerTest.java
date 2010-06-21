package wiki;

import junit.framework.TestCase;

public class XmlSerializerTest extends TestCase {

  static final String FORMAT_NAME = "test";
  static final String FORMAT_NS = "format:test";
  static final String XML_STUB =
    "<"+ FORMAT_NAME +" xmlns=\""
    + FORMAT_NS + "\">\n  <node/>\n</"
    + FORMAT_NAME +">\n";
  static final String XML_STUB_VALUE =
    "<"+ FORMAT_NAME +" xmlns=\""
    + FORMAT_NS + "\">\n  <node>\n    <foo>bar</foo>\n  </node>\n</"
    + FORMAT_NAME +">\n";

  public XmlSerializerTest() {
    super(XmlSerializerTest.class.getName());
  }

  public void testFormatFromAndToXml() throws Exception {
    Format format = XmlSerializer.formatFromXml(new java.io.StringBufferInputStream(XML_STUB));
    String xml = XmlSerializer.toXml(format);
    assertEquals(XML_STUB, xml);
  }

  public void testMPDFromAndToXml() throws Exception {
    Format format = XmlSerializer.formatFromXml(new java.io.StringBufferInputStream(XML_STUB_VALUE));
    MultiPartDocument doc = XmlSerializer.docFromXml(new java.io.StringBufferInputStream(XML_STUB_VALUE));
    String xml = XmlSerializer.toXml(doc, format);
    assertEquals(XML_STUB_VALUE, xml);
  }

  public void testFlatXML() throws Exception {
    String flat = "<test xmlns=\"a:b\">\n  <a>b</a>\n</test>\n";
    Format format = XmlSerializer.formatFromXml(new java.io.StringBufferInputStream(flat));
    MultiPartDocument doc = XmlSerializer.docFromXml(new java.io.StringBufferInputStream(flat));
    String xml = XmlSerializer.toXml(doc, format);
    assertEquals(flat, xml);
  }

  public void testLinearMPDToXml() throws Exception {
    // MPD fields may be of the form a, or /a, or /a/a or ...
    Format format = new Format("test", "uri:test");
    MultiPartDocument doc = new MultiPartDocument("test");
    doc.fields.add(new DocumentField("attr1", "val1"));
    doc.fields.add(new DocumentField("attr2", "val2"));
    assertEquals("<test xmlns=\"uri:test\">\n  <attr1>val1</attr1>\n  <attr2>val2</attr2>\n</test>\n",
                 XmlSerializer.toXml(doc, format));
  }

  public static void main(final String [] args) {
    junit.textui.TestRunner.run(XmlSerializerTest.class);
  }
}
