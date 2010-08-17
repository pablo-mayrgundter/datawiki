<%@page import="wiki.Util,java.util.LinkedHashMap"%>
<%
  final LinkedHashMap<String,String> links = new LinkedHashMap();
  links.put("Home", "/");
  links.put("Datasets", "/wiki/formats/");
  links.put("Bugs!", "http://code.google.com/p/datawiki/issues/list");
%><!-- nav -->
<div class="logoBody">
  <a href="/"><img src="/logo.png" alt="Google Public Data Wiki Home"></a>
</div>
<span class="sidePanel trans">
<%
  for (final String name : links.keySet()) {
    final String link = links.get(name);
    // TODO(pmy): this seems like a terrible way to do this.
    final String path = request.getRequestURI().toLowerCase();
    if (path.equals("/index.jsp") && link.equals("/") || path.equals(link) && !link.equals("/")) {
%>
  <li><strong><%= Util.encodeForHTML(name) %></strong></li>
<%
    } else {
%>
  <li><a href="<%= Util.encodeForDoubleQuotedAttribute(link) %>"><%= Util.encodeForHTML(name) %></a></li>
<%
    }
  }
%>
</span>
