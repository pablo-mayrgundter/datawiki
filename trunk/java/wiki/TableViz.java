package wiki;

import com.google.visualization.datasource.DataSourceServlet;
import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableCell;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.datatable.value.NumberValue;
import com.google.visualization.datasource.datatable.value.DateTimeValue;
import com.google.visualization.datasource.query.Query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Adapted from Google Visualization SimpleExampleServlet. */
public class TableViz extends DataSourceServlet {

  static final Logger logger = Logger.getLogger(TableViz.class.getName());

  protected void doGet(final HttpServletRequest req, final HttpServletResponse rsp)
    throws IOException {
    super.doGet(req, rsp);
    rsp.setHeader("Content-Type", "application/json");
  }

  public DataTable generateDataTable(final Query query, final HttpServletRequest request) {

    // TODO(pmy): choose either attribute or parameter passing.
    final String reqFormatName = request.getParameter("format");
    if (reqFormatName == null)
      throw new IllegalStateException("Missing format name");

    final Format format = Formats.lookupFormat(reqFormatName);
    if (format == null)
      throw new IllegalArgumentException("Format not found");

    final List<MultiPartDocument> matchingDocs =
      Documents.queryOrAll(request, reqFormatName, format);

    if (request.getParameter("summary") != null) {
      return summaryTable(matchingDocs);
    } else {
      return detailTable(matchingDocs, format);
    }
  }

  DataTable summaryTable(final List<MultiPartDocument> docs) {
    final DataTable data = new DataTable();
    final List<ColumnDescription> cd = new ArrayList<ColumnDescription>();
    cd.add(new ColumnDescription("created", ValueType.DATETIME, "Created"));
    cd.add(new ColumnDescription("count", ValueType.NUMBER, "Count"));
    data.addColumns(cd);
    final SortedMap<DateTimeValue,Integer> dateCounts = new TreeMap<DateTimeValue,Integer>();
    for (final MultiPartDocument doc : docs) {
      final DateTimeValue createDate = dateToDateTime(doc.getCreatedDate(), Calendar.MINUTE);
      Integer count = dateCounts.get(createDate);
      if (count == null)
        dateCounts.put(createDate, count = 0);
      dateCounts.put(createDate, ++count);
    }
    for (final Map.Entry<DateTimeValue,Integer> dateCount : dateCounts.entrySet()) {
      final DateTimeValue date = dateCount.getKey();
      final Integer count = dateCount.getValue();
      final TableRow row = new TableRow();
      row.addCell(date);
      row.addCell(new NumberValue(count));
      try {
        data.addRow(row);
      } catch (TypeMismatchException e) {
        throw new IllegalStateException(e);
      }
    }
    return data;
  }

  DataTable detailTable(final List<MultiPartDocument> docs, final Format format) {
    final DataTable data = new DataTable();
    final LinkedList<ColumnDescription> cols = new LinkedList<ColumnDescription>();
    ColumnDescription latitudeCol = null, longitudeCol = null;
    for (final FormField field : format.getFields()) {
      if (field.getType() == FormField.Type.Latitude || field.getName().equals("latitude")) {
        latitudeCol = new ColumnDescription(field.getName(), ValueType.NUMBER, field.getText());
        continue;
      }
      if (field.getType() == FormField.Type.Longitude || field.getName().equals("longitude")) {
        longitudeCol = new ColumnDescription(field.getName(), ValueType.NUMBER, field.getText());
        continue;
      }
      cols.add(new ColumnDescription(field.getName(), ValueType.TEXT, field.getText()));
    }
    // TODO(pmy): handle only one being here, i.e. convert to a single GeoPoint objectn.
    if (latitudeCol != null && longitudeCol != null) {
      data.setCustomProperty("hasMap", "true");
    }
    boolean hasLatLon = false; // TODO(pmy): currently possible to have only one.
    if (latitudeCol != null) {
      data.addColumn(latitudeCol);
      hasLatLon |= true;
    }
    if (longitudeCol != null) {
      data.addColumn(longitudeCol);
      hasLatLon &= true;
    }
    // Used for map view only.  Use ID as stored field and show summary as formatted version.
    if (hasLatLon) {
      data.addColumn(new ColumnDescription("summary", ValueType.NUMBER, "Summary"));
    }
    data.addColumn(new ColumnDescription("id", ValueType.NUMBER, "Edit"));
    data.addColumn(new ColumnDescription("created", ValueType.DATETIME, "Created"));
    data.addColumn(new ColumnDescription("updated", ValueType.DATETIME, "Updated"));
    data.addColumns(cols);

    int rowCount = 0;
    for (final MultiPartDocument doc : docs) {
      final Map<String,String> docFields = new HashMap<String,String>();
      for (final DocumentField field : doc.getFields()) {
        docFields.put(field.getName(), field.getValue());
      }
      final TableRow row = new TableRow();
      if (latitudeCol != null) {
        double val = Double.NaN;
        // TODO(pmy): missing values handled as NaN;
        try {
          val = Double.parseDouble(docFields.get(latitudeCol.getId()));
        } catch (Exception e) {}
        row.addCell(val);
      }
      if (longitudeCol != null) {
        double val = Double.NaN;
        try {
          val = Double.parseDouble(docFields.get(longitudeCol.getId()));
        } catch (Exception e) {}
        row.addCell(val);
      }

      // Compute a summary even though it may not be used if !hasLatLon.
      String summary = "<table class=\"chartBubble\">\n";
      TableCell summaryCell = null;
      if (hasLatLon) {
        // Only added if its column added above.  Now client will have
        // first 3 columns as lat, lon, summary for construction of
        // the map table.  Otherwise, it will still have lat, lon
        // first, but will jump right to ID.
        summaryCell = new TableCell(new NumberValue(doc.getId()));
        row.addCell(summaryCell);
      }

      // Note, displayed id row id, not instance id.  instance id is
      // still available in client table.
      //String itemLink = String.format("<a href=\"\" onclick=\"editItem(this, '%d');return false;\">%d</a>",
      String itemLink = String.format("<a href=\"/wiki/%s/%d\">%d</a>",
                                      format.getName(), doc.getId(), rowCount, rowCount);
      row.addCell(new TableCell(new NumberValue(doc.getId()), itemLink));
      summary += String.format("  <tr><td>ID:</td><td>%s</td></tr>\n", itemLink);


      row.addCell(dateToDateTime(doc.getCreatedDate()));
      summary += String.format("  <tr><td>Created:</td><td>%s</td></tr>\n", doc.getCreatedDate());

      row.addCell(dateToDateTime(doc.getUpdatedDate()));
      summary += String.format("  <tr><td>Updated:</td><td>%s</td></tr>\n", doc.getUpdatedDate());

      for (final ColumnDescription col : cols) {
        String val = docFields.get(col.getId());
        if (val == null) {
          val = "N/A"; // TODO(pmy): i18n
        }
        row.addCell(Util.encodeForHTML(val));
        summary += String.format("  <tr><td>%s:</td><td>%s</td></tr>\n", col.getId(), val);
      }
      summary += "</table>\n";
      if (hasLatLon) {
        summaryCell.setFormattedValue(summary);
      }
      try {
        data.addRow(row);
      } catch (TypeMismatchException e) {
        logger.warning("Field mismatch in doc "+ doc.getId() +", nested: "+ e);
        continue;
      }
      rowCount++;
    }
    return data;
  }

  DateTimeValue dateToDateTime(final Date date) {
    return dateToDateTime(date, -1);
  }

  /**
   * All time fields including and shorter than the given timeBucket
   * will be set to 0 in the returned DateTimeValue.
   *
   * @param timeBucket Must be a Calendar contant field value between
   * or including MONTH and MILLISECONDS or specify a value of -1 for
   * no bucketing.
   */
  DateTimeValue dateToDateTime(final Date date, final int timeBucket) {
    int month, day, hour, min, sec, mil;
    month = day = hour = min = sec = mil = 1;
    switch (timeBucket) {
    case Calendar.MONTH: month = 0;
    case Calendar.DAY_OF_MONTH: day = 0;
    case Calendar.HOUR: hour = 0;
    case Calendar.MINUTE: min = 0;
    case Calendar.SECOND: sec = 0;
    case Calendar.MILLISECOND: mil = 0;
    }
    final GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT-8"));
    cal.setTime(date);
    return new DateTimeValue(cal.get(Calendar.YEAR), month * cal.get(Calendar.MONTH),
                             day == 0 ? 1 : cal.get(Calendar.DAY_OF_MONTH), hour * cal.get(Calendar.HOUR),
                             min * cal.get(Calendar.MINUTE), sec * cal.get(Calendar.SECOND),
                             mil * cal.get(Calendar.MILLISECOND));
  }

  /**
   * NOTE: By default, this function returns true, which means that cross
   * domain requests are rejected.
   * This check is disabled here so examples can be used directly from the
   * address bar of the browser. Bear in mind that this exposes your
   * data source to xsrf attacks.
   * If the only use of the data source url is from your application,
   * that runs on the same domain, it is better to remain in restricted mode.
   */
  @Override
  protected boolean isRestrictedAccessMode() {
    return false;
  }
}
