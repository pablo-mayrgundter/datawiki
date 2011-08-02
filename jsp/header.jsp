<%@page import="wiki.Properties"%>
<%
  if (Properties.getBoolean("online")) {
%>
    <script src="http://www.google.com/jsapi" type="text/javascript"></script>
    <script src="/Translate.js" type="text/javascript"></script>
<%
  }
%>
    <script src="/Wiki.js" type="text/javascript"></script>
