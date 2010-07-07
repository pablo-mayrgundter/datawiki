<%
  String siteName = request.getParameter("siteName");
  if (siteName == null)
    siteName = "ReliefHub";
%>
<html>
  <head>
    <link rel="stylesheet" href="index.css" type="text/css"/>
    <style>
      .mainPanel {
        width: 600px;
      }
    </style>
  </head>
  <body>
    <jsp:include page="nav.jsp"/>
    <div class="mainPanel">
      <h1>ReliefHub</h1> 
      <p>This app is a test instance of
      of <a href="http://code.google.com/p/datawiki/">DataWiki</a> for
      use in sharing disaster response and humanitarian assistance
      data.  All data and current storage formats should be considered
      for testing use only.</p> 
    </div>
  </body>
</html>
