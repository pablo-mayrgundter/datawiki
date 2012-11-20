<div class="mainPanel" ng-controller="DocumentCtrl">
  <ul class="tabs">
    <li class="activeTab">Document</li>
    <jsp:include page="search.jsp"/>
  </ul>
  <div id="formatBox" class="box tabbed activeTabbed">
    <div style="float: right; text-align: center">
      <!-- <img alt="TODO/qrcode" src="TODO/qrcode" style="margin-bottom: 1em"/><br/> -->
      Scannable link to this page
    </div>
    <p><a href="#/wiki/{{dataset}}">
        « Return to dataset
    </a></p>
    <h2>Fields</h2>
    <div style="width: 50%">
      <form action="/wiki/{{dataset}}/{{id}}" method="POST" enctype="multipart/form-data">
        <table class="form">
          <tr ng-repeat="(key,val) in document">
            <td><label for="{{key}}">{{key}}</label>:</td>
            <td>
              <input name="{{key}}" value="{{val}}"/>
            </td>
          </tr>
          <tr>
            <td colspan="2">
              <div class="buttons formButtons">
                <input type="submit" value="Save"/>
                <input type="reset" value="Reset"/>
              </div>
            </td>
          </tr>
        </table>
      </form>
    </div>
    <h2 style="clear: right">XML</h2>
    <pre>TODO_xmlDoc</pre>
    or:<br/>
    <pre>TODO_xmlDocLink</pre>
  </div>
</div>
