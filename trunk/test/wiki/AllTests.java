package wiki;

import junit.framework.TestSuite;

public class AllTests {

  public static TestSuite suite () {
    final TestSuite suite = new TestSuite();
    suite.addTestSuite(MappedListIteratorTest.class);
    suite.addTestSuite(UtilTest.class);
    suite.addTestSuite(XmlSerializerTest.class);
    return suite;
  }

  public static void main (final String [] args) {
    junit.textui.TestRunner.run(suite());
  }
}
