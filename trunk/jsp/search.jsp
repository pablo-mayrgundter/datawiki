<li class="searchTab">
  <form action="/wiki" method="GET" id="searchForm">
    <input onclick="this.value='';this.style.color='black'" value="Enter format name" style="color: #aaa" id="searchInput"/>
    <button class="button search" onclick="location.pathname='/wiki/'+get('searchInput').value;return false;"><div></div>&nbsp;</button>
  </form>
</li>

