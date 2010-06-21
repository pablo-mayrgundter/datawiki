<%@page import="wiki.*"%>
<%
  final String formatName = (String) request.getAttribute("formatName");
  final Format format = (Format) request.getAttribute("format");
  final boolean startEdit = format.getFields().isEmpty();
  final String hostURL = Util.getHostURL(request);
  final String self = hostURL + request.getRequestURI();
%>
<html>
  <head>
    <link rel="stylesheet" href="/format.css" type="text/css"/>
    <script src="/Util.js" type="text/javascript"></script>
    <script src="/HTTP.js" type="text/javascript"></script>
    <script src="/Format.js" type="text/javascript"></script>
    <script src="/FormEditor.js" type="text/javascript"></script>
    <script src="/FormConverter.js" type="text/javascript"></script>
    <script src="/FieldEditor.js" type="text/javascript"></script>
    <script>
      function init() {
        new Format(<%= startEdit %>);
      }
    </script>
  </head>
  <body onload="init()">
    <jsp:include page="nav.jsp"/>
    <div class="mainPanel">
      <ul class="tabs">
        <li><a href="/wiki/documents?format=<%= formatName %>">Dataset</a></li>
        <li class="activeTab">Format</li>
      </ul>
      <div id="formatBox" class="box">
        <div id="formatPanelLeft">
<%
  String title = format.getTitle();
  if (title == null || title.equals("")) {
    title = format.getName().toUpperCase();
  }
%>
          <h2 id="title"><%= title %></h2>
          <p id="description"><%= format.getDescription() %></p>
        </div>
        <div id="formatPanelRight">
          <ul id="formTabs" class="tabs">
            <li class="activeTab"><a>Edit</a></li>
          </ul>
          <div id="tabbedForms">
            <jsp:include page="form.jsp">
              <jsp:param name="jspFormId" value="formEdit"/>
              <jsp:param name="jspFormTitle" value="Edit"/>
              <jsp:param name="jspFormMethod" value="GET"/>
              <jsp:param name="jspFormAction" value="<%= "/wiki/formats/"+ formatName %>"/>
            </jsp:include>
          </div>
        </div>
        <div>
          <h3>Developer Guide</h3>
          <h4>Web API</h4>
          <p>This dataset can be searched or retrieved in bulk using
          a <a href="http://en.wikipedia.org/wiki/Representational_State_Transfer">RESTful</a> <a href="http://en.wikipedia.org/wiki/Web_service">Web
          Service</a> <a href="http://en.wikipedia.org/wiki/Application_programming_interface">API</a>.</p>

          <p>All documents may be retrieved in Atom format using this URL:</p>
          <pre><%= hostURL %>/wiki/documents?format=<%= formatName %>&amp;output=xml</pre>

          <p>A search for documents matching some criteria may be
          specified by setting the <code>q</code> request parameter
          and one or more <code>&amp;FIELD=VALUE</code>
          attribute/value pairs to return documents which have all of
          the requested attributes.  The FIELD name must match exactly
          the value of the name attribute used in the HTML form input
          for the associated field, as desribed in the "HTML Find and
          Create Forms" section below.</p>
          <pre><%= hostURL %>/wiki/documents?format=<%= formatName %>&amp;q&amp;output=xml&amp;/Item/ID=1</pre>

          <h4>XML Template</h4>
          <p>Documents retrieved in XML format will have the following
          structure.</p>

          <pre><%= XmlSerializer.toXml(format).replaceAll("<", "&lt;").replaceAll(">", "&gt;") %></pre>

          <h4>HTML Find and Create Forms</h4>
          <p>You can use forms for finding or creating documents in
          this dataset on your own site using the following HTML code.
          Embedding this code on your site will present a plain form
          that will submit a request directly from the user's web
          browser to this site.</p>

          <p>Find:</p>
<pre><%= XmlSerializer.toFindForm(formatName, hostURL, format.getFields()).replaceAll("<", "&lt;").replaceAll(">", "&gt;") %></pre>
          <p>Create:</p>
<pre><%= XmlSerializer.toCreateForm(formatName, hostURL, format.getFields()).replaceAll("<", "&lt;").replaceAll(">", "&gt;") %></pre>
        </div>
      </div>
      <jsp:include page="form-template.jsp"/><!-- Has to be outside of formatBox to hide input elts from FormEditor.js. -->
    </div>
  </body>
</html>
