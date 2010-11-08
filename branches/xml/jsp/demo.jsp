<%
  String host = request.getScheme() +"://"+ request.getServerName();
  int port = request.getServerPort();
  if (port != 80)
    host += ":" + port;
  final String formatName = request.getParameter("format");
%><html> 
  <head>
    <title>Dataset: <%= formatName %></title>
    <script src="http://www.google.com/jsapi" type="text/javascript"></script> 
    <script src="<%= host %>/Wiki.js" type="text/javascript"></script> 
    <script src="<%= host %>/Translate.js" type="text/javascript"></script>
    <script type="text/javascript">
      // This block should be kept inline to ensure it is called
      // before onload.
      try {
        if (google != null) {
          google.load('visualization', '1', {'packages':['corechart','table','map']});
          vizFormat = '<%= formatName %>'; // required for vizInit;
          google.setOnLoadCallback(vizQuery);
        }
      } catch(e) {
        alert('This page requires resources from the internet that are currently unavailable.');
      }
    </script>
    <script type="text/javascript">
    </script>
    <link rel="stylesheet" href="<%= host %>/demo.css" type="text/css"/>
  </head>
  <body onload="translateInit('langSelect')">
    <div class="header">
      <h1><%= formatName %></h1>
      <jsp:include page="onebar.jsp"/>
    </div>
    <div id="content" class="trans">
      <div id="mapChart"></div>
      <div id="listChart"></div>
    </div>
  </body>
</html>
