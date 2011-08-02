<%@page import="wiki.Properties,wiki.Util"%>
<%
  if (!request.getServerName().equalsIgnoreCase("localhost")) {
%>
Properties can only be set from localhost.
<%
    return;
  }
  final String reqKey = request.getParameter("key");
  final String reqVal = request.getParameter("val");
  if (reqKey != null) {
    final String curVal = Properties.getProperty(reqKey);
%>
<html>
  <head>
    <style>
      table { border-collapse: collapse; }
      td {
        border: solid grey 1px;
        padding: 5px;
      }
    </style>
  </head>
  <body>
    <table>
      <tr>
        <td>Key</td><td><%= reqKey %></td>
      </tr>
      <tr>
        <td>Current Value</td><td><%= curVal %></td>
      </tr>
<%
    if (reqVal != null) {
      Properties.setProperty(reqKey, reqVal);
%>
      <tr>
        <td>New Value</td><td><%= reqVal %></td>
      </tr>
<%
    }
%>
    </table>
<%
  }
%>
  </body>
</html>
