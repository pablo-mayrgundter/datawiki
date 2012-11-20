<div class="mainPanel" ng-controller="WelcomeCtrl">
  <ul class="tabs">
    <li class="activeTab">Welcome!</li>
    <jsp:include page="search.jsp"/>
  </ul>
  <div id="formatBox" class="box tabbed activeTabbed">
    <h2>A wiki for structured data</h2>

    <p>DataWiki is currently in testing; all data
      and current storage formats should be used for testing
      purposes only. DataWiki currently has XX 
      documents in XX formats.</p>

    <div id="featured">
      <!--<div id="createButton"><a href="/formats?action=new"><button class="button plus text"><div></div>Create New</button></a></div>-->
      <h2>Recent Datasets</h2>
      <!-- Collection view of /wiki/ -->
      <ul>
        <li ng-repeat="(name,info) in datasetList">
          <a href="#/wiki{{name}}">{{name}}</a>
        </li>
      </ul>
    </div>
  </div>
</div>
