<%@page import="wiki.Format,wiki.FormField"%>
<%
  final String formatName = (String) request.getAttribute("formatName");
  final Format format = (Format) request.getAttribute("format");
  final String formId = request.getParameter("jspFormId");
  final String title = request.getParameter("jspFormTitle");
  final String action = request.getParameter("jspFormAction");
  final String method = request.getParameter("jspFormMethod");
  final String activeClass = request.getParameter("jspFormActive") == null ? "activeTabbed" : "";
%>
<form id="<%= formId %>" class="tabbed <%= activeClass %>" action="<%= action %>" enctype="multipart/form-data" method="<%= method %>">
<%
  if (!formId.equals("formEdit")) {
%>
  <input name="format" value="<%= formatName %>" type="hidden"/>
<%
  }
  if (formId.equals("formFind")) {
%>
  <input name="q" value="" type="hidden"/>
<%
  }
%>
  <table id="<%= formId %>-table" class="form">
<% 
  if (format != null) {
    int fieldCount = 0;
    for (final FormField field : format.getFields()) {
      String value = field.getValue();
      if (request.getParameter("q") != null && request.getParameter(field.getName()) != null)
        value = request.getParameter(field.getName());
%>
    <tr>
      <td><label for="<%= field.getName() %>"><%= field.getText() %></label>:</td>
      <td>
        <input id="<%= formId %>-input-<%= fieldCount++ %>" name="<%= field.getName() %>" value="<%= value %>">
        <div class="edit-buttons hover-reveal">
          <button class="button delete"><div></div>&nbsp;</button>
          <button class="button edit"><div></div>&nbsp;</button>
        </div>
      </td>
    </tr>
<%
    }
  } 
%>
    <tr>
      <td colspan="2">
        <div id="<%= formId %>-buttons" class="buttons formButtons">
          <input type="submit" value="Submit"/>
          <input type="reset" value="Clear"/>
        </div>
      </td>
    </tr>
  </table>
</form>
