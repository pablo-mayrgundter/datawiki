package wiki;

import static org.easymock.EasyMock.*;
import javax.servlet.http.HttpServletRequest;
import junit.framework.TestCase;

public class UtilTest extends TestCase {

  public UtilTest() {
    super(UtilTest.class.getName());
  }

  public void testIndent() {
    assertEquals("", Util.indent("", 0));
    assertEquals("", Util.indent("", 1));
    assertEquals("a", Util.indent("a", 0));
    assertEquals(" a", Util.indent(" a", 0));
    assertEquals(" a", Util.indent("a", 1));
    assertEquals("  a\n  a", Util.indent("a\na", 2));
    assertEquals("a", Util.indent("a", -1));
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

  // Namespaces.
  public void testGetParentNamespace() {
    assertEquals("/foo/bar/", Util.getParentNamespace("/foo/bar/baz"));
    assertEquals("/foo/bar/", Util.getParentNamespace("/foo/bar/baz/"));
  }

  public void testGetNameFromNamespace() {
    assertEquals("baz", Util.getNameFromNamespace("/foo/bar/baz"));
    assertEquals("baz", Util.getNameFromNamespace("/foo/bar/baz/"));
  }

  public void testCreateNamespace() {
    assertEquals("/foo/bar/baz", Util.createNamespace("/foo/bar/", "baz"));
    assertEquals("/foo/bar/baz", Util.createNamespace("/foo/bar", "baz"));
  }

  // Http params.
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
