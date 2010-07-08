<%
  String siteName = request.getParameter("siteName");
  if (siteName == null)
    siteName = "ReliefHub";
%>
<html>
  <head>
    <link rel="stylesheet" href="index.css" type="text/css"/>
    <script src="http://www.google.com/jsapi" type="text/javascript"></script>
    <script src="/Translate.js" type="text/javascript"></script>
    <style>
      .mainPanel {
        width: 600px;
      }
    </style>
  </head>
  <body onload="translateInit('langSelect')">
    <jsp:include page="onebar.jsp"/>
    <jsp:include page="nav.jsp"/>
    <div class="mainPanel">
      <h1>ReliefHub</h1> 
      <p class="trans">This app is a test instance of
      of <a href="http://code.google.com/p/datawiki/">DataWiki</a> for
      use in sharing disaster response and humanitarian assistance
      data.  All data and current storage formats should be considered
      for testing use only.</p> 
    </div>
  </body>
</html>
