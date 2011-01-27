<%@page import="wiki.*,java.net.URLEncoder,java.util.*,org.xml.sax.SAXParseException"%>
<%
  final MultiPartDocument doc = (MultiPartDocument) request.getAttribute("doc");
  if (doc == null) {
     throw new IllegalStateException("req.getAttribute('doc') == null");
  }
  final Format format = Formats.lookupFormat(doc.getFormat());
  if (format == null) {
    throw new IllegalStateException("Document format not known: "+ doc.getFormat());
  }
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
      <ul class="tabs">
        <li class="activeTab">Document</li>
        <jsp:include page="search.jsp"/>
      </ul>
      <div id="formatBox" class="box tabbed activeTabbed">
        <img src="http://chart.apis.google.com/chart?chs=150x150&cht=qr&chl=<%= URLEncoder.encode(request.getRequestURL().toString(), "UTF-8") %>&choe=UTF-8" style="float: right">
        <p><a href="/wiki/<%= Util.encodeForDoubleQuotedAttribute(doc.getFormat()) %>">
          « Return to dataset
        </a></p>
        <h2>Fields</h2>
        <div style="width: 50%">
          <form action="/wiki/<%= Util.encodeForDoubleQuotedAttribute(doc.getFormat()) %>/<%= Util.encodeForHTML(doc.getId()+"") %>" method="POST" enctype="multipart/form-data">
            <table class="form">
<%
  for (final FormField formField : format.getFields()) {
    final DocumentField docField = docFields.get(formField.getName());
    final String fieldName = Util.encodeForHTML(formField.getName());
    final String fieldText = Util.encodeForHTML(formField.getText());
    final String fieldValue = Util.encodeForHTML(docField == null ? "" : docField.getValue());
%>
              <tr>
                <td><label for="<%= fieldName %>"><%= fieldText %></label>:</td>
                <td><input name="<%= fieldName %>" value="<%= fieldValue %>"/></td>
              </tr>
<%
  }
%>
              <tr>
                <td colspan="2">
                  <div class="buttons formButtons">
                    <input type="submit" value="Save"/>
                    <input type="reset" value="Reset"/>
                  </div>
                </td>
              </tr>
            </table>
          </form>
        </div>
        <h2>XML</h2>
        <pre><%= Util.encodeForHTML(XmlSerializer.toXml(doc, format)) %></pre>
      </div>
    </div>
  </body>
</html>
