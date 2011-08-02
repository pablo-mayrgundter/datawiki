package wiki;

import common.PersistentList;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Logger;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.ws.rs.core.Context;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.xml.sax.SAXException;

@Path("/formats")
public class Formats extends PersistentList<Format> {

  static final Logger logger = Logger.getLogger(Formats.class.getName());

  /** @return The format or null if not found. */
  public Format withName(final String name) {
    final List<Format> formats = query(Format.gqlFilterForMatchingFormat(name));
    if (formats.size() == 0)
      return null;
    return formats.get(0);
  }

  /** @return The format or null if not found. */
  /*
  public Format lookupFormatByTitle(final String title) {
    final List<Format> formats =
      query(Format.gqlFilterForMatchingFormatTitle(title.replaceAll("_", " ")));
    if (formats.size() == 0)
      return null;
    return formats.get(0);
    }*/

  static final String JSP_COLLECTION = "/formats.jsp";
  static final String JSP_SINGLE = "/format.jsp";
  static final String JSP_FORMAT_NOT_FOUND = "/formatNotFound.jsp";
  static final String JSP_CREATE = "/formatCreate.jsp";
  static final String JSP_CREATED = "/formatCreated.jsp";

  public Formats() {
    super(Format.class);
  }

  @GET
  @Produces({"text/html; charset=utf-8"})
  public Response get(@Context HttpServletRequest req,
                      @Context HttpServletResponse rsp,
                      @QueryParam("action") String action) throws ServletException, IOException {
    if (action != null && action.equals("new")) {
      req.getRequestDispatcher(JSP_CREATE).include(req, rsp);
    } else {
      req.getRequestDispatcher(JSP_COLLECTION).include(req, rsp);
    }
    return Response.ok().build();
  }

  @GET
  @Path("{formatName}")
  @Produces({"text/html; charset=utf-8"})
  public Response get(@Context HttpServletRequest req,
                      @Context HttpServletResponse rsp,
                      @PathParam("formatName") String formatName,
                      @QueryParam("action") String action) throws ServletException, IOException {
    Format format = null;
    final List<Format> formats = query(Format.gqlFilterForMatchingFormat(formatName));
    if (formats.size() == 0) {
      if (action != null && action.equals("edit")) {
        format = new Format(formatName, makeFormatNamespace(req), "");
      } else {
        return formatNotFound(formatName, req, rsp);
      }
    } else {
      format = formats.get(0);
    }
    req.setAttribute("formatName", formatName);
    showFormat(format, req, rsp);
    return Response.ok().build();
  }

  /**
   * Creates a format from XML.  If the XML cannot be parsed, the user
   * may try again.  The root tag of the XML (without its namespace)
   * is used as the name of the format and XPaths are used as field
   * names.
   */
  @POST
  @Consumes({"multipart/form-data"})
  @Produces({"text/html; charset=utf-8"})
  public Response handlePost(@Context HttpServletRequest req,
                             @Context HttpServletResponse rsp)
    throws IOException, ServletException, FileUploadException, URISyntaxException {
    final List<FileItem> items = FormUpload.processFormData(req);
    String title = null;
    String name = null;
    for (final FileItem item : items) {
      final String fieldName = item.getFieldName();
      final String fieldValue = new String(item.get());
      if (fieldName.equals("title")) {
        title = fieldValue;
      }
      if (fieldName.equals("name")) {
        name = fieldValue;
      }
    }
    Format format = withName(name);
    if (format != null) {
      req.getRequestDispatcher(JSP_CREATE).include(req, rsp);
      return Response.ok().build();
    } else if (title == null || name == null) {
      req.getRequestDispatcher(JSP_CREATE).include(req, rsp);
      return Response.ok().build();
    }
    format = new Format(name, makeFormatNamespace(req, "/wiki/formats/"+ name), "", title);
    save(format);
    req.setAttribute("format", format);
    req.setAttribute("formatName", format.getName());
    logger.info("Created: "+ format);
    req.getRequestDispatcher(JSP_SINGLE).include(req, rsp);
    return Response.created(new URI("/wiki/formats/"+format.getName())).build();
  }

  /**
   * TODO(pmy): Using POST to *update* format.  REST says use PUT for
   * update, POST for new collection at address.
   */
  @POST
  @Path("{formatName}")
  @Consumes({"multipart/form-data"})
  @Produces({"text/html; charset=utf-8"})
  public Response handlePost(@PathParam("formatName") String formatName,
                             @Context HttpServletRequest req,
                             @Context HttpServletResponse rsp) throws Exception {
    final List<FileItem> items = FormUpload.processFormData(req);
    for (final FileItem item : items) {
      if (item.getFieldName().equals("xsd")) {
        final String xsd = new String(item.get());
        final Format format = XmlSerializer.formatFromXml(formatName, xsd);
        return Response.ok(format.toString()).build();
      }
    }
    String description = null, title = "";
    // Used to track the largest fieldNdx for the reconstructed
    // ordering.  This will be compared with the number of fields to
    // ensure there is no field index missing.
    int fieldNdxMax = -1;
    final SortedMap<Integer, FormField> sortedFields = new TreeMap<Integer, FormField>();
    for (final FileItem item : items) {
      final String fieldName = item.getFieldName();
      final String reqAttrs = new String(item.get());
      logger.info("field name: "+ fieldName + ", value: "+ reqAttrs);
      final Map<String,String> fieldAttrMap = new HashMap<String,String>();
      final String [] fieldAttrs = reqAttrs.split(";");
      for (final String fieldAttr : fieldAttrs) {
        final String [] nameVal = fieldAttr.split(",");
        if (nameVal.length != 2) {
          logger.warning("Skipping invalid field attribute from client (must be of format name,value): "+ fieldAttr);
          continue;
        }
        final String attrName = URLDecoder.decode(nameVal[0], "UTF-8");
        final String attrValue = URLDecoder.decode(nameVal[1], "UTF-8");
        fieldAttrMap.put(attrName, attrValue);
      }

      if (fieldName.equals("description")) {
        description = fieldAttrMap.get("value");
        continue;
      }

      if (fieldName.equals("title")) {
        title = fieldAttrMap.get("value");
        continue;
      }

      String fieldText = fieldAttrMap.get("help_text");
      if (fieldText == null)
        fieldText = fieldName;

      String fieldNdxStr = fieldAttrMap.get("order");
      if (fieldNdxStr == null) {
        logger.warning("Skipping field with missing index attribute: "+ fieldName);
        continue;
      }

      int fieldNdx = -1;
      try {
        fieldNdx = Integer.parseInt(fieldNdxStr);
      } catch (NumberFormatException e) {
        logger.warning("Skipping field with non-numeric index attribute: "+ fieldName +", index value: "+ fieldNdxStr);
        continue;
      }
      FormField field = null;
      try {
        field = new FormField(fieldText, fieldName, "");
      } catch (IllegalArgumentException e) {
        return Response.status(500).entity(e.getMessage()).build();
      }
      sortedFields.put(fieldNdx, field);
      if (fieldNdx > fieldNdxMax)
        fieldNdxMax = fieldNdx;
    }

    if (fieldNdxMax + 1 != sortedFields.size())
      throw new IllegalArgumentException("The number of submitted fields must match the field index range.");

    final List<FormField> newFields = new ArrayList<FormField>();
    for (final Integer key : sortedFields.keySet()) {
      final FormField field = sortedFields.get(key);
      newFields.add(field);
    }

    if (description == null)
      description = "";

    formatName = formatName.replaceAll("\\s+", "_");
    final List<Format> formats = query(Format.gqlFilterForMatchingFormat(formatName));
    Format format = null;
    if (formats.size() == 0) {
      format = new Format(formatName, makeFormatNamespace(req), description, title);
      logger.info("Created: "+ format);
    } else {
      format = formats.get(0);
      logger.info("Reusing: "+ format);
      format.setDescription(description);
      format.setTitle(title);
    }
    format.fields.clear();
    format.fields.addAll(newFields);
    logger.info("Updated: "+ format);
    save(format);

    return Response.ok().build(); // TODO(pmy): change to 201 when this method is changed to POST.
  }

  static Response formatWithTitleNotFound(final String formatTitle,
                                          final HttpServletRequest req,
                                          final HttpServletResponse rsp)
    throws ServletException, IOException {
    // TODO(pmy): do something different for by title?
    return formatNotFound(formatTitle, req, rsp);
  }

  static Response formatNotFound(final String formatName,
                                 final HttpServletRequest req,
                                 final HttpServletResponse rsp)
    throws ServletException, IOException {
    req.setAttribute("formatName", formatName);
    req.getRequestDispatcher(JSP_FORMAT_NOT_FOUND).include(req, rsp);
    return Response.ok().build();
  }

  void showFormat(final Format format,
                  final HttpServletRequest req,
                  final HttpServletResponse rsp) throws ServletException, IOException {
    req.setAttribute("format", format);
    logger.info(format.toString());
    req.getRequestDispatcher(JSP_SINGLE).include(req, rsp);
  }

  String makeFormatNamespace(final HttpServletRequest req) {
    return makeFormatNamespace(req, req.getRequestURI());
  }

  String makeFormatNamespace(final HttpServletRequest req, final String uri) {
    return Util.getHostURL(req) + uri;
  }
}