package wiki;

import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;

public class Util {

  public static final String XML_SAFE_CHARS = "a-zA-Z_-";

  /**
   * @return String of the form http://host.com or http://host.com:port
   * if the port != 80.
   */
  public static String getHostURL(final HttpServletRequest req) {
    String url = req.getScheme() +"://"+ req.getServerName();
    int port = req.getServerPort();
    if (port != 80)
      url += ":" + port;
    return url;
  }

  static boolean safeForXmlTag(final String s) {
    return s.matches(String.format("^[%s]+$", XML_SAFE_CHARS));
  }

  static boolean safeFormatNamespace(final String s) {
    try {
      new URL(s);
      return true;
    } catch (MalformedURLException e) {
      return false;
    }
  }
}
