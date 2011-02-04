<%@page import="wiki.*"%>
<%
  final Format format = (Format) request.getAttribute("format");
  String unsafeTitle = format.getTitle();
  if (unsafeTitle == null || unsafeTitle.equals("")) {
    unsafeTitle = format.getName().toUpperCase();
  }
  String title = Util.encodeForHTML(unsafeTitle);
  if (request.getParameter("includeHref") != null
      && request.getParameter("includeHref").equalsIgnoreCase("true")) {
    title =
      "<a href=\"/wiki/"+ Util.encodeForDoubleQuotedAttribute(format.getURLTitle()) +"\">"
      + title +"</a>";
  }
%>
<h2 id="title"><%= title %></h2>
<p id="description"><%= Util.encodeForHTML(format.getDescription()) %></p>
