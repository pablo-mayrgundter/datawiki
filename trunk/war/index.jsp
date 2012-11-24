<%@page import="java.util.List,wiki.*" %>
<html ng-app="datawiki">
  <head>
    <title>DataWiki</title>
    <jsp:include page="header.jsp"/>
    <link rel="stylesheet" href="index.css" type="text/css"/>
  </head>
  <body ng-controller="WikiCtrl">
    <div ng-view></div>
  </body>
</html>
