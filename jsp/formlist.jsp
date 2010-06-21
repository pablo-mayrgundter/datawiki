<%@page import="wiki.*,java.util.List"%>
<ul class="formats">
<%
   for (final Format format : new Formats().asList()) {
     if (format == null) // TODO(pmy): still required?
       continue;
  String title = format.getTitle();
  if (title == null || title.trim().equals(""))
    title = format.getName().toUpperCase();
  title = "<a href=\"/wiki/documents?format="+ format.getName() +"\">"+ title +"</a>";
%>
  <li>
    <h2><%= title %></h2>
    <p><%= format.getDescription() %></p>
  </li>
<%
  }
%>
</ul>
