package common;

import java.util.List;
import java.util.logging.Logger;
import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import org.datanucleus.jdo.NucleusJDOHelper;

public interface Persistable<T> {

  static final Logger logger = Logger.getLogger("Persitable<T>");

  static final PersistenceManagerFactory pmf =
    JDOHelper.getPersistenceManagerFactory("transactions");

  static final Persistence persister = new Persistence();

  static final class Persistence {

    public static PersistenceManager getPersistenceManager() {
      return pmf.getPersistenceManager();
    }

    /**
     * @return The object of the given class and key or a
     * clazz.newInstance() of the given class if it doesn't exist.
     */
    public static final <T> T findCreate(final Class<T> clazz, final int key) {
      final T obj = get(clazz, key);
      if (obj != null)
        return obj;
      try {
        return clazz.newInstance();
      } catch (Exception e) {
        throw new IllegalArgumentException("Couldn't create new instance of given class: "
                                           + e);
      }
    }

    /**
     * @return The object of the given class and key or null if it
     * doesn't exist.
     */
    public static final <T> T get(final Class<T> clazz, final int key) {
      final PersistenceManager pm = getPersistenceManager();
      final Transaction tx = pm.currentTransaction();
      tx.begin();
      pm.setDetachAllOnCommit(true);
      try {
        final T obj = pm.getObjectById(clazz, key);
        tx.commit();
        return obj;
      } catch (JDOObjectNotFoundException e) {
        // If not found.
        return null;
      } finally {
        if (tx.isActive()) {
          logger.warning("Rolling back get transaction for class type: "+ clazz);
          tx.rollback();
        }
        pm.close();
      }
    }

    /** This method will close the given PersistenceManager. */
    @SuppressWarnings("unchecked")
    public static final <T> List<T> query(final PersistenceManager pm, final Query q) {
      final Transaction tx = pm.currentTransaction();
      tx.begin();
      pm.setDetachAllOnCommit(true);
      try {
        final List<T> items = (List<T>) q.execute();
        tx.commit();
        return items;
      } finally {
        if (tx.isActive()) {
          logger.warning("Rolling back query transaction.");
          tx.rollback();
        }
        pm.close();
      }
    }

    /** @return True iff success. */
    public static final <T> boolean save(final T obj) {
      final PersistenceManager pm = getPersistenceManager();
      final Transaction tx = pm.currentTransaction();
      tx.begin();
      try {
        pm.makePersistent(obj);
        tx.commit();
        return true;
      } finally {
        if (tx.isActive()) {
          logger.warning("Rolling back save transaction for object of type: "+ obj.getClass().getName());
          tx.rollback();
        }
        pm.close();
      }
      return false;
    }
  }
}
