<%@page import="wiki.Util" %><%
  final String reqXml = (String) request.getAttribute("reqXml");
  final Exception reqXmlException = (Exception) request.getAttribute("reqXmlException");
%><html>
  <head>
    <script src="http://www.google.com/jsapi" type="text/javascript"></script>
    <script src="/Wiki.js" type="text/javascript"></script>
    <script src="/Translate.js" type="text/javascript"></script>
    <script src="/Formats.js" type="text/javascript"></script>
    <link rel="stylesheet" href="/formats.css" type="text/css"/>
  </head>
  <body onload="init()">
    <jsp:include page="onebar.jsp"/>
    <jsp:include page="nav.jsp"/>
    <div class="mainPanel trans">
      <table>
        <tr>
          <td class="featured">
            <jsp:include page="featured.jsp"/>
          </td>
          <td class="createFormat">
            <h3>Create a new dataset format from scratch...</h3>
            Format Name: <input id="target" value=""/>
            <a id="link" href="" onclick="setTarget(get('target'))">Go</a>
            <span class="note">(Allowed characters: <%= Util.XML_SAFE_CHARS %>)</span>

            <h3>or use an existing XML template</h3>
            <form action="/wiki/formats" method="POST" enctype="multipart/form-data">
              <textarea name="xml" cols="40" rows="20"><%= reqXml == null ? "" : Util.encodeForHTML(reqXml) %></textarea>
              <br/>
<%
   if (reqXml != null && reqXmlException != null) {
%>
     <em>The XML template cannot be parsed.  The parser generated the following exception:<br/>
       <%= Util.encodeForHTML(reqXmlException.toString()) %></em><br/>
<%
   }
%>
              <input type="submit"/><input type="reset"/>
            </form>
          </td>
        </tr>
      </table>
    </div>
  </body>
</html>
