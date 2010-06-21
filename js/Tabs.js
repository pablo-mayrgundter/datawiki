function Tabs(tabElts, contentElts) {
  d('tabElts: '+ tabElts);
  if (tabElts.length != contentElts.length)
    throw 'tab and content arrays must be equal';
  this.tabEltPairs = new Array();
  var activeNdx = 0;
  for (var i = 0; i < tabElts.length; i++) {
    this.tabEltPairs[i] = [tabElts[i],contentElts[i]];
    if (checkClass(tabElts[i], 'activeTab'))
      activeNdx = i;
  }
  this.activeTab = this.tabEltPairs[activeNdx][0];
  this.activeElt = this.tabEltPairs[activeNdx][1];
  for (var i = 0; i < this.tabEltPairs.length; i++) {
    var tabEltPair = this.tabEltPairs[i];
    var tab = tabEltPair[0];
    var elt = tabEltPair[1];
    d('adding onclick to tab: '+ tab +' for content elt: '+ elt);
    tab.onclick = func(this, this.handleClick, [i]);
  }
};

Tabs.prototype.handleClick = function(ndx) {
  d('click on ndx: '+ ndx);
  var tab = this.tabEltPairs[ndx][0];
  if (this.activeTab == tab)
    return;
  var elt = this.tabEltPairs[ndx][1];
  removeClass(this.activeTab, 'activeTab');
  removeClass(this.activeElt, 'activeTabbed');
  this.activeTab = tab;
  this.activeElt = elt;
  addClass(this.activeTab, 'activeTab');
  addClass(this.activeElt, 'activeTabbed');
  return false;
};
