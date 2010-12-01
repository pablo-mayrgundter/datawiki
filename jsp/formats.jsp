<%@page import="java.util.List,wiki.Format,wiki.Formats,wiki.Util" %>
<html>
  <head>
    <title>Format Listing</title>
    <script src="http://www.google.com/jsapi" type="text/javascript"></script>
    <script src="/Translate.js" type="text/javascript"></script>
    <script src="/Formats.js" type="text/javascript"></script>
    <link rel="stylesheet" href="/formats.css" type="text/css"/>
  </head>
  <body onload="Formats()">
    <jsp:include page="onebar.jsp"/>
    <jsp:include page="nav.jsp"/>
    <div class="mainPanel trans">
      <h2>Featured Datasets</h2>
      <ul class="formats">
<%
  final List<Format> formats = new Formats().asList();
  for (int i = formats.size() - 1; i >= 0; i--) {
    final Format format = formats.get(i);
    if (format == null) { // TODO(pmy): still required?
      continue;
    }
    String title = format.getTitle();
    if (title == null || title.trim().equals("")) {
      title = format.getName().toUpperCase();
    }
    title = title.replaceAll("_", " ");
    title = "<a href=\"/wiki/"+ Util.encodeForDoubleQuotedAttribute(format.getURLTitle()) +"\">"
            + Util.encodeForHTML(title) +"</a>";
%>
        <li>
          <h2><%= title %></h2>
          <p><%= Util.encodeForHTML(format.getDescription()) %></p>
        </li>
<%
  }
%>
      </ul>
      <a href="/wiki/formats?action=new"><button class="button plus text"><div></div>Create New</button></a>
    </div>
  </body>
</html>
