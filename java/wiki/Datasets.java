package wiki;

import common.PersistentList;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
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
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * TODO(pmy): generalize this to AbstractDocument, or store datasets
 * as XML and use objects for indices.  Also, the flow between
 * POST/GET and pageList could probably make better use of dispatchers
 * so that pageList is a real entry point and can be dispatched to.
 */
@Path("/")
public class Datasets extends PersistentList<Dataset> {

  static final Logger logger = Logger.getLogger(Datasets.class.getName());

  static final String JSP_COLLECTION = "/dataset.jsp";
  static final String JSP_COLLECTION_XML = "/documentsXml.jsp";
  static final String JSP_UNKNOWN = "/unknown.jsp";

  public Datasets() {
    super(Dataset.class);
  }

  // Methods grouped below by HTTP verb.

  // GET methods follow.

  /** Search */
  @GET
  @Produces({"text/html; charset=utf-8"})
  public Response searchDatasets(@Context HttpServletRequest req,
                                 @Context HttpServletResponse rsp)
    throws ServletException, IOException {
    final String reqQuery = req.getParameter("q");
    if (reqQuery == null || reqQuery.length() == 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Must specify a format.").build();
    }
    final Format format = new Formats().withName(reqQuery);
    if (format == null) {
      return Formats.formatWithTitleNotFound(reqQuery, req, rsp);
    }
    return getDocsInDataset(req, rsp, format.getName());
  }

  /** Overview for a dataset. */
  @GET
  @Path("{dataset}")
  @Produces({"text/html; charset=utf-8"})
  public Response getDocsInDataset(@Context HttpServletRequest req,
                                   @Context HttpServletResponse rsp,
                                   @PathParam("dataset") String datasetName)
    throws ServletException, IOException {
    Dataset dataset = withName(datasetName);
    if (dataset == null) {
      final Format format = new Formats().withName(datasetName);
      if (format == null) {
	return Formats.formatWithTitleNotFound(datasetName, req, rsp);
      }
      dataset = new Dataset(format);
    }
    return pageList(req, rsp, dataset);
  }

  /** @return The format or null if not found. */
  public Dataset withName(final String name) {
    final List<Dataset> datasets = query(Dataset.gqlFilterForMatchingFormat(name));
    if (datasets.size() == 0)
      return null;
    return datasets.get(0);
  }

  // POST methods follow.  If the user submits from a form, they are
  // returned to the dataset page since this is where the forms are.

  /**
   * Create doc from form post.
   */
  @POST
  @Path("{dataset}")
  @Consumes({"multipart/form-data"})
  @Produces({"text/html; charset=utf-8"})
  public Response handlePost(@Context HttpServletRequest req,
                             @Context HttpServletResponse rsp,
                             @PathParam("dataset") String datasetName) throws Exception {
    List<FileItem> items = null;
    try {
      items = FormUpload.processFormData(req);
    } catch (FileUploadException e) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Submission could not be parsed: "+ e).build();
    }
    final Format format = new Formats().withName(datasetName);
    if (format == null) {
      return Formats.formatWithTitleNotFound(datasetName, req, rsp);
    }
    final LinkedHashMap<String,DocumentField> fields = Documents.fileItemsToDocumentFields(items);
    final MultiPartDocument doc = new MultiPartDocument(format.getName());
    for (final String fieldName : fields.keySet()) {
      doc.addField(fields.get(fieldName));
    }

    // TODO(pmy): pass in XML from client as well?
    final String xml = XmlSerializer.toXml(doc, format);
    doc.setXml(xml);
    new Documents().save(doc);

    return getDocsInDataset(req, rsp, datasetName);
  }

  /** Create doc from XML. */
  @POST
  @Path("{dataset}")
  @Consumes({"application/x-www-form-urlencoded"})
  public Response handlePostXml(@Context HttpServletRequest req,
                                @Context HttpServletResponse rsp,
                                @PathParam("dataset") String datasetName,
                                String xml) throws Exception {
    final Format format = new Formats().withName(datasetName);
    if (format == null) {
      return Formats.formatWithTitleNotFound(datasetName, req, rsp);
    }

    MultiPartDocument doc;
    try {
      doc = XmlSerializer.docFromXml(xml, format.getSchema());
    } catch (SAXParseException schemaException) {
      return Response.status(Response.Status.BAD_REQUEST).entity("The submitted XML does not match this format: "
                                                                 + schemaException).build();
    } catch (SAXException encodingException) {
      return Response.status(Response.Status.BAD_REQUEST).entity("The submitted document is not valid XML: "
                                                                 + encodingException).build();
    }

    new Documents().save(doc);

    return Response.ok("Document created.").build();
  }

  // Helpers.

  Response pageList(final HttpServletRequest req, final HttpServletResponse rsp, final Dataset dataset)
    throws ServletException, IOException {
    req.setAttribute("dataset", dataset);
    req.setAttribute("format", new Formats().withName(dataset.getFormat()));
    // TODO(pmy): hack for craig, move this to @Produces control.
    final String output = req.getParameter("output");
    if (output != null && output.equals("xml")) {
      req.getRequestDispatcher(JSP_COLLECTION_XML).include(req, rsp);
    } else {
      req.getRequestDispatcher(JSP_COLLECTION).include(req, rsp);
    }
    return Response.ok().build();
  }
}
