<%@page import="java.util.List,wiki.Util" %>
<html>
  <head>
    <title>Create a new Format</title>
    <script src="http://www.google.com/jsapi" type="text/javascript"></script>
    <script src="/Translate.js" type="text/javascript"></script>
    <script src="/Wiki.js" type="text/javascript"></script>
    <script src="/FormatCreator.js" type="text/javascript"></script>
    <link rel="stylesheet" href="/formatCreate.css" type="text/css"/>
  </head>
  <body onload="creator = new FormatCreator();">
    <jsp:include page="onebar.jsp"/>
    <jsp:include page="nav.jsp"/>
    <div class="mainPanel trans">
      <div class="help">
        <p><strong>Format name</strong> must be composed of letters,
        digits, dashes and spaces.  The spaces will be displayed as
        spaces or underscores, depending on context.  This name will
        be part of your format's web address (URL) and cannot be
        changed later.</p>
        <p><strong>Short name</strong> will be used for computer code as
        a simple identifier of the format.</p>
      </div>

      <h1>Create Format</h1>
      <form action="/formats" method="POST" enctype="multipart/form-data" id="createForm">
        <p>Title:<br/>
          <input id="newFormatTitle" name="title" value="" onchange="creator.checkValid()"/><em class="warning"></em>
        </p>

        <p>Short name:<br/>
          <input id="newFormatName" name="name" value="" onchange="creator.checkValid()"/><em class="warning"></em>
        </p>
        <input id="createButton" type="submit" value="Create Format"/>
      </form>
    </div>
  </body>
</html>
