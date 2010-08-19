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
      // Needs to be called before page loaded.
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
    <style>
      body {
        font-family: helvetica, arial, sans;
        margin: 0;
        padding: 0;
      }
      .header > h1 {
        display: inline;
        margin: 0.25em;
      }
      .onebar {
        display: inline;
        position: absolute;
        right: 0;
        padding: 0.5em;
      }
      .header * {
        white-spac: nowrap;
      }
      #content {
        position: absolute;
        width: 100%;
        height: 95%;
        top: 5%;
      }
      #listChart, #mapChart {
        width: 50%;
        height: 100%;
      }
      #listChart {
        left: 0;
      }
      #mapChart {
        float: right;
      }
      input {
        border: none;
      }
    </style>
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
