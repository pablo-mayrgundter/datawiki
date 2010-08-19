package wiki;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;

// TODO(pmy): generalize this to AbstractDocument, or store documents
// as XML and use objects for indices.  Also, the flow between
// POST/GET and pageList could probably make better use of dispatchers
// so that pageList is a real entry point and can be dispatched to.
@Path("/")
public class Documents extends PersistentList<MultiPartDocument> {

  static final Logger logger = Logger.getLogger(Documents.class.getName());

  static final String JSP_SINGLE = "/document.jsp";
  static final String JSP_COLLECTION = "/documents.jsp";
  static final String JSP_COLLECTION_XML = "/documentsXml.jsp";
  static final String JSP_UNKNOWN = "/unknown.jsp";

  public Documents() {
    super(MultiPartDocument.class);
  }

  public List<MultiPartDocument> matchingFormat(final String format) {
    return query(MultiPartDocument.gqlFilterForMatchingFormat(format));
  }

  @GET
  @Produces({"text/html; charset=utf-8"})
  public Response getDocs(@Context HttpServletRequest req,
                          @Context HttpServletResponse rsp) {
    return Response.status(Response.Status.BAD_REQUEST).entity("Must specify a format.").build();
  }

  @GET
  @Path("{formatTitle}")
  @Produces({"text/html; charset=utf-8"})
  public Response getDocsForFormat(@Context HttpServletRequest req,
                                   @Context HttpServletResponse rsp,
                                   @PathParam("formatTitle") String formatTitle)
    throws ServletException, IOException {
    final Format format = Formats.lookupFormatByTitle(formatTitle);
    if (format == null) {
      return Formats.formatWithTitleNotFound(formatTitle, req, rsp);
    }
    return pageList(req, rsp, format);
  }

  @GET
  @Path("{format}/{id}")
  @Produces({"text/html; charset=utf-8"})
  public Response getSingleDoc(@Context HttpServletRequest req,
                               @Context HttpServletResponse rsp,
                               @PathParam("id") String id)
    throws ServletException, IOException {
    req.setAttribute("doc", get(Integer.parseInt(id) - 1));
    req.getRequestDispatcher(JSP_SINGLE).include(req, rsp);
    return Response.ok().build();
  }

  @POST
  @Path("{formatTitle}")
  @Consumes({"multipart/form-data"})
  @Produces({"text/html; charset=utf-8"})
  public Response postDoc(@Context HttpServletRequest req,
                          @Context HttpServletResponse rsp,
                          @PathParam("formatTitle") String formatTitle) throws Exception {
    List<FileItem> items = null;
    try {
      items = FormUpload.processFormData(req);
    } catch (FileUploadException e) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Submission could not be parsed: "+ e).build();
    }
    final LinkedHashMap<String,DocumentField> fields = fileItemsToDocumentFields(items);
    final Format format = Formats.lookupFormatByTitle(formatTitle);
    if (format == null) {
      return Formats.formatWithTitleNotFound(formatTitle, req, rsp);
    }
    final MultiPartDocument doc = new MultiPartDocument(format.getName());
    for (final String fieldName : fields.keySet()) {
      doc.addField(fields.get(fieldName));
    }
    save(doc);
    return pageList(req, rsp, format);
  }

  @POST
  @Path("{formatTitle}")
  @Consumes({"application/x-www-form-urlencoded"})
  public Response postXmlDoc(@Context HttpServletRequest req,
                             @Context HttpServletResponse rsp,
                             @PathParam("formatTitle") String formatTitle,
                             String content) throws Exception {
    final Format format = Formats.lookupFormatByTitle(formatTitle);
    if (format == null) {
      return Formats.formatWithTitleNotFound(formatTitle, req, rsp);
    }
    final MultiPartDocument doc = XmlSerializer.docFromXml(new java.io.ByteArrayInputStream(content.getBytes()));
    if (!doc.getFormat().equals(format.getName())) {
      return Response.status(Response.Status.BAD_REQUEST).entity("The submitted XML does not match this format.").build();
    }
    System.out.println(doc);
    save(doc);
    return pageList(req, rsp, format);
  }

  @POST
  @Path("{format}/{id}")
  @Consumes({"multipart/form-data"})
  @Produces({"text/html; charset=utf-8"})
  public Response handlePost(@Context HttpServletRequest req,
                             @Context HttpServletResponse rsp,
                             @PathParam("format") String format,
                             @PathParam("id") String reqId) throws Exception {
    final int id = Integer.parseInt(reqId) - 1;
    final MultiPartDocument doc = get(id);
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
      doc.addField(fields.get(fieldName));
    }
    save(doc);
    req.setAttribute("doc", doc);
    req.getRequestDispatcher(JSP_SINGLE).include(req, rsp);
    return Response.ok().build();
  }

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

  Response pageList(final HttpServletRequest req, final HttpServletResponse rsp, final Format format)
    throws ServletException, IOException {
    req.setAttribute("formatName", format.getName());
    req.setAttribute("format", format);
    // TODO(pmy): hack for craig, move this to @Produces control.
    final String output = req.getParameter("output");
    if (output != null && output.equals("xml"))
      req.getRequestDispatcher(JSP_COLLECTION_XML).include(req, rsp);
    else
      req.getRequestDispatcher(JSP_COLLECTION).include(req, rsp);
    return Response.ok().build();
  }

  /** Helper for maybe querying if a query is present, or returning all docs otherwise. */
  public static List<MultiPartDocument> queryOrAll(final HttpServletRequest request,
                                                   final String reqFormatName,
                                                   final Format format) {
    // TODO(pmy): consider use query instead of direct access to request attributes.
    // Create a data table.
    final Documents allDocs = new Documents();
    // TODO(pmy): pick one or the other way to propagate this.
    List<MultiPartDocument> matchingDocs;
    if (request.getParameter("q") == null)
      matchingDocs = allDocs.matchingFormat(reqFormatName);
    else {
      try {
        matchingDocs = allDocs.search(request, format);
      } catch (Exception e) {
        e.printStackTrace();
        throw new IllegalStateException("Internal search failed: "+ e);
      }
    }
    return matchingDocs;
  }

  public List<MultiPartDocument> search(final HttpServletRequest req, final Format format)
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
      if (key.equals("tqx") || key.equals("q") || key.equals("summary"))
        continue;
      final String [] values = (String []) entry.getValue();
      // TODO(pmy): reconsider this. currently don't filter on attrs with empty values.
      if (values == null || values.length == 0 || values[0].equals(""))
        continue;
      if (values.length > 1) {
        logger.warning("Empty or multi-value filters not supported for key: "+ key);
        continue;
      }
      final String value = values[0];
      if (key.equals("format")) {
        if (!query.equals(""))
          query += " && ";
        query += "format == '"+ value +"'";
        continue;
      }
      if (!query.equals(""))
        query += " && ";
      query += String.format("fields.contains(%s) && %s.name == '%s' && %s.value == '%s'",
                             varName, varName, key, varName, value);
      if (!varNames.equals(""))
        varNames += ",";
      varNames += varName;
      varName = "f"+(++varCount);
    }
    String varDecl = "";
    for (final String name : varNames.split(",")) {
      if (!varDecl.equals(""))
        varDecl += "; ";
      varDecl += DocumentField.class.getName() +" "+ name;
    }
    logger.info("queryWithVariables: "+ query + ", and var decl: "+ varDecl);
    return queryWithVariables(query, varDecl);
  }
}