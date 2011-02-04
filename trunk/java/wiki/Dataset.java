package wiki;

import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable(detachable = "true")
@Inheritance(customStrategy = "complete-table")
public class Dataset extends MetaDocument<Dataset> {

  public static String gqlFilterForMatchingFormat(final String otherFormatName) {
    return String.format("format == '%s'", otherFormatName);
  }

  @Persistent
  String format;

  public Dataset(final String name, final String title, final String description, final String format) {
    super(name, title, description);
    this.format = format;
  }

  Dataset(final Format format) {
    this(format.getName(), format.getTitle(), format.getDescription(), format.getName());
  }

  public String getFormat() {
    return format;
  }

  public String toString() {
    return String.format("%s@%d:{name=\"%s\",title\"=%s\",format=\"%s\"}",
                         Format.class.getName(), System.identityHashCode(this),
                         name, title, format);
  }
}
