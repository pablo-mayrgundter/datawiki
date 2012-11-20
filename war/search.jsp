<li class="searchTab">
  <form action="/wiki" method="GET" id="searchForm">
    <input onclick="this.value='';this.style.color='black'" value="Enter format name" style="color: #aaa" id="searchInput"/>
    <button class="button search" onclick="val=get('searchInput').value;if(val){location.pathname='/wiki/'+val;};return false;"><div></div>&nbsp;</button>
  </form>
</li>
