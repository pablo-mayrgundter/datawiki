package wiki;

import javax.servlet.http.HttpServletRequest;

public class Util {

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
}
