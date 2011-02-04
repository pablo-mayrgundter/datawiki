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
    request.setAttribute("format", format);
%>
  <li>
    <jsp:include page="formatSummary.jsp">
      <jsp:param name="includeHref" value="true"/>
    </jsp:include>
  </li>
<%
  }
%>
</ul>
