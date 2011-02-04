package wiki;

import com.google.appengine.api.datastore.Text;
import java.util.ArrayList;
import java.util.List;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Extension;

@PersistenceCapable(detachable = "true")
@Inheritance(customStrategy = "complete-table")
public class Format extends MetaDocument<Format> {

  public static String gqlFilterForMatchingFormat(final String otherFormatName) {
    return String.format("name == '%s'", otherFormatName);
  }

  @Persistent
  String namespace;

  @Persistent(serialized="true",defaultFetchGroup="true")
  List<FormField> fields;

  @Persistent
  Text schema;

  /** Used to store administrative flags. */
  @Persistent
  public String flags = null;

  public Format(final String name, final String namespace) {
    this(name, namespace, "");
  }

  public Format(final String name, final String namespace, final String description) {
    this(name, namespace, description, name.replaceAll("_", " "));
  }

  public Format(final String name, final String namespace, final String description, final String title) {
    super(name, title, description);
    this.namespace = Util.validFormatNamepsace(namespace);
    fields = new ArrayList<FormField>();
  }

  public List<FormField> getFields() {
    return fields;
  }

  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(final String namespace) {
    this.namespace = namespace;
  }

  public String getSchema() {
    if (schema == null)
      return null; // TODO(pmy): temporary until schema always present.
    return schema.getValue();
  }

  public void setSchema(final String schema) {
    this.schema = new Text(schema);
  }

  public String toString() {
    return String.format("%s@%d:{name=\"%s\",namespace=\"%s\",fields=%s}",
                         Format.class.getName(), System.identityHashCode(this),
                         name, namespace, fields);
  }
}
