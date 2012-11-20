<%@page import="wiki.*, java.text.DateFormat, java.text.SimpleDateFormat, java.util.*"%><%
  final Format format = (Format) request.getAttribute("format");
  final String formatName = format.getName();
  final Boolean reqShowDocs = (Boolean) request.getAttribute("showDocs");
  final boolean showDocs = reqShowDocs == null ? false : true;
  String host = request.getScheme() +"://"+ request.getServerName();
  int port = request.getServerPort();
  if (port != 80) {
    host += ":" + port;
  }
  final String self = host + request.getRequestURI();
  final String wikiPage = self;
  final List<MultiPartDocument> matchingDocs = Documents.queryOrAll(request, formatName);
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
  <id><%= wikiPage %></id>
  <updated><%= feedUpdateDate %></updated>
  <generator>http://code.google.com/p/datawiki</generator>
<%
  int count = 0;
  for (final MultiPartDocument doc : matchingDocs) {
    final String xml = XmlSerializer.toXml(doc, format);
%>
  <entry>
    <title>/<%= formatName %>/<%= doc.getId() %></title>
    <author><name>anonymous</name></author>
    <link href="<%= host %>/wiki/docs/<%= doc.getId() %>"/>
    <id>urn:uuid:<%= java.util.UUID.nameUUIDFromBytes(xml.getBytes()) %></id>
    <updated><%= dateFormat.format(doc.getUpdatedDate()) %></updated>
    <content type="application/xml+<%= formatName %>">
      <%= Util.indent(xml, 6).trim() %>
    </content>
  </entry>
<%
  }
%>
</feed>
