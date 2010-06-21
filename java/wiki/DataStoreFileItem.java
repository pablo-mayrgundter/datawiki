package wiki;

import com.google.appengine.api.datastore.Blob;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PersistenceCapable;
import org.apache.commons.fileupload.FileItem;

/**
 * @author Pablo Mayrgundter
 */
public class DataStoreFileItem implements FileItem {

  static class OutputStreamConvertor extends ByteArrayOutputStream {
    final DataStoreFileItem item;
    OutputStreamConvertor(final DataStoreFileItem item) {
      System.err.println("DataStoreFileItem: OutputStreamConvertor: ctor: in");
      this.item = item;
    }
    public void close() throws IOException {
      super.close();
      System.err.println("DataStoreFileItem: OutputStreamConvertor: close: buf: "+ new String(toByteArray()));
      item.content = new Blob(toByteArray());
    }
  }

  // TODO(pmy): using Blob for convenience.
  Blob content;
  String contentType;
  String fieldName;
  String fileName;
  // TODO(pmy): what does this do?
  boolean isFormField;

  public DataStoreFileItem(final String contentType, final String fieldName,
                           final String fileName, final boolean isFormField) {
    content = new Blob(new byte[]{});
    this.contentType = contentType;
    this.fieldName = fieldName;
    this.fileName = fileName;
    this.isFormField = isFormField;
  }

  /**
   * @return The contents of the file item as an array of bytes.
   */
  public byte [] get() {
    return content.getBytes();
  }

  /**
   * @return The mime-type of the document or null if not known.
   */
  public String getContentType() {
    return contentType;
  }

  /**
   * @return An InputStream that can be used to retrieve the contents
   * of the file.
   */
  public InputStream getInputStream() {
    return new ByteArrayInputStream(content.getBytes());
  }

  /**
   * @return An OutputStream that can be used for storing the contents
   * of the file.
   */
  public OutputStream getOutputStream() {
    return new OutputStreamConvertor(this);
  }


  /**
   * @return The size of the file item.
   */
  public long getSize() {
    return content.getBytes().length;
  }

  /**
   * @return The contents of the file item as a String, using the
   * default character encoding.
   */
  public String getString() {
    return new String(content.getBytes());
  }

  /**
   * @return The contents of the file item as a String, using the
   * specified encoding.
   */
  public String getString(final String encoding) {
    try {
      return new String(content.getBytes(), encoding);
    } catch (final UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * @return The name of the field in the multipart form corresponding
   * to this file item.
   */
  public String getFieldName() {
    return fieldName;
  }

  /**
   * Sets the field name used to reference this file item.
   */
  public void setFieldName(final String name) {
    this.fieldName = name;
  }

  /**
   * Deletes the underlying storage for a file item, including
   * deleting any associated temporary disk file.
   */
  public void delete() {
    throw new UnsupportedOperationException();
  }

  /**
   * Provides a hint as to whether or not the file contents will be
   * read from memory.
   */
  public boolean isInMemory() {
    return true;
  }

  /**
   * Determines whether or not a FileItem instance represents a simple
   * form field.
   *
   * TODO(pmy): what is this used for?
   */
  public boolean isFormField() {
    return isFormField;
  }

  /**
   * Specifies whether or not a FileItem instance represents a simple
   * form field.
   */
  public void setFormField(final boolean state) {
    this.isFormField = state;
  }

  /**
   * @return The original filename in the client's filesystem, as
   * provided by the browser (or other client software).
   */
  public String getName() {
    return fileName;
  }

  /**
   * A convenience method to write an uploaded item to disk.
   */
  public void write(File file) {
    throw new UnsupportedOperationException();
  }
}