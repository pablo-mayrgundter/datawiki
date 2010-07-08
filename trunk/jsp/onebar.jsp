<div class="onebar">
  <span class="langControl">Language:<span id="langSelect"></span></span>
  <%
  String loginContinuePage = request.getRequestURI();
  if (request.getQueryString() != null) {
    loginContinuePage += "?"+ request.getQueryString();
  }
  %>
  <jsp:include page="signin.jsp">
    <jsp:param name="uri" value="<%= loginContinuePage %>"/>
  </jsp:include>
</div>
