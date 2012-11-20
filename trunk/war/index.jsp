<%@page import="java.util.List,wiki.*" %>
<html ng-app="datawiki">
  <head>
    <title>DataWiki</title>
    <jsp:include page="header.jsp"/>
    <link rel="stylesheet" href="index.css" type="text/css"/>
  </head>
  <body ng-controller="WikiCtrl">
    <jsp:include page="onebar.jsp"/>
    <jsp:include page="nav.jsp"/>
    <div ng-view></div>
  </body>
</html>
