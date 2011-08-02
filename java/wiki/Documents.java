package wiki;

import common.PersistentList;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Consumes;
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

@Path("/docs")
public class Documents extends PersistentList<MultiPartDocument> {

  static final Logger logger = Logger.getLogger(Documents.class.getName());
  static final String JSP_SINGLE = "/document.jsp";

  /** Helper for maybe querying if a query is present, or returning all docs otherwise. */
  public static List<MultiPartDocument> queryOrAll(final HttpServletRequest request,
                                                   final String formatName) {
    // TODO(pmy): consider use query instead of direct access to request attributes.
    // Create a data table.
    final Documents allDocs = new Documents();
    List<MultiPartDocument> matchingDocs;
    if (request.getParameter("q") == null) {
      matchingDocs = allDocs.matchingFormat(formatName);
    } else {
      try {
        matchingDocs = allDocs.search(request);
      } catch (Exception e) {
        e.printStackTrace();
        throw new IllegalStateException("Internal search failed: "+ e);
      }
    }
    return matchingDocs;
  }

  public Documents() {
    super(MultiPartDocument.class);
  }

  public List<MultiPartDocument> matchingFormat(final String format) {
    return query(MultiPartDocument.gqlFilterForMatchingFormat(format));
  }

  /** Show doc. */
  @GET
  @Path("{id}")
  @Produces({"text/html; charset=utf-8"})
  public Response getSingleDoc(@Context HttpServletRequest req,
                               @Context HttpServletResponse rsp,
                               @PathParam("id") int id)
    throws ServletException, IOException {
    if (req.getParameter("output") != null && req.getParameter("output").equals("xml")) {
      return getSingleDocAsXml(req, rsp, id);
    }
    final MultiPartDocument doc = get(id - 1);
    if (doc == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    req.setAttribute("doc", doc);
    req.getRequestDispatcher(JSP_SINGLE).include(req, rsp);
    return Response.ok().build();
  }

  /** Get doc as xml. */
  @GET
  @Path("{id}")
  @Produces({"text/xml; charset=utf-8"})
  public Response getSingleDocAsXml(@Context HttpServletRequest req,
                                    @Context HttpServletResponse rsp,
                                    @PathParam("id") int id)
    throws ServletException, IOException {
    final MultiPartDocument doc = get(id - 1);
    if (doc == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    final Format format = new Formats().withName(doc.getFormat());
    if (format == null) {
      return Response.status(Response.Status.NOT_FOUND).entity("Document has missing format: "+ doc.getFormat()).build();
    }
    String xml;
    try {
      xml = XmlSerializer.toXml(doc, format);
    } catch (javax.xml.transform.TransformerException e) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Document could not be converted to XML: "+ e).build();
    }
    rsp.getOutputStream().write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n".getBytes());
    rsp.getOutputStream().write(xml.getBytes());
    return Response.ok().build();
  }

  @GET
  @Path("{id}/qrcode")
  @Produces({"image/png; charset=utf-8"})
  public Response getQRCode(@Context HttpServletRequest req,
                            @Context HttpServletResponse rsp,
                            @PathParam("id") int id)
    throws ServletException, IOException {
    final String hostURL = Util.getHostURL(req);
    final String selfURL = hostURL + req.getRequestURI();
    try {
      final QR qr = new QR(selfURL);
      qr.draw();
      qr.write(rsp.getOutputStream());
    } catch (Exception e) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Path could not be encoded: "+ e).build();
    }
    return Response.ok().build();
  }

  @GET
  @Path("{id}/shape/tile")
  @Produces({"image/png; charset=utf-8"})
  public Response getTile(@Context HttpServletRequest req,
                          @Context HttpServletResponse rsp,
                          @PathParam("id") int id)
    throws ServletException, IOException {
    final MultiPartDocument doc = get(id - 1);
    if (doc == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    final DocumentField shapeField = doc.findField("shape");
    if (shapeField == null) {
      return Response.status(Response.Status.NOT_FOUND).entity("Given document has no shape field.").build();
    }
    try {
      new Polygon(shapeField.getValue()).write(rsp.getOutputStream());
    } catch (Exception e) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Shape could not be drawn: "+ e).build();
    }
    return Response.ok().build();
  }

  /** Update doc from form post. */
  @POST
  @Path("{id}")
  @Consumes({"multipart/form-data"})
  @Produces({"text/html; charset=utf-8"})
  public Response handlePost(@Context HttpServletRequest req,
                             @Context HttpServletResponse rsp,
                             @PathParam("id") int id) throws Exception {
    final MultiPartDocument doc = get(id - 1);
    if (doc == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    List<FileItem> items = null;
    try {
      items = FormUpload.processFormData(req);
    } catch (FileUploadException e) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Submission could not be parsed: "+ e).build();
    }
    final LinkedHashMap<String,DocumentField> fields = fileItemsToDocumentFields(items);
    doc.fields.clear();
    for (final String fieldName : fields.keySet()) {
      final DocumentField field = fields.get(fieldName);
      doc.addField(field);
      System.out.println("field: "+ field);
    }
    System.out.println("saving: "+ doc);
    save(doc);
    req.setAttribute("doc", doc);
    req.getRequestDispatcher(JSP_SINGLE).include(req, rsp);
    return Response.ok().build();
  }

  // HELPERS

  static LinkedHashMap<String,DocumentField> fileItemsToDocumentFields(final List<FileItem> items) {
    final LinkedHashMap<String,DocumentField> fields = new LinkedHashMap<String,DocumentField>();
    String format = null, id = null;
    for (final FileItem item : items) {
      final String fieldName = item.getFieldName();
      final String fieldValue = new String(item.get());
      fields.put(fieldName, new DocumentField(fieldName, fieldValue));
    }
    return fields;
  }

  // TODO(pmy): fix, very ugly.
  public List<MultiPartDocument> search(final HttpServletRequest req)
    throws ServletException, IOException {
    final Map params = req.getParameterMap();
    String query = "";
    int varCount = 0;
    String varName = "f"+varCount;
    String varNames = "";
    for (final Object entryObj : params.entrySet()) {
      final Map.Entry entry = ((Map.Entry)entryObj);
      final String key = (String) entry.getKey();
      // TODO(pmy): hack.
      if (key.equals("tqx") || key.equals("q") || key.equals("summary") 
          || key.equals("dataset") || key.equals("output")) {
        continue;
      }
      final String [] values = (String []) entry.getValue();
      // TODO(pmy): reconsider this. currently don't filter on attrs with empty values.
      if (values == null || values.length == 0 || values[0].equals("")) {
        continue;
      }
      if (values.length > 1) {
        logger.warning("Empty or multi-value filters not supported for key: "+ key);
        continue;
      }
      final String value = values[0];
      if (key.equals("format")) {
        if (!query.equals("")) {
          query += " && ";
        }
        query += "format == '"+ value +"'";
        continue;
      }
      if (!query.equals("")) {
        query += " && ";
      }
      query += String.format("(fields.contains(%s) && %s.name == '%s' && %s.value == '%s')",
                             varName, varName, key, varName, value);
      if (!varNames.equals("")) {
        varNames += ",";
      }
      varNames += varName;
      varName = "f"+(++varCount);
    }
    String varDecl = "";
    for (final String name : varNames.split(",")) {
      if (!varDecl.equals("")) {
        varDecl += "; ";
      }
      varDecl += DocumentField.class.getName() +" "+ name;
    }
    // hack.
    if (query.equals("")) {
      return new java.util.ArrayList<MultiPartDocument>();
    }
    logger.info("queryWithVariables: "+ query + ", and var decl: "+ varDecl);
    return queryWithVariables(query, varDecl);
  }
}
