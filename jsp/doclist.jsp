<%@page import="com.google.appengine.api.datastore.Cursor,
                common.Persistable,
                java.util.*,
                javax.jdo.*,
                org.datanucleus.store.appengine.query.JDOCursorHelper,
                wiki.*"%>
<%
  final String dataset = request.getParameter("dataset");
  final Format format = new Formats().withName(dataset);
  final int from = Util.getParameter(request, "from", 0);
  final int PAGE_SIZE = 10;
%>
<table id="doclist">
  <tr>
    <th width="5%">ID</th>
<% for (final FormField f : format.getFields()) { %>
    <th><%= Util.encodeForHTML(f.getText()) %></th>
<% } %>
  </tr>
<%
  final PersistenceManager pm = Persistable.pmf.getPersistenceManager();
  final Query query = pm.newQuery(MultiPartDocument.class, "format == '"+ dataset +"'");
  query.setRange(from, from + PAGE_SIZE);
  final List<MultiPartDocument> docs = (List<MultiPartDocument>) query.execute();
/*
  final Cursor cursor = JDOCursorHelper.getCursor(docs);
  final String cursorString = cursor.toWebSafeString();
*/

  for (final MultiPartDocument doc : docs) {
    final Map<String,String> docFields = new HashMap<String,String>();
    for (final DocumentField field : doc.getFields()) {
      docFields.put(field.getName(), field.getValue());
    }
%>
  <tr>
    <td><a href="docs/<%= doc.getId() %>"><%= doc.getId() %></a></td>
<%
    String lat = null, lon = null;
    for (final FormField f : format.getFields()) {

      String val = docFields.get(f.getName());
      if (val == null) {
        val = "n/a";
      } else {
        if (f.getName().equalsIgnoreCase("latitude")) {
          lat = val;
        } else if (f.getName().equalsIgnoreCase("longitude")) {
          lon = val;
        }
      }
%>
    <td><%= Util.encodeForHTML(val) %></td>
<%
    }

    if (lat != null && lon != null) {
%>
  <script>
    var latlng = new google.maps.LatLng(<%= lat %>, <%= lon %>);
    new google.maps.Marker(latlng);
  </script>
<%
    }
%>
  </tr>
<%
  }
%>
</table>
<%
  if (from > 0) {
    final int back = Math.max(0, from - PAGE_SIZE);
%>
  <a href="?from=<%= back %>">&lt;</a>
<%
  }
%>

<%
  if (docs.size() >= from + PAGE_SIZE) {
    final int to = Math.min(docs.size(), from + PAGE_SIZE);
%>
  <a href="?from=<%= to %>">&gt;</a>
<%
  }
%>
