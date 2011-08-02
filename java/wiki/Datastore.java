package wiki;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/**
 * The Datastore class is a utility for accessing the GAE Datastore.
 *
 * @author "Pablo Mayrgundter" <pmy@google.com>
 */
public class Datastore {

  static Key key(final String type, final String name) {
    return KeyFactory.createKey(type, name);
  }

  static Entity get(final String type, final String name) {
    return get(key(type, name));
  }

  static Entity get(final Key key) {
    try {
      return DatastoreServiceFactory.getDatastoreService().get(key);
    } catch (EntityNotFoundException e) {
      return null;
    }
  }

  static void put(final Entity entity) {
    DatastoreServiceFactory.getDatastoreService().put(entity);
  }
}
