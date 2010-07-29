<%@page import="wiki.*,java.net.URLEncoder,java.util.HashMap,java.util.Map"%>
<%
  final MultiPartDocument doc = (MultiPartDocument) request.getAttribute("doc");
  if (doc == null)
     throw new IllegalStateException("req.getAttribute('doc') == null");
  final Format format = Formats.lookupFormat(doc.getFormat());
  if (format == null)
    throw new IllegalStateException("Document format not known: "+ doc.getFormat());
  final Map<String, DocumentField> docFields = new HashMap<String, DocumentField>();
  for (final DocumentField field : doc.getFields()) {
    docFields.put(field.getName(), field);
  }
%>
<html>
  <head>
    <script src="http://www.google.com/jsapi" type="text/javascript"></script>
    <link rel="stylesheet" href="/documents.css" type="text/css"/>
    <script src="/Translate.js" type="text/javascript"></script>
  </head>
  <body onload="translateInit('langSelect')">
    <jsp:include page="onebar.jsp"/>
    <jsp:include page="nav.jsp"/>
    <div class="mainPanel trans">
      <p><a href="/wiki/<%= doc.getFormat() %>"><%= doc.getFormat() %></a> &gt; <%= doc.getId() %>
      <p>&nbsp;</p>
      <h2>Fields</h2>
      <form action="/wiki/<%= doc.getFormat() %>/<%= doc.getId() %>" method="POST" enctype="multipart/form-data">
        <table>
<%
  for (final FormField formField : format.getFields()) {
    final DocumentField docField = docFields.get(formField.getName());
%>
          <tr>
            <td><%= formField.getText() %>:</td>
            <td><input name="<%= formField.getName() %>" value="<%= docField == null ? "" : docField.getValue() %>"/></td>
          </tr>
<%
  }
%>
        </table>
        <div class="buttons">
          <input type="submit" value="Save"/>
          <input type="reset" value="Clear"/>
        </div>
      </form>
      <p>&nbsp;</p>
      <h2>Scannable Link To This Page</h2>
      <img src="http://chart.apis.google.com/chart?chs=150x150&cht=qr&chl=<%= URLEncoder.encode(request.getRequestURL().toString(), "UTF-8") %>&choe=UTF-8">
    </div>
  </body>
</html>
