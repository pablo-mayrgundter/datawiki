<%@page import="java.util.List,wiki.*" %>
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

        <p class="trans">DataWiki is currently in testing; all data
          and current storage formats should be used for testing
          purposes only. DataWiki currently has <%= new
          Documents().size() %> documents in <%= new
          Formats().size() %> formats.</p>
	  
        <div id="featured">
          <!--<div id="createButton"><a href="/wiki/formats?action=new"><button class="button plus text"><div></div>Create New</button></a></div>-->
          <h2>Featured Datasets</h2>
          <jsp:include page="formats.jsp">
            <jsp:param name="showFeatured" value="true"/>
          </jsp:include>
          <h2>Recent Datasets</h2>
          <jsp:include page="formats.jsp"/>
        </div>
      </div>
    </div>
  </body>
</html>
