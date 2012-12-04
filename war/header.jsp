<%@page import="wiki.Properties"%>
<%
  boolean online = Properties.getBoolean("online");
  // Whether the dataset has geodata.
  boolean hasGeo = false; // TODO(pmy)
  // Location of offline geo server if present.
  String geepServer = Properties.getProperty("geep.server");
  boolean hasGeep = geepServer != null;
  boolean showGeo = hasGeo && (online || hasGeep);
%>
<%
  if (Properties.getBoolean("online")) {
%>
    <script src="http://www.google.com/jsapi" type="text/javascript"></script>
<%
  }
%>
    <script src="/util.js" type="text/javascript"></script>
    <script src="angular-1.0.2.js"></script>
    <script src="angular-resource-1.0.2.js"></script>
    <script src="/wiki.js" type="text/javascript"></script>
<% if (online && showGeo) { %>
    <script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>
<% } %>
    <script src="/Documents.js" type="text/javascript"></script>
    <script src="/FormEditor.js" type="text/javascript"></script>
    <link href="/documents.css" rel="stylesheet" type="text/css"/>
