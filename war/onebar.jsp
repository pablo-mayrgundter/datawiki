<div id="onebar">
  <%
     String loginContinuePage = request.getRequestURI();
     if (request.getQueryString() != null) {
       loginContinuePage += "?"+ request.getQueryString();
     }
  %>
</div>
