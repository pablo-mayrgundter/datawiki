<%@page import="java.util.*,wiki.Format,wiki.Formats,wiki.Util" %>
<%
  final boolean showFeatured = request.getParameter("showFeatured") != null;
  final int maxToShow = 5;
%>
<ul class="formats">
<%
  final List<Format> formats = new ArrayList<Format>(new Formats().asList());
  Collections.reverse(formats);
  for (int i = 0; i < formats.size(); i++) {
    if (i >= maxToShow)
      break;
    final Format format = formats.get(i);
    if (format == null) { // TODO(pmy): still required?
      continue;
    }
    if (showFeatured) {
      if (format.flags == null) {
        continue;
      } else if (format.flags.indexOf("featured") < 0) {
        continue;
      }
    }
    String title = format.getTitle();
    if (title == null || title.trim().equals("")) {
      title = format.getName().toUpperCase();
    }
    title = title.replaceAll("_", " ");
    title = "<a href=\"/wiki/"+ Util.encodeForDoubleQuotedAttribute(format.getURLTitle()) +"\">"
            + Util.encodeForHTML(title) +"</a>";
%>
  <li>
    <%= title %>
    <p><%= Util.encodeForHTML(format.getDescription()) %></p>
  </li>
<%
  }
%>
</ul>
