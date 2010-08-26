package wiki;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.FileUploadException;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

class FormUpload {
  static final FileItemFactory factory = new FileItemFactory() {
      public FileItem createItem(final String fieldName, final String contentType,
                                 final boolean isFormField, final String fileName) {
        // TODO(pmy): clean up isFormField usage in related classes.
        return new DataStoreFileItem(contentType, fieldName, fileName, isFormField);
      }
    };

  @SuppressWarnings("unchecked")
  static List<FileItem> processFormData(final HttpServletRequest req) throws FileUploadException {
    if (!ServletFileUpload.isMultipartContent(req)) {
      throw new IllegalArgumentException("Must specify enctype=\"multipart/form-data\" in form definition."
                                         +" See http://www.w3.org/TR/html401/interact/forms.html#h-17.13.4");
    }
    final ServletFileUpload upload = new ServletFileUpload(factory);
    final List<FileItem> items = upload.parseRequest(req);
    if (items == null) {
      throw new IllegalStateException("Empty form yields no (null) items.");
    }
    return items;
  }
}