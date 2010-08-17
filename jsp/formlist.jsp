<%@page import="wiki.*,java.util.List"%>
<ul class="formats">
<%
  final List<Format> formats = new Formats().asList();
  for (int i = formats.size() - 1; i >= 0; i--) {
    final Format format = formats.get(i);
    if (format == null) { // TODO(pmy): still required?
      continue;
    }
    String title = format.getTitle();
    if (title == null || title.trim().equals("")) {
      title = format.getName().toUpperCase();
    }
    title = "<a href=\"/wiki/"+ Util.encodeForDoubleQuotedAttribute(format.getURLTitle()) +"\">"+ title +"</a>";
%>
  <li>
    <h2><%= title %></h2>
    <p><%= Util.encodeForHTML(format.getDescription()) %></p>
  </li>
<%
  }
%>
</ul>
