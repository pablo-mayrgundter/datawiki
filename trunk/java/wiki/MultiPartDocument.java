package wiki;

import java.util.ArrayList;
import java.util.List;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@PersistenceCapable(detachable = "true")
@Inheritance(customStrategy = "complete-table")
public class MultiPartDocument extends AbstractDocument<MultiPartDocument> {

  public static String gqlFilterForMatchingFormat(final String otherFormatName) {
    return String.format("format == '%s'", otherFormatName);
  }

  @Persistent
  String format;

  @Persistent(defaultFetchGroup = "true")
  @Element(dependent = "true")
  List<DocumentField> fields;

  public MultiPartDocument() {
    fields = new ArrayList<DocumentField>();
  }

  public MultiPartDocument(final String format) {
    this();
    this.format = format;
  }

  public String getFormat() {
    return format;
  }

  public List<DocumentField> getFields() {
    return fields;
  }

  public void addField(final DocumentField field) {
    if (fields == null)
      fields = new ArrayList<DocumentField>();
    fields.add(field);
  }

  public String toString() {
    return String.format("MultiPartDocument@%d{format: %s, fields: %s}",
                         System.identityHashCode(this), format, fields.toString());
  }
}
