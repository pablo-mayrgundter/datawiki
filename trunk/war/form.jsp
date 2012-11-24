<%@page import="wiki.Util"%>
<%
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
  int fieldCount = 0;
%>
  <table id="<%= formId %>-table" class="form">
    <tr ng-repeat="(key,val) in format.schema">
      <td><label for="{{key}}">{{key}}</label>:</td>
      <td>
        <input id="<%= formId %>-input-<%= fieldCount++ %>" name="{{key}}" value="{{val}}">
        <div class="edit-buttons hover-reveal">
          <button class="button delete"><div></div>&nbsp;</button>
          <button class="button edit"><div></div>&nbsp;</button>
        </div>
      </td>
    </tr>
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
