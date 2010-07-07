<%
  String host = request.getScheme() +"://"+ request.getServerName();
  int port = request.getServerPort();
  if (port != 80)
    host += ":" + port;
  final String formatName = request.getParameter("format");
%><html> 
  <head> 
    <script src="http://www.google.com/jsapi" type="text/javascript"></script> 
    <script src="<%= host %>/Util.js" type="text/javascript"></script> 
    <script src="<%= host %>/StatsTable.js" type="text/javascript"></script> 
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
      table.td {
        vertical-align: top;
      }
    </style>
  </head>
  <body>
    <h1>Demo</h1>
    <table>
      <tr>
        <td>
          <div id="timelineChart" style="width:50%; height:90%"></div>
        </td>
        <td>
          <h2>Forms</h2>
          <h3>Find</h3>
          <form action="http://localhost:8080/wiki/documents?format=test" method="GET">
            foo: <input name="foo"><br/>
            bar: <input name="bar"><br/>
            <input type="Submit"><input type="Reset">
            <input name="q" type="hidden">
            <input name="format" value="test" type="hidden">
          </form>
          <h3>Create</h3>
          <form action="http://localhost:8080/wiki/documents?format=test" method="POST" enctype="multipart/form-data">
            foo: <input name="foo"><br/>
            bar: <input name="bar"><br/>
            <input type="Submit"><input type="Reset">
            <input name="format" value="test" type="hidden">
          </form>
        </td>
      </tr>
      <tr>
        <td width="50%" valign="top">
          <div id="listChart"></div>
        </td>
        <td width="50%">
          <div id="mapChart"></div>
        </td>
      </tr>
    </table>
  </body>
</html>
