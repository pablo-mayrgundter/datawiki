<div id="onebar">
  <span id="langControl"><span class="trans">Language</span>:<span id="langSelect"></span></span>
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
