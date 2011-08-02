package wiki;

import junit.framework.TestCase;

public class PolygonTest extends TestCase {

  public PolygonTest() {
    super(PolygonTest.class.getName());
  }

  public void testInvalidBox() {
    failHelper("asdf");
  }

  public void testValidBox1() {
    helper("50,0&#13;\n50,50\n100,25\n0,50\n50,0");
  }

  public void testValidBox2() {
    helper("50,0\n22.784349,15\n100,25\n0,50\n50,0");
  }

  void failHelper(final String lineString) {
    try {
      new Polygon(lineString);
      fail("Succeeded parsing invalid line string.");
    } catch (Exception e) {
      // expected;
    }
  }

  Polygon helper(final String lineString) {
    try {
      return new Polygon(lineString);
    } catch (Exception e) {
      fail("Failed to parse valid line string.");
    }
    return null;
  }

  public static void main(final String [] args) {
    junit.textui.TestRunner.run(PolygonTest.class);
  }
}
