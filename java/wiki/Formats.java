package wiki;

import java.io.IOException;
import java.io.StringBufferInputStream;
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
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

@Path("/formats")
public class Formats extends PersistentList<Format> {

  static final Logger logger = Logger.getLogger(Formats.class.getName());

  /** @return The form or null if not found. */
  public static Format lookupFormat(final String name) {
    final List<Format> formats = new Formats().query(Format.gqlFilterForMatchingFormat(name));
    if (formats.size() == 0)
      return null;
    return formats.get(0);
  }

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
    return getFormat(formatName, req, rsp);
  }

  String makeFormatNamespace(final HttpServletRequest req) {
    return Util.getHostURL(req) + req.getRequestURI();
  }

  public Response getFormat(final String formatName,
                            final HttpServletRequest req,
                            final HttpServletResponse rsp) throws ServletException, IOException {
    final List<Format> formats = query(Format.gqlFilterForMatchingFormat(formatName));
    req.setAttribute("formatName", formatName);
    final Format format = formats.size() > 0 ?
      formats.get(0)
      : new Format(formatName, makeFormatNamespace(req), "");
    req.setAttribute("format", format);
    System.out.printf("Showing format with %d fields.\n", format.fields.size());
    req.getRequestDispatcher(JSP_SINGLE).include(req, rsp);
    return Response.ok().build();
  }

  @POST
  @Consumes({"multipart/form-data"})
  @Produces({"text/html"})
  public String post(@Context HttpServletRequest req,
                     @Context HttpServletResponse rsp) throws Exception {
    if (!ServletFileUpload.isMultipartContent(req)) {
      throw new IllegalArgumentException("Must specify enctype=\"multipart/form-data\" in form definition."
                                         +" See http://www.w3.org/TR/html401/interact/forms.html#h-17.13.4");
    }
    final FileItemFactory factory = new FileItemFactory() {
        public FileItem createItem(final String fieldName, final String contentType,
                                   final boolean isFormField, final String fileName) {
          System.err.println("Documents: handlePost: createItem: "+ fieldName);
          // TODO(pmy): clean up isFormField usage in related classes.
          return new DataStoreFileItem(contentType, fieldName, fileName, isFormField);
        }
      };
    final ServletFileUpload upload = new ServletFileUpload(factory);
    final List<FileItem> items = upload.parseRequest(req);

    if (items == null)
      throw new NullPointerException("Empty form yeilds no (null) items.");

    final List<FormField> fields = new ArrayList<FormField>();
    Format format = null;
    for (final FileItem item : items) {
      final String fieldName = item.getFieldName();
      final String fieldValue = new String(item.get());
      if (fieldName.equals("xml")) {
        format = XmlSerializer.formatFromXml(new StringBufferInputStream(fieldValue));
      }
    }

    System.out.printf("Saving format:\n%s\n", format);
    for (FormField field : format.getFields()) {
      field.getName();
      field.getValue();
    }
    save(format);
    return "<html><head><meta http-equiv=\"refresh\" content=\"0;url=/wiki/formats/"+format.getName()+"\" /></head></html>";
  }

  /**
   * TODO(pmy): Using POST to *update* format.  REST says use PUT for
   * update, POST for new collection at address.
   */
  @POST
  @Path("{formatName}")
  @Consumes({"application/x-www-form-urlencoded"})
  @Produces({"text/html"})
  public Response post(final MultivaluedMap<String,String> params,
                       @PathParam("formatName") String formatName,
                       @Context HttpServletRequest req,
                       @Context HttpServletResponse rsp) throws Exception {
    logger.warning("in post to: "+ formatName +", numParams: "+ params.size());
    String description = null, title = "";
    // Used to track the largest fieldNdx for the reconstructed
    // ordering.  This will be compared with the number of fields to
    // ensure there is no field index missing.
    int fieldNdxMax = 0;
    final SortedMap<Integer, FormField> sortedFields = new TreeMap<Integer, FormField>();
    for (final String fieldName : params.keySet()) {
      final String reqAttrs = params.getFirst(fieldName);
      if (reqAttrs == null)
        throw new IllegalArgumentException("Invalid field parameter for field: "+ fieldName);
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

      final FormField field = new FormField(fieldText, fieldName, "");
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
}