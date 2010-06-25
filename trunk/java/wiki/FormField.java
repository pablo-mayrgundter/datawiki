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

  enum Type {
    String, Latitude, Longitude
  }

  final Type type;
  String text;
  String name;
  Text value;

  /**
   * @throws IllegalArgumentException if the given field name cannot
   * be used as an XML tag name.
   */
  public FormField(final String text, final String name, final String value) {
    this(text, name, value, Type.String);
  }

  /**
   * @throws IllegalArgumentException if the given field name cannot
   * be used as an XML tag name.
   */
  public FormField(final String text, final String name, final String value, final Type type) {
    this.text = text;
    if (!Util.safeForXmlTag(name)) {
      throw new IllegalArgumentException(String.format("The given field name '%s' cannot be used.  It must use only these characters: %s", name, Util.XML_SAFE_CHARS));
    }
    this.name = name;
    this.value = new Text(value);
    this.type = type;
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

  public Type getType() {
    return type;
  }

  public String toString() {
    return String.format("%s@%d:{text=\"%s\",name=\"%s\",value=\"%s\"}",
                         FormField.class.getName(), System.identityHashCode(this),
                         text, name, value.getValue());
  }
}
