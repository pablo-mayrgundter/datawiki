<%@page import="wiki.Util"%>
<%
  final String formId = Util.encodeForHTML(request.getParameter("jspFormId"));
  if (formId.equals("formFind")) {
%>
<form name="<%= formId %>" id="<%= formId %>" ng-submit="find()" ng-controller="DatasetCtrl">
  <input name="q" value="" type="hidden"/>
<%
  } else {
%>
<form name="<%= formId %>" id="<%= formId %>" ng-submit="create()" ng-controller="DatasetCtrl">
<%
  }
%>
  <table class="form">
    <tr ng-repeat="(key,val) in format">
      <td><label for="{{ key }}">{{ key }}</label>:</td>
      <td>
        <input name="{{ key }}" ng-model="queryForm[key]">
        <div class="edit-buttons hover-reveal">
          <button class="button delete"><div></div>&nbsp;</button>
          <button class="button edit"><div></div>&nbsp;</button>
        </div>
      </td>
    </tr>
    <tr>
      <td colspan="2">
        <div class="buttons formButtons">
          <input type="submit" value="Submit"/>
          <input type="reset" value="Clear" onclick="clearForm('<%= formId %>')"/>
        </div>
      </td>
    </tr>
  </table>
</form>
