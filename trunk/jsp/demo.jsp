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
    <script src="<%= host %>/Util.js" type="text/javascript"></script> 
    <script src="<%= host %>/StatsTable.js" type="text/javascript"></script> 
    <script src="<%= host %>/Translate.js" type="text/javascript"></script>
    <script src="<%= host %>/Viz.js" type="text/javascript"></script> 
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
      table, tr, td {
        margin: 0;
        padding: 0;
        border-spacing: 0;
      }
      table {
        width: 100%;
        height: 100%;
      }
      tr.header {
        height: 1em;
      }
      tr.header h1 {
        font-size: 1.1em;
        margin: 0;
        padding: 0.5em;
      }
      tr.main > td {
        vertical-align: top;
      }
      .onebar {
        float: right;
        padding: 0.5em;
      }
      .nobr {
        text-spacing: no-break;
      }
    </style>
  </head>
  <body onload="translateInit('langSelect')">
    <table>
      <tr class="header">
        <td><h1><%= formatName %></h1></td>
        <td>
          <div class="onebar">
<%
  final String loginContinuePage = request.getRequestURI() +"?format="+ formatName;
%>
            <jsp:include page="signin.jsp">
              <jsp:param name="uri" value="<%= loginContinuePage %>"/>
            </jsp:include>
          </div>
          <span class="nobr">Language:<span id="langSelect"></span></span>
        </td>
      </tr>
      <tr class="main">
        <td width="40%">
          <div id="listChart" class="trans"></div>
        </td>
        <td width="60%">
          <div id="mapChart"></div>
        </td>
      </tr>
    </table>
  </body>
</html>
