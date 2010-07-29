package wiki;

import java.io.IOException;
import java.io.StringBufferInputStream;
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
  public static Format lookupFormat(final String name) {
    final List<Format> formats = new Formats().query(Format.gqlFilterForMatchingFormat(name));
    if (formats.size() == 0)
      return null;
    return formats.get(0);
  }

  /** @return The format or null if not found. */
  public static Format lookupFormatByTitle(final String title) {
    final List<Format> formats = new Formats().query(Format.gqlFilterForMatchingFormatTitle(title.replaceAll("_", " ")));
    if (formats.size() == 0)
      return null;
    return formats.get(0);
  }

  static final String JSP_FORMAT_NOT_FOUND = "/formatNotFound.jsp";
  static final String JSP_COLLECTION = "/formats.jsp";
  static final String JSP_SINGLE = "/format.jsp";

  public Formats() {
    super(Format.class);
  }

  @GET
  @Produces({"text/html"})
  public Response get(@Context HttpServletRequest req,
                      @Context HttpServletResponse rsp) throws ServletException, IOException {
    req.getRequestDispatcher(JSP_COLLECTION).include(req, rsp);
    return Response.ok().build();
  }

  @GET
  @Path("{formatName}")
  @Produces({"text/html"})
  public Response get(@PathParam("formatName") String formatName,
                      @Context HttpServletRequest req,
                      @Context HttpServletResponse rsp) throws ServletException, IOException {
    Format format = null;
    final List<Format> formats = query(Format.gqlFilterForMatchingFormat(formatName));
    if (formats.size() == 0) {
      if (req.getParameter("action") != null && req.getParameter("action").equals("edit")) {
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

  @POST
  @Consumes({"multipart/form-data"})
  @Produces({"text/html"})
  public Response post(@Context HttpServletRequest req,
                       @Context HttpServletResponse rsp)
    throws IOException, ServletException, FileUploadException, URISyntaxException {
    final List<FileItem> items = FormUpload.processFormData(req);
    final List<FormField> fields = new ArrayList<FormField>();
    Format format = null;
    for (final FileItem item : items) {
      final String fieldName = item.getFieldName();
      final String fieldValue = new String(item.get());
      if (fieldName.equals("xml")) {
        try {
          format = XmlSerializer.formatFromXml(new StringBufferInputStream(fieldValue));
        } catch (SAXException e) {
          req.setAttribute("reqXml", fieldValue);
          req.setAttribute("reqXmlException", e);
          req.getRequestDispatcher(JSP_COLLECTION).include(req, rsp);
          return Response.ok().build();
        }
      }
    }
    save(format);
    showFormat(format, req, rsp);
    return Response.created(new URI("/wiki/formats/"+format.getName())).build();
  }

  /**
   * TODO(pmy): Using POST to *update* format.  REST says use PUT for
   * update, POST for new collection at address.
   */
  @POST
  @Path("{formatName}")
  @Consumes({"multipart/form-data"})
  @Produces({"text/html"})
  public Response post(@PathParam("formatName") String formatName,
                       @Context HttpServletRequest req,
                       @Context HttpServletResponse rsp) throws Exception {
    final List<FileItem> items = FormUpload.processFormData(req);
    String description = null, title = "";
    // Used to track the largest fieldNdx for the reconstructed
    // ordering.  This will be compared with the number of fields to
    // ensure there is no field index missing.
    int fieldNdxMax = -1;
    final SortedMap<Integer, FormField> sortedFields = new TreeMap<Integer, FormField>();
    for (final FileItem item : items) {
      final String fieldName = item.getFieldName();
      final String reqAttrs = new String(item.get());
      logger.warning("### Field name: "+ fieldName + ", value: "+ reqAttrs);
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

    final List<Format> formats = query(Format.gqlFilterForMatchingFormat(formatName));
    Format format = null;
    if (formats.size() == 0) {
      System.out.println("##### Formats: creating new format object.");
      format = new Format(formatName, makeFormatNamespace(req), description, title);
    } else {
      System.out.println("##### Forms: reusing existing format object.");
      format = formats.get(0);
      format.setDescription(description);
      format.setTitle(title);
    }
    format.fields.clear();
    format.fields.addAll(newFields);
    /*  
    if (format.fields.isEmpty())
      format.fields = newFields;
    else
      for (final FormField newField : newFields)
        for (final FormField oldField : format.fields)
          if (oldField.getName().equals(newField.getName()))
            if (!oldField.getText().equals(newField.getText()))
              oldField.setText(newField.getText());
    */
    System.out.printf("Saving format with %d fields.\n", newFields.size());
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
    System.out.printf("Showing format with %d fields.\n", format.fields.size());
    req.getRequestDispatcher(JSP_SINGLE).include(req, rsp);
  }

  String makeFormatNamespace(final HttpServletRequest req) {
    return Util.getHostURL(req) + req.getRequestURI();
  }
}