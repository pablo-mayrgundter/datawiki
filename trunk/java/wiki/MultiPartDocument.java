package wiki;

import com.google.appengine.api.datastore.Text;
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

  public static String gqlFilterForMatchingFormat(final String formatName) {
    return String.format("format == '%s'", formatName);
  }

  public static String gqlFilterForMatchingDataset(final String datasetName) {
    return String.format("dataset == '%s'", datasetName);
  }

  @Persistent
  String format;

  @Persistent
  String dataset;

  @Persistent(defaultFetchGroup = "true")
  @Element(dependent = "true")
  List<DocumentField> fields;

  @Persistent
  Text xml;

  // TODO(pmy): why public?
  public MultiPartDocument() {
    fields = new ArrayList<DocumentField>();
  }

  /** Equivalent to MultiPartDocument(format, format). */
  public MultiPartDocument(final String format) {
    this(format, format);
  }

  public MultiPartDocument(final String format, final String dataset) {
    this();
    this.format = format;
    this.dataset = dataset;
  }

  public String getDataset() {
    return dataset;
  }

  public String getFormat() {
    return format;
  }

  public void setXml(final String xml) {
    this.xml = new Text(xml);
  }

  public String getXml() {
    return xml.getValue();
  }

  public List<DocumentField> getFields() {
    return fields;
  }

  /**
   * This method performs a linear search of fields for the first
   * field whose name matches the given name parameter, or null if no
   * match is found.
   *
   * TODO(pmy): less than O(n), XPath.
   */
  public DocumentField findField(final String name) {
    for (final DocumentField field : fields)
      if (field.getName().equals(name))
        return field;
    return null;
  }

  public void addField(final DocumentField field) {
    if (fields == null)
      fields = new ArrayList<DocumentField>();
    fields.add(field);
  }

  public String toString() {
    return String.format("MultiPartDocument@%d{format: %s, dataset: %s, fields: %s}",
                         System.identityHashCode(this),
			 format, dataset,
			 fields.toString());
  }
}
