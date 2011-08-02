<%@page import="wiki.*,java.net.URLEncoder,java.util.*,org.xml.sax.SAXParseException"%>
<%
  final MultiPartDocument doc = (MultiPartDocument) request.getAttribute("doc");
  if (doc == null) {
     throw new IllegalStateException("req.getAttribute('doc') == null");
  }
  final Format format = new Formats().withName(doc.getFormat());
  if (format == null) {
    throw new IllegalStateException("Document format not known: "+ doc.getFormat());
  }
  final Map<String, DocumentField> docFields = new HashMap<String, DocumentField>();
  for (final DocumentField field : doc.getFields()) {
    docFields.put(field.getName(), field);
  }
  final String hostURL = Util.getHostURL(request);
  final String selfURL = hostURL + request.getRequestURI();
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
        <div style="float: right; text-align: center">
          <img alt="<%= selfURL %>/qrcode" src="<%= selfURL %>/qrcode" style="margin-bottom: 1em"/><br/>
          Scannable link to this page
        </div>
        <p><a href="/wiki/<%= Util.encodeForDoubleQuotedAttribute(doc.getFormat()) %>">
          « Return to dataset
        </a></p>
        <h2>Fields</h2>
        <div style="width: 50%">
          <form action="/wiki/docs/<%= Util.encodeForHTML(doc.getId()+"") %>" method="POST" enctype="multipart/form-data">
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
                <td>
<%
   if (fieldName.equals("shape") && fieldValue.length() != 0) {
%>
                  <img src="/wiki/docs/<%= doc.getId() %>/<%= fieldName%>/tile"/>
                  <!--<textarea name="<%= fieldName %>" cols=30 rows=10><%= fieldValue %></textarea>-->
<%
   }
%>
                  <input name="<%= fieldName %>" value="<%= fieldValue %>"/>
                </td>
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
        <h2 style="clear: right">XML</h2>
        <pre><%= Util.encodeForHTML(XmlSerializer.toXml(doc, format)) %></pre>
        or:<br/>
        <a href="<%= hostURL + request.getRequestURI() %>?output=xml"><%= hostURL + request.getRequestURI() %>?output=xml</a>
      </div>
    </div>
  </body>
</html>
