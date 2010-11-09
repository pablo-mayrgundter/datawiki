<%@page import="wiki.*, java.text.DateFormat, java.text.SimpleDateFormat, java.util.*"%><%
  final String formatName = (String) request.getAttribute("formatName");
  final Format format = (Format) request.getAttribute("format");
  final Boolean reqShowDocs = (Boolean) request.getAttribute("showDocs");
  final boolean showDocs = reqShowDocs == null ? false : true;
  String host = request.getScheme() +"://"+ request.getServerName();
  int port = request.getServerPort();
  if (port != 80)
    host += ":" + port;
  final String self = host + request.getRequestURI();
  final String wikiPage = self;

  final List<MultiPartDocument> matchingDocs =
    Documents.queryOrAll(request, formatName, format);

  final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

  // TODO(pmy): now unless we have docs.
  String feedUpdateDate = dateFormat.format(new Date());
  if (matchingDocs.size() > 0) {
    feedUpdateDate = dateFormat.format(matchingDocs.get(matchingDocs.size() - 1).getUpdatedDate());
  }

%><?xml version="1.0" encoding="utf-8"?>
<feed xmlns="http://www.w3.org/2005/Atom">
  <title>Documents of format: <%= formatName %> from <%= request.getServerName() %></title>
  <link href="<%= wikiPage %>?output=xml" rel="self"/>
  <link href="<%= wikiPage %>" />
  <id>urn:uuid:<%= java.util.UUID.randomUUID() %></id>
  <updated><%= feedUpdateDate %></updated>
<%
  int count = 0;
  for (final MultiPartDocument doc : matchingDocs) {
    final String xml = XmlSerializer.toXml(doc, format);
    
%>
  <entry>
    <title><%= formatName%> document #<%= count++ %></title>
    <author><name>anonymous</name></author>
    <summary>Serialized XML according to the format described at <%= wikiPage %></summary>
    <link href="<%= host %>/wiki/<%= formatName %>/<%= doc.getId() %>"/>
    <id>urn:uuid:<%= java.util.UUID.randomUUID() %></id>
    <updated><%= dateFormat.format(doc.getUpdatedDate()) %></updated>
    <content type="application/xml+<%= formatName %>">
<%= xml %>
    </content>
  </entry>
<%
  }
%>
</feed>
