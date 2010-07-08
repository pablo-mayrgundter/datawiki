<%@page import="java.util.LinkedHashMap"%>
<%
  final LinkedHashMap<String,String> links = new LinkedHashMap();
  links.put("Home", "/");
  links.put("Datasets", "/wiki/formats/");
  links.put("Bugs!", "http://code.google.com/p/datawiki/issues/list");
//  links.put("FAQ", "/faq");
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
  <li><strong><%= name %></strong></li>
<%
    } else {
%>
  <li><a href="<%= link %>"><%= name %></a></li>
<%
    }
  }
%>
</span>
<div style="position: fixed; bottom: 0">
<a href="#" onclick="document.getElementById('debug').style.display = 'block'">+</a>
<div id="debug" style="display: none" onclick="this.style.display = 'none'">
Context Path: <%= request.getContextPath() %><br/>
PathInfo: <%= request.getPathInfo() %><br/>
PathTrans: <%= request.getPathTranslated() %><br/>
QueryString: <%= request.getQueryString() %><br/>
URI: <%= request.getRequestURI() %><br/>
URL: <%= request.getRequestURL() %><br/>
ServletPath: <%= request.getServletPath() %><br/>
ServletPath: <%= request.getServletPath() %><br/>
</div>
</div>
