<%@page import="wiki.Dataset,
                wiki.Format,
                wiki.Properties,
                wiki.Util"%>
<%
  Dataset dataset = (Dataset) request.getAttribute("dataset");
  Format format = (Format) request.getAttribute("format");

  boolean online = Properties.getBoolean("online");
  // Whether the dataset has geodata.
  boolean hasGeo = format.hasGeoData();
  // Location of offline geo server if present.
  String geepServer = Properties.getProperty("geep.server");
  boolean hasGeep = geepServer != null;
  boolean showGeo = hasGeo && (online || hasGeep);
%>
<html>
  <head>
    <jsp:include page="header.jsp"/>
<% if (online && showGeo) { %>
    <script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>
<% } %>
    <script src="/Documents.js" type="text/javascript"></script>
    <link href="/documents.css" rel="stylesheet" type="text/css"/>
  </head>
<% if (!online && showGeo) { %>
  <body onload="Documents(); InitMap()">
<% } else { %>
  <body onload="Documents()">
<% } %>
    <jsp:include page="onebar.jsp">
      <jsp:param name="showTranslate" value="<%= online %>"/>
    </jsp:include>
    <jsp:include page="nav.jsp"/>
    <div class="mainPanel trans">
      <ul class="tabs">
        <li class="activeTab"><a tabindex="0">Dataset</a></li>
        <li><a href="/wiki/formats/<%= format.getName() %>" tabindex="0">Format</a></li>
        <jsp:include page="search.jsp"/>
      </ul>
      <div id="formatBox" class="box tabbed activeTabbed">
        <div id="formatPanelLeft">
	  <jsp:include page="datasetSummary.jsp"/>
        </div>
        <div id="formatPanelRight">
          <ul id="formTabs" class="tabs">
            <li class="activeTab"><a tabindex="0">Find</a></li>
            <li><a tabindex="0">Create</a></li>
          </ul>
          <div id="tabbedForms">
            <jsp:include page="form.jsp">
              <jsp:param name="jspFormId" value="formFind"/>
              <jsp:param name="jspFormTitle" value="Find"/>
              <jsp:param name="jspFormMethod" value="GET"/>
              <jsp:param name="jspFormAction" value="<%= \"/wiki/\"+ format.getName() %>"/>
            </jsp:include>
            <jsp:include page="form.jsp">
              <jsp:param name="jspFormId" value="formCreate"/>
              <jsp:param name="jspFormTitle" value="Create"/>
              <jsp:param name="jspFormMethod" value="POST"/>
              <jsp:param name="jspFormAction" value="<%= \"/wiki/\"+ format.getName() %>"/>
              <jsp:param name="jspFormActive" value="false"/>
            </jsp:include>
          </div>
        </div>
        <div>&nbsp;</div>
      </div>
      <ul id="chartTabs" class="tabs">
        <li class="activeTab"><a tabindex="0">Documents</a></li>
        <li><a tabindex="0">Stats</a></li>
      </ul>
      <div id="tabbedCharts">
        <div id="detailCharts" class="box tabbed chart activeTabbed">
          <table>
            <tr>
              <td width="<%= showGeo ? "50%" : "100%" %>" valign="top">
                <jsp:include page="doclist.jsp">
                  <jsp:param name="dataset" value="<%= format.getName() %>"/>
                </jsp:include>
<!--
                <div id="listChart">
                  <div style="margin: 1em"><img src="/loader.gif" alt="Loading..."></div>
                </div>
-->
              </td>
<% if (showGeo) { %>
              <td width="50%" valign="top">
                <div style="display: none">
                  <div id="header">
                    <div id="logo">
                      <img src="/local/images/gee_logo.gif" align="left"/>
                    </div>
                    <div id="search_tabs" style="display: none"></div>
                  </div>
                  <table cellspacing="0" cellpadding="0">
                    <tr valign="top">
                      <td id="left_panel_cell">
                        <div id="left_panel"></div>
                      </td>
                      <td valign="top">&nbsp;</td>
                    </tr>
                  </table>
                </div>
                <div id="map" style="width: 100%; height: 500px; position: relative"></div>
              </td>
<% } %>
            </tr>
          </table>
        </div>
        <div id="statsCharts" class="box tabbed chart">
          <table>
            <tr>
              <td width="50%" valign="top" class="statsLeft">
                <h3>Format</h3>
                <ul>
                  <li>Created on: <%= Util.encodeForHTML(format.getCreatedDate().toString()) %></li>
                  <li>Namespace: <%= Util.encodeForHTML(format.getNamespace().toString()) %></li>
                </ul>
                <h3>Documents</h3>
                <div id="statsChart" style="width:50%; height:90%"></div>
              </td>
              <td width="50%">
              </td>
            </tr>
          </table>
        </div>
      </div>
    </div>
  </body>
</html>
