<%
  final boolean showTrans = Boolean.parseBoolean(request.getParameter("showTranslate"));
%>
<div id="onebar">
  <% if (showTrans) { %>
  <!-- Google Translate Element -->
  <div id="google_translate_element" style="display:block"></div>
  <script>
    function googleTranslateElementInit() {
      new google.translate.TranslateElement({pageLanguage: "af"}, "google_translate_element");
    };
  </script>
  <% } %>
  <%
     String loginContinuePage = request.getRequestURI();
     if (request.getQueryString() != null) {
       loginContinuePage += "?"+ request.getQueryString();
     }
  %>
</div>
