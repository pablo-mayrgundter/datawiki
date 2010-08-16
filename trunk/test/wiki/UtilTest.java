package wiki;

import junit.framework.TestCase;

public class UtilTest extends TestCase {

  public UtilTest() {
    super(UtilTest.class.getName());
  }

  public void test() throws Exception {
    assertEquals("", "");
  }

  public static void main(final String [] args) {
    junit.textui.TestRunner.run(UtilTest.class);
  }
}
