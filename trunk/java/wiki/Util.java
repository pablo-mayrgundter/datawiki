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

  // String utils
  /**
   * Prefix each line of the string with the given number of spaces.
   * This includes the first line unless the string is empty, in which
   * case the given string is itself returned.
   */
  public static String indent(String s, int numSpaces) {
    // Special case of not prefixing with tab if string is
    if (s.equals("")) {
      return s;
    }
    String tab = "";
    for (int i = 0; i < numSpaces; i++) {
      tab += " ";
    }
    return tab + s.replaceAll("\n", "\n" + tab);
  }

  // XSS utils
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

  public static int getParameter(final HttpServletRequest req, final String name, final int defaultValue) {
    final String reqVal = req.getParameter(name);
    try {
      return Integer.parseInt(reqVal);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  /**
   * @return String of the form http://host.com or http://host.com:port
   * if the port != 80.
   */
  public static String getHostURL(final HttpServletRequest req) {
    String url = req.getScheme() +"://"+ req.getServerName();
    int port = req.getServerPort();
    if (port != 80) {
      url += ":" + port;
    }
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
      throw new IllegalArgumentException(String.format("The given name (%s) cannot be used.  "
                                                       + "It must match only these characters: "
                                                       + XML_SAFE_CHARS, s));
    }
    return s;
  }

  // Namepaces.

  /**
   * Given /foo/bar/baz/, will return /foo/bar/.
   *
   * @throws IllegalArgumentException if no slash is present or if the
   * only slash present is the terminal character.
   */
  static String getParentNamespace(String namespace) {
    int ndx = namespace.lastIndexOf("/");
    if (ndx == -1) {
      throw new IllegalArgumentException("Malformed namespace, must have at least one slash.");
    }
    if (ndx == namespace.length() - 1) {
      ndx = namespace.lastIndexOf("/", ndx - 1);
      if (ndx == -1) {
        throw new IllegalArgumentException("Namespace has no parent, must have at least one non-terminal slash.");
      }
    }
    // Include trailing "/".
    return namespace.substring(0, ndx + 1);
  }

  /**
   * Given /foo/bar/baz/, will return baz.
   */
  static String getNameFromNamespace(String namespace) {
    String [] parts = namespace.split("/");
    return parts[parts.length - 1];
  }

  /**
   * The root namespace for this server.  This is inferred from the
   * given request, e.g. "http://localhost:8080/wiki/".
   */
  static String getRootNamespace(HttpServletRequest req) {
    return getHostURL(req) + "/wiki/";
  }

  /**
   * Create a new namespace with the given name under the given
   * parentNamespace.
   *
   * NOTE: it's good practice to ensure a trailing slash on a
   * namespace, but this method does not.  This is because the
   * namespaces are also wiki-style article names.
   *
   * @throws IllegalArgumentException if the given name is invalid or
   * if the new namespace is invalid.
   */
  static String createNamespace(String parentNamespace, String name) {
    if (!parentNamespace.endsWith("/")) {
      parentNamespace += "/";
    }
    validFormatName(name);
    return validFormatNamespace(parentNamespace + name);
  }

  /**
   * Checks if the given string is a valid URI namespace.
   *
   * @returns The given string if it is valid.
   * @throws IllegalArgumentException If the given string is not
   * valid to use as a namespace.
   */
  static String validFormatNamespace(final String s) {
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
