package wiki;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

// TODO(pmy): generalize this to AbstractDocument, or store documents
// as XML and use objects for indices.  Also, the flow between
// POST/GET and pageList could probably make better use of dispatchers
// so that pageList is a real entry point and can be dispatched to.
@Path("/documents")
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
  @Produces({"text/html"})
  public Response pageList(@Context HttpServletRequest req,
                           @Context HttpServletResponse rsp)
    throws ServletException, IOException {
    final String format = req.getParameter("format");
    if (format == null)
      return Response.status(Response.Status.BAD_REQUEST).entity("Must specify a format.").build();
    return pageList(req, rsp, format);
  }

  Response pageList(final HttpServletRequest req, final HttpServletResponse rsp, final String formatName)
    throws ServletException, IOException {
    final Format format = Formats.lookupFormat(formatName);
    if (format == null) {
      return Formats.formatNotFound(formatName, req, rsp);
    }
    req.setAttribute("formatName", formatName);
    req.setAttribute("format", format);
    if (req.getParameter("q") != null)
      req.setAttribute("showDocs", true);
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

  @GET
  @Path("{id}")
  @Produces({"text/html"})
  public String getDoc(@Context HttpServletRequest req,
                       @Context HttpServletResponse rsp,
                       @PathParam("id") String id)
    throws ServletException, IOException {
    req.setAttribute("doc", get(Integer.parseInt(id) - 1));
    req.getRequestDispatcher(JSP_SINGLE).include(req, rsp);
    return "";
  }

  @POST
  @Consumes({"multipart/form-data"})
  @Produces({"text/html"})
  public Response handlePost(@Context HttpServletRequest req,
                             @Context HttpServletResponse rsp) throws Exception {
    if (!ServletFileUpload.isMultipartContent(req)) {
      throw new IllegalArgumentException("Must specify enctype=\"multipart/form-data\" in form definition."
                                         +" See http://www.w3.org/TR/html401/interact/forms.html#h-17.13.4");
    }
    final FileItemFactory factory = new FileItemFactory() {
        public FileItem createItem(final String fieldName, final String contentType,
                                   final boolean isFormField, final String fileName) {
          logger.info("Documents: handlePost: createItem: "+ fieldName);
          // TODO(pmy): clean up isFormField usage in related classes.
          return new DataStoreFileItem(contentType, fieldName, fileName, isFormField);
        }
      };
    final ServletFileUpload upload = new ServletFileUpload(factory);
    final List<FileItem> items = upload.parseRequest(req);

    if (items == null)
      throw new NullPointerException("Empty form yeilds no (null) items.");

    final List<DocumentField> fields = new ArrayList<DocumentField>();
    String format = null, id = null;
    for (final FileItem item : items) {
      final String fieldName = item.getFieldName();
      final String fieldValue = new String(item.get());
      if (fieldName.equals("format")) {
        format = fieldValue;
        continue;
      }
      if (fieldName.equals("id")) {
        id = fieldValue;
        continue;
      }
      fields.add(new DocumentField(fieldName, fieldValue));
    }
    if (format == null)
      throw new IllegalArgumentException("Must specify a format name.");

    matchingFormat(format); // TODO(pmy): needed?
    MultiPartDocument doc;
    if (id == null) {
      doc = new MultiPartDocument(format);
      doc.getFormat(); // TODO(pmy): needed?
      for (final DocumentField field : fields)
        doc.addField(field);
    } else {
      doc = get(Integer.parseInt(id) - 1);
      doc.getFormat(); // TODO(pmy): needed?
      final List<DocumentField> docFields = doc.getFields();
      for (int i = 0; i < docFields.size(); i++) {
        final DocumentField docField = docFields.get(i);
        final DocumentField newField = fields.get(i);
        docField.setValue(newField.getValue());
      }
    }
    save(doc);
    req.setAttribute("formatName", format);
    req.setAttribute("showDocs", true);
    return pageList(req, rsp, format);
  }
}