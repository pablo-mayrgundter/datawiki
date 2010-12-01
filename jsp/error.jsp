<html>
  <head>
    <link rel="stylesheet" href="/index.css" type="text/css"/>
  </head>
  <body onload="translateInit('langSelect')">
    <jsp:include page="nav.jsp"/>
    <div class="mainPanel trans">
      <p>DataWiki failed :(</p>
      <ul>
        <li>Please report the bug, including the reason below, <a href="http://code.google.com/p/datawiki/issues/entry">here</a>!</li>
        <li>And please enjoy the fail animals.</li>
      </ul>
      <%
         final String uri = (String)request.getAttribute("javax.servlet.error.request_uri");
         final int httpCode = (Integer)request.getAttribute("javax.servlet.error.status_code");
         final String message = (String)request.getAttribute("javax.servlet.error.message");
         final Exception exception = (Exception)request.getAttribute("javax.servlet.error.exception");
         String reason = "URI: " + uri + "<br/>\nStatus Code: "+ httpCode + "<br/>\n";
         if (message != null) reason += "Message: "+ message + "<br/>\n";
         if (exception != null) {
           reason += "Exception: "+ exception + "<br/>\n";
         };
         %>
      <p><strong>Reason:</strong></p>
      <blockquote style="border: solid 1px black; background-color: #cfc; padding: 1em">
        <%= reason %>
      </blockquote>
      <p>&nbsp;</p>
      <p><strong>Fail Animals:</strong></p>
      <iframe src="http://www.google.com/images?q=fail+animals&um=1&ie=UTF-8&source=og&sa=N&hl=en&tab=wi&biw=1270&bih=680" width="100%" height="600px" frameborder=0/>
    </div>
  </body>
</html>
