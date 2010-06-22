<%
  final String formatName = (String) request.getAttribute("formatName");
//  final String hostURL = Util.getHostURL(request);
//  final String self = hostURL + request.getRequestURI();
%>
<html>
  <head>
    <link rel="stylesheet" href="/format.css" type="text/css"/>
  </head>
  <body>
    <jsp:include page="nav.jsp"/>
    <div class="mainPanel">
      <ul class="tabs">
        <li>Dataset</li>
        <li class="activeTab">Format</li>
      </ul>
      <div id="formatBox" class="box">
        <h2 id="title"><%= formatName %></h2>
        <p id="description">DataWiki does not have a format with this name.  You can create it <a href="?action=edit">here</a>.</p>
      </div>
    </div>
  </body>
</html>
