package wiki;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;
import common.Persistable;
import java.io.StringWriter;
import java.util.Date;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

/**
 * The AbstractDocument class provides a storage key and XML conversion
 * methods for AppEngine non-native java types.
 */
@PersistenceCapable(detachable = "true")
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public abstract class AbstractDocument<T> implements Persistable<T> {

  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  @PrimaryKey
  Key key;

  @Persistent
  Date createdDate;

  @Persistent
  Date updatedDate;

  public AbstractDocument() {
    createdDate = updatedDate = new Date();
  }

  public long getId() {
    return key.getId();
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public Date getUpdatedDate() {
    return updatedDate;
  }

  /** Method to hide this.persister.save(...); */
  public void save() {
    updatedDate = new Date(); // TODO(pmy): check if modified.
    persister.save(this);
  }

  /** Method to hide this.persister.findCreate(...); */
  public static <T> T findCreate(final Class<T> c, final int key) {
    return persister.findCreate(c, key);
  }
}
