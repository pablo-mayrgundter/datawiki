<%@page import="java.util.List,wiki.Format,wiki.Formats,wiki.Util" %>
<html>
  <head>
    <link rel="stylesheet" href="index.css" type="text/css"/>
    <script src="http://www.google.com/jsapi" type="text/javascript"></script>
    <script src="/Wiki.js" type="text/javascript"></script>
    <script src="/Translate.js" type="text/javascript"></script>
  </head>
  <body>
    <jsp:include page="onebar.jsp"/>
    <jsp:include page="nav.jsp"/>
    <div class="mainPanel trans">
      <ul class="tabs">
        <li class="activeTab">Welcome!</li>
        <jsp:include page="search.jsp"/>
      </ul>
      <div id="formatBox" class="box tabbed activeTabbed">
        <h2 class="trans">A wiki for structured data</h2>

        <p class="trans">DataWiki is currently in testing; all data and current
          storage formats should be used for testing purposes only.</p>

        <div id="featured">
          <!--<div id="createButton"><a href="/wiki/formats?action=new"><button class="button plus text"><div></div>Create New</button></a></div>-->
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
              <%= title %>
              <p><%= Util.encodeForHTML(format.getDescription()) %></p>
            </li>
<%
  }
%>
          </ul>
          
        </div>
      </div>
    </div>
  </body>
</html>
