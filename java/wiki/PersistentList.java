package wiki;

import java.util.AbstractList;
import java.util.List;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

public class PersistentList<T> extends AbstractList<T> implements Persistable<T> {

  Class<T> clazz;

  PersistentList(final Class<T> clazz) {
    this.clazz = clazz;
  }

  // Collection
  @Override
  public boolean add(final T o) {
    persister.save(o);
    return true; // TODO(pmy): ?
  }
  
  @Override
  public int size() {
    return asList().size();
  }

  // List
  @Override
  public T get(final int id) {
    return persister.get(clazz, id + 1);
  }

  public List<T> asList() {
    final PersistenceManager pm = getPersistenceManager();
    // Callee closes pm.
    return query(pm, pm.newQuery(clazz));
  }

  public List<T> query(final String q) {
    final PersistenceManager pm = getPersistenceManager();
    final Query query = pm.newQuery(clazz, q);
    // Callee closes pm.
    return query(pm, query);
  }

  public List<T> queryWithVariables(final String q, final String varDecl) {
    final PersistenceManager pm = getPersistenceManager();
    final Query query = pm.newQuery(clazz, q);
    query.declareVariables(varDecl);
    // Callee closes pm.
    return query(pm, query);
  }

  // Wrapper to persister below here.
  /**
   * Method to hide this.persister.getPersistenceManager(...);
   * Caller must close the PersistenceManager.
   */
  public PersistenceManager getPersistenceManager() {
    return persister.getPersistenceManager();
  }

  /**
   * Method to hide this.persister.save(...);
   * Caller must close the PersistenceManager.
   */
  public void save(final T obj) {
    persister.save(obj);
  }

  /**
   * Method to hide this.persister.query(...);
   * Will close the given PersistenceManager.
   */
  public List<T> query(final PersistenceManager pm, final Query q) {
    return persister.query(pm, q);
  }

  /** Method to hide this.persister.findCreate(...); */
  public static <T> T findCreate(final Class<T> c, final int key) {
    return persister.findCreate(c, key);
  }
}