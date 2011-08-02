package wiki;

import junit.framework.TestCase;
import javax.servlet.http.HttpServletRequest;
import static org.easymock.EasyMock.*;

public class UtilTest extends TestCase {

  public UtilTest() {
    super(UtilTest.class.getName());
  }

  public void testValidFormatName() {
    final String [] names = {"Name", "Test_Name"};
    for (final String name : names) {
      try {
        assertEquals(name, Util.validFormatName(name));
      } catch (IllegalArgumentException e) {
        fail("String should be accepted: "+ name);
      }
    }
  }

  public void testInvalidFormatName() {
    final String [] names = {"Name*", "Test !Name", "<Name/>"};
    for (final String name : names) {
      try {
        assertEquals(name, Util.validFormatName(name));
        fail("String should not be accepted");
      } catch (IllegalArgumentException e) {
        // Expected.
      }
    }
  }

  public void testGetParameterInt() {
    final HttpServletRequest req = createMock(HttpServletRequest.class);
    expect(req.getParameter("foo")).andReturn("1");
    replay(req);
    assertEquals("Expecting ", 1, Util.getParameter(req, "foo", 1));
    verify(req);
  }

  public static void main(final String [] args) {
    junit.textui.TestRunner.run(UtilTest.class);
  }
}
