package wiki;

import junit.framework.TestSuite;

/**
 * This is a convenient target for the build system to invoke all of
 * the unit tests.
 *
 * @author Pablo Mayrgundter
 */
public class AllTests {

  /**
   * Creates a suite of all unit tests in this package and
   * sub-packages.
   */
  public static TestSuite suite() {
    final TestSuite suite = new TestSuite();
    suite.addTestSuite(PolygonTest.class);
    suite.addTestSuite(UtilTest.class);
    suite.addTestSuite(XmlSerializerTest.class);
    return suite;
  }

  /**
   * Runnable as:
   *
   *   java wiki.AllTests
   */
  public static void main(final String [] args) {
    junit.textui.TestRunner.run(suite());
  }
}
