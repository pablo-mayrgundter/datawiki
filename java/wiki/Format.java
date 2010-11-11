package wiki;

import com.google.appengine.api.datastore.Text;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable(detachable = "true")
@Inheritance(customStrategy = "complete-table")
public class Format extends AbstractDocument<Format> {

  @Persistent
  String name;

  @Persistent
  String title;

  @Persistent
  String namespace;

  @Persistent
  Text description;

  @Persistent(serialized="true",defaultFetchGroup="true")
  List<FormField> fields;

  @Persistent
  Text schema;

  public Format(final String name, final String namespace) {
    this(name, namespace, "");
  }

  public Format(final String name, final String namespace, final String description) {
    this(name, namespace, description, name.replaceAll("_", " "));
  }

  public Format(final String name, final String namespace, final String description, final String title) {
    this.name = Util.validFormatName(name);
    this.title = title;
    this.namespace = Util.validFormatNamepsace(namespace);
    this.description = new Text(description);
    fields = new ArrayList<FormField>();
  }

  void setSchema(final String schema) {
    this.schema = new Text(schema);
  }

  String getSchema() {
    if (schema == null)
      return null; // TODO(pmy): temporary until schema always present.
    return schema.getValue();
  }

  public String getName() {
    return name;
  }

  public String getTitle() {
    return title;
  }

  public String getURLTitle() {
    return title.replaceAll(" ", "_");
  }

  public String getNamespace() {
    return namespace;
  }

  public String getDescription() {
    return description.getValue();
  }

  public void setTitle(final String title) {
    this.title = title;
  }

  public void setNamespace(final String namespace) {
    this.namespace = namespace;
  }

  public void setDescription(final String description) {
    this.description = new Text(description);
  }

  public static String gqlFilterForMatchingFormat(final String otherFormatName) {
    return String.format("name == '%s'", otherFormatName);
  }

  public static String gqlFilterForMatchingFormatTitle(final String otherFormatTitle) {
    return String.format("title == '%s'", otherFormatTitle);
  }

  public List<FormField> getFields() {
    return fields;
  }

  public String toString() {
    return String.format("%s@%d:{name=\"%s\",namespace=\"%s\",fields=%s}",
                         Format.class.getName(), System.identityHashCode(this),
                         name, namespace, fields);
  }
}
