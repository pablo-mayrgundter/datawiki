<%
  String siteName = request.getParameter("siteName");
  if (siteName == null)
    siteName = "ReliefHub";
%>
<html>
  <head>
    <link rel="stylesheet" href="index.css" type="text/css"/>
    <style>
      .mainPanel {
        width: 600px;
      }
    </style>
  </head>
  <body>
    <jsp:include page="nav.jsp"/>
    <div class="mainPanel">
      <h3>DataWiki: Open Datasets and Formats</h3>

      <p>DataWiki is an open, collaborative system to help people
      create, maintain and share public collections of data.</p>

      <p>Creating a new dataset is as easy as creating a new web form
      for a person to enter it.  DataWiki automatically transforms the
      description of the data into formats that can be browsed, shared
      in social networks like Twitter or even accessed via SMS on
      mobile phones.  And like Wikipedia, users can contribute
      anonymously, under a pseudonym or with their real identity.</p>

      <object width="480" height="385"><param name="movie" value="http://www.youtube.com/v/kZGCOara3hU&hl=en_US&fs=1&"></param><param name="allowFullScreen" value="true"></param><param name="allowscriptaccess" value="always"></param><embed src="http://www.youtube.com/v/kZGCOara3hU&hl=en_US&fs=1&" type="application/x-shockwave-flash" allowscriptaccess="always" allowfullscreen="true" width="480" height="385"></embed></object>
    </div>
  </body>
</html>
