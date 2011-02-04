package wiki;

import com.google.appengine.api.datastore.Text;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable(detachable = "true")
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public class MetaDocument<T> extends AbstractDocument<T> {

  public static String gqlFilterForMatchingFormatTitle(final String otherFormatTitle) {
    return String.format("title == '%s'", otherFormatTitle);
  }

  @Persistent
  String name;

  @Persistent
  String title;

  @Persistent
  Text description;

  public MetaDocument(final String name, final String title, final String description) {
    this.name = Util.validFormatName(name);
    this.title = title;
    this.description = new Text(description);
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description.getValue();
  }

  public void setDescription(final String description) {
    this.description = new Text(description);
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(final String title) {
    this.title = title;
  }

  public String getURLTitle() {
    if (title == null)
      return "";
    return title.replaceAll(" ", "_");
  }
}
