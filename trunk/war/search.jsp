<li class="searchTab">
  <form name="searchForm" ng-submit="doSearch()" id="searchForm">
    <input name="query"
           ng-model="search.query"
           onclick="this.value='';this.style.color='black'"
           style="color: #aaa"
           size="40"
           id="searchInput"/>
    <button class="button search" ng-click="doSearch()"><div></div>&nbsp;</button>
  </form>
</li>
