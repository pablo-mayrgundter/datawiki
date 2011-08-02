package wiki;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import junit.framework.TestCase;

public class PropertiesTest extends TestCase {

  final LocalServiceTestHelper helper =
    new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  public PropertiesTest() {
    super(PropertiesTest.class.getName());
  }

  @Override
  public void setUp() {
    helper.setUp();
  }

  @Override
  public void tearDown() {
    helper.tearDown();
  }

  public void test() {
    //assertNotNull("Excpecting appliaction properties to exist.",
    // Properties.getProperty("application.properties"));
  }
}
