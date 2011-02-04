<%@page import="wiki.*"%>
<%
  final Dataset dataset = (Dataset) request.getAttribute("dataset");
  String unsafeTitle = dataset.getTitle();
  if (unsafeTitle == null || unsafeTitle.equals("")) {
    unsafeTitle = dataset.getName().toUpperCase();
  }
  String title = Util.encodeForHTML(unsafeTitle);
  if (request.getParameter("includeHref") != null
      && request.getParameter("includeHref").equalsIgnoreCase("true")) {
    title =
      "<a href=\"/wiki/"+ Util.encodeForDoubleQuotedAttribute(dataset.getURLTitle()) +"\">"
      + title +"</a>";
  }
%>
<h2 id="title"><%= title %></h2>
<p id="description"><%= Util.encodeForHTML(dataset.getDescription()) %></p>
