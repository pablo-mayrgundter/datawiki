package wiki;

import java.util.Set;
import java.util.HashSet;
import javax.ws.rs.core.Application;

public class FormApp extends Application {
  public Set<Class<?>> getClasses() {
    final Set<Class<?>> s = new HashSet<Class<?>>();
    s.add(Datasets.class);
    s.add(Documents.class);
    s.add(Formats.class);
    //s.add(Hub.class);
    return s;
  }
}
