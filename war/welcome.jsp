<div class="mainPanel" ng-controller="WelcomeCtrl">
  <ul class="tabs">
    <li class="activeTab">Welcome!</li>
    <jsp:include page="search.jsp"/>
  </ul>
  <div id="formatBox" class="box tabbed activeTabbed">
    <h2>A wiki for sharing data</h2>

    <p>DataWiki is currently in testing! All data and current storage
      formats should be used for testing purposes only. </p>

    <p>See <a href="http://code.google.com/p/datawiki">http://code.google.com/p/datawiki</a>
    for the code project.</p>

    <div id="featured">
      <!--<div id="createButton"><a href="/formats?action=new"><button class="button plus text"><div></div>Create New</button></a></div>-->
      <h2>Recent Data</h2>
      <!-- Collection view of /wiki/ -->
      <ul>
        <li ng-repeat="(name,info) in datasetList">
          <a href="#/wiki{{name}}">{{name}}</a>
        </li>
      </ul>
    </div>
  </div>
</div>
