<%@page import="java.io.IOException,com.google.appengine.api.users.UserService,com.google.appengine.api.users.UserServiceFactory"%><%
  final UserService userService = UserServiceFactory.getUserService();
  final String thisURI = request.getParameter("uri");
  if (request.getUserPrincipal() != null) {
    response.getWriter().println("<span class=\"username\">"
                                 + request.getUserPrincipal().getName()
                                 + "</span> | <a href=\""
                                 + userService.createLogoutURL(thisURI)
                                 + "\" class=\"trans\">Sign out</a>");
  } else {
    response.getWriter().println("<span class=\"username\">anonymous</span> | <a href=\""
                                 + userService.createLoginURL(thisURI)
                                 + "\" class=\"trans\">Sign in</a>");
  }
%>
