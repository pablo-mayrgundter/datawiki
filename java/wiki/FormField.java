package wiki;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;
import java.io.Serializable;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

// TODO(pmy): FormField should subclass DocumentField if possible.
/**
 * The FormField represents an individual HTML form field.
 *
 * @author Pablo Mayrgundter
 */
public class FormField implements Serializable {

  private static final long serialVersionUID = 2765740961462495537L;

  String text;
  String name;
  Text value;

  public FormField(final String text, final String name, final String value) {
    this.text = text;
    this.name = name;
    this.value = new Text(value);
  }

  public String getText() {
    return text;
  }

  public String getName() {
    return name;
  }

  public void setText(final String text) {
    this.text = text;
  }

  public String getValue() {
    return value.getValue();
  }

  public String toString() {
    return String.format("%s@%d:{text=\"%s\",name=\"%s\",value=\"%s\"}",
                         FormField.class.getName(), System.identityHashCode(this),
                         text, name, value.getValue());
  }
}
