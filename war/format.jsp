<script src="/Format.js" type="text/javascript"></script>
<script src="/FormEditor.js" type="text/javascript"></script>
<script src="/FormConverter.js" type="text/javascript"></script>
<script src="/FieldEditor.js" type="text/javascript"></script>
<link rel="stylesheet" href="/format.css" type="text/css"/>
<div class="mainPanel" ng-controller="DatasetCtrl">
  <ul class="tabs">
    <li id="mainTab"><a href="/#" tabindex="0">DataWiki</a></li>
    <li><a href="#/wiki/{{datasetName}}">{{datasetName}}</a></li>
    <li class="activeTab">Format</li>
  </ul>
  <div id="formatBox" class="box">
    <div id="formatPanelLeft">
      <p>{{format.description}}</p>
    </div>
    <div id="formatPanelRight">
      <ul id="formTabs" class="tabs">
        <li class="activeTab"><a>Sample form</a></li>
      </ul>
      <div id="tabbedForms">
        <jsp:include page="form.jsp">
          <jsp:param name="jspFormId" value="formEdit"/>
          <jsp:param name="jspFormTitle" value="Edit"/>
        </jsp:include>
      </div>
    </div>
    <div>
      <h3>Developer Guide</h3>
      <h4>Web API</h4>
      <p>This dataset can be searched or retrieved in bulk using
        a <a href="http://en.wikipedia.org/wiki/Representational_State_Transfer">RESTful</a>
        <a href="http://en.wikipedia.org/wiki/Web_service">Web
          Service</a> <a href="http://en.wikipedia.org/wiki/Application_programming_interface">API</a>.</p>

      <p>All documents may be retrieved in Atom format using this URL:</p>
      <pre>TODO: xml feed link</pre>

      <p>A search for documents matching some criteria may be
        specified by setting the <code>q</code> request parameter
        and one or more <code>&amp;FIELD=VALUE</code>
        attribute/value pairs to return documents which have all of
        the requested attributes.  The FIELD name must match exactly
        the value of the name attribute used in the HTML form input
        for the associated field, as desribed in the "HTML Find and
        Create Forms" section below.</p>
      <pre>TODO: item search link</pre>

      <h4>XML Template</h4>
      <p>Documents retrieved in XML format will have the following
        structure.</p>

      TODO: XML template
    </div>
  </div>
  <jsp:include page="form-template.jsp"/><!-- Has to be outside of formatBox to hide input elts from FormEditor.js. -->
</div>
