package wiki;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

/**
 * The DocumentField extends Document with a fieldName, to represent an
 * individual HTML form field.
 *
 * @author Pablo Mayrgundter
 */
@PersistenceCapable(detachable = "true")
public class DocumentField {

  @Persistent
  String name;

  @Persistent
  String value;

  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  @PrimaryKey
  Key key;

  @Persistent
  MultiPartDocument doc;

  public DocumentField() {}

  public DocumentField(final String name, final String value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(final String value) {
    this.value = value;
  }
}
