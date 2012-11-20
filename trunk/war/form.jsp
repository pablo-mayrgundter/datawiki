<%@page import="wiki.Util"%>
<%
  final String formatName = (String) request.getAttribute("formatName");
  final String formId = Util.encodeForHTML(request.getParameter("jspFormId"));
  final String action = Util.encodeForHTML(request.getParameter("jspFormAction"));
  final String method = Util.encodeForHTML(request.getParameter("jspFormMethod"));
  final String activeClass = Util.encodeForHTML(request.getParameter("jspFormActive") == null ? "activeTabbed" : "");
%>
<form id="<%= formId %>" class="tabbed <%= activeClass %>" action="<%= action %>" enctype="multipart/form-data" method="<%= method %>">
<%
  if (formId.equals("formFind")) {
%>
  <input name="q" value="" type="hidden"/>
<%
  }
%>
  <table id="<%= formId %>-table" class="form">
<% 
  int fieldCount = 0;
  String [][] fields = {{"formatName1", "formatText1", "formatValue1"},
                        {"formatName1", "formatText1", "formatValue1"}};
  for (final String [] field : fields) {
      final String name = Util.encodeForHTML(field[0]);
      final String text = Util.encodeForHTML(field[1]);
      String value = field[2]; // i.e. default value.
      if (request.getParameter("q") != null && request.getParameter(name) != null) {
        value = request.getParameter(name);
      }
      value = Util.encodeForHTML(value);
%>
    <tr>
      <td><label for="<%= name %>"><%= text %></label>:</td>
      <td>
        <input id="<%= formId %>-input-<%= fieldCount++ %>" name="<%= name %>" value="<%= value %>">
        <div class="edit-buttons hover-reveal">
          <button class="button delete"><div></div>&nbsp;</button>
          <button class="button edit"><div></div>&nbsp;</button>
        </div>
      </td>
    </tr>
<%
  }
%>
    <tr>
      <td colspan="2">
        <div id="<%= formId %>-buttons" class="buttons formButtons">
          <input type="submit" value="Submit"/>
          <input type="reset" value="Clear" onclick="clearForm('<%= formId %>')"/>
        </div>
      </td>
    </tr>
  </table>
</form>
