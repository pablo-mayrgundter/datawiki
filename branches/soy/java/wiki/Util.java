package wiki;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

public class Util {

  public static final String XML_SAFE_CHARS = "-a-zA-Z_";
  public static final String XML_SAFE_STRING = String.format("^[%s]+$", XML_SAFE_CHARS);

  /**
   * Implements ESAPI's Rule #1 escaping.
   * TODO(pmy): replace with ESAPI.
   */
  public static String encodeForHTML(String s) {
    s = s.replace("&", "&amp;");
    s = s.replace("<", "&lt;");
    s = s.replace(">", "&gt;");
    s = s.replace("\"", "&quot;");
    s = s.replace("'", "&#x27;");
    s = s.replace("/", "&#x2F;");
    return s;
  }

  /**
   * Implements ESAPI's Rule #2 escaping for the quoted attribute
   * case.  TODO(pmy): replace with ESAPI.
   */
  public static String encodeForDoubleQuotedAttribute(String s) {
    s = s.replace("\"", "&quot;");
    return s;
  }

  public static String getParameter(final HttpServletRequest req, final String name, final String defaultValue) {
    final String value = req.getParameter(name);
    return value == null ? defaultValue : value;
  }

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

  /**
   * Checks if the given string is a valid name.
   *
   * @returns The given string if it is valid.
   * @throws IllegalArgumentException If the given string is not
   * valid to use as a name.
   */
  static String validFormatName(final String s) {
    if (!s.matches(XML_SAFE_STRING)) {
      throw new IllegalArgumentException(String.format("The given name cannot be used."
                                                       + "It must match only these characters: "
                                                       + XML_SAFE_CHARS));
    }
    return s;
  }

  /**
   * Checks if the given string is a valid URI namespace.
   *
   * @returns The given string if it is valid.
   * @throws IllegalArgumentException If the given string is not
   * valid to use as a namespace.
   */
  static String validFormatNamepsace(final String s) {
    try {
      new URI(s);
      return s;
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException(String.format("The given format namespace '%s' cannot be used.  It must be a valid URI.", s));
    }
  }

  /**
   * Encodes the given string as application/x-www-form-urlencoded and
   * prefers UTF-8 but will fallback to the platform's default
   * (URLEncoder.encode()) if it is not available.
   */
  @SuppressWarnings("deprecation")
  static String urlEncode(final String s) {
    try {
      return URLEncoder.encode(s, "UTF-8");
    } catch (java.io.UnsupportedEncodingException e) {
      return URLEncoder.encode(s);
    }
  }

  static String encodePost(final String [][] params) {
    String body = null;
    for (final String [] param : params) {
      body = body == null ? "" : body + "&";
      body += urlEncode(param[0]) +"="+ urlEncode(param[1]);
    }
    return body;
  }

  static Response getErrorResponse(final String errMsg, final Logger logger,
                                   final int httpCode, final Exception ... e) {
    if (e.length > 0) {
      logException(errMsg, logger, e[0]);
    } else {
      logWarning(errMsg, logger);
    }
    return Response.status(httpCode).entity(errMsg).build();    
  }

  static void logWarning(final String errMsg, final Logger logger) {
      logger.log(Level.WARNING, errMsg);
  }

  static void logException(final String errMsg, final Logger logger, final Exception e) {
    logger.log(Level.WARNING, errMsg, e);
  }
}
