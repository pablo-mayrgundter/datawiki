package wiki;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/**
 * Properties is a class for accessing application properties
 * configured and stored by the GAE administrative interface.
 *
 * @author "Pablo Mayrgundter" <pmy@google.com>
 */
public class Properties {

  static final String propsKeyType = "properties";
  static final String propsKeyName = "application";
  static final Key storedPropsKey = Datastore.key(propsKeyType, propsKeyName);

  static {
    if (Datastore.get(storedPropsKey) == null) {
      Datastore.put(new Entity(storedPropsKey));
    }
  }

  public static boolean hasProperty(final String name) {
    return props().hasProperty(name);
  }

  public static String getProperty(final String name) {
    return (String) props().getProperty(name);
  }

  public static boolean getBoolean(final String name) {
    return Boolean.parseBoolean((String) props().getProperty(name));
  }

  public static void setProperty(final String name, final String value) {
    Entity props = props();
    props.setProperty(name, value);
    Datastore.put(props);
  }

  static Entity props() {
    return Datastore.get(storedPropsKey);
  }
}
