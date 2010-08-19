
function include(file) {
  var head = document.getElementsByTagName('head')[0];
  var script = document.createElement('script');
  script.src = file;
  script.type = 'text/javascript';
  head.appendChild(script);
}

// http://ejohn.org/blog/flexible-javascript-events
function addEvent( obj, type, fn ) {
  if ( obj.attachEvent ) {
    obj['e'+type+fn] = fn;
    obj[type+fn] = function(){obj['e'+type+fn]( window.event );}
    obj.attachEvent( 'on'+type, obj[type+fn] );
  } else
    obj.addEventListener( type, fn, false );
}
function removeEvent( obj, type, fn ) {
  if ( obj.detachEvent ) {
    obj.detachEvent( 'on'+type, obj[type+fn] );
    obj[type+fn] = null;
  } else
    obj.removeEventListener( type, fn, false );
}

function func(obj, handler, args) {
  if (!args)
    args = [];
  return function() { handler.apply(obj, args); };
}

function get(id) {
  return document.getElementById(id);
}

function elts(name) {
  return document.getElementsByTagName(name);
}

function eltsByIdPrefix(prefix, node)  {
  if (!node)
    node = document.getElementsByTagName("body")[0];
  var elts = {};
  var re = new RegExp('^'+ prefix);
  var subNodes = node.getElementsByTagName("*");
  for (var i = 0; i < subNodes.length; i++)
    if (re.test(subNodes[i].id))
      elts[subNodes[i].id] = subNodes[i];
  return elts;
}

var serial = 0;
function loadTemplate(parent, tplId) {
  var prefix = tplId +'_'+ (serial++) +'-';
  return [prefix, loadTemplateWithPrefix(prefix, parent, tplId)];
}

function loadTemplateWithPrefix(prefix, parent, tplId) {
  var tpl = document.getElementById('template-'+ tplId).innerHTML;
  parent.innerHTML = tpl;
  var elts = eltsByIdPrefix('template-', parent);
  var objsByNewId = {};
  for (var id in elts) {
    var elt = elts[id];
    var newId = prefix + id.substring('template-'.length, id.length);
    elt['id'] = newId;
    objsByNewId[newId] = elt;
  }
  return objsByNewId;
}

function add(parent, name, attrs, text) {
  var e = create(name, attrs, text);
  parent.appendChild(e);
  return e;
}

function remove(node) {
  node.parentNode.removeChild(node);
}

function before(newElt, node) {
  node.parentNode.insertBefore(newElt, node);
}

function after(newElt, node) {
  if (!node.nextSibling) {
    node.parentNode.appendChild(newElt);
  } else {
    node.parentNode.insertBefore(newElt, node.nextSibling);
  }
}

var debugElt;
function d(msg) {
  if (null == debugElt) {
    debugElt = get('debug');
    if (null == debugElt)
      return;
  }
  debugElt.innerHTML += msg + '<br/>';
}

/**
 * @param attrs A map of attribute names to objects, which may include
 * event handlers.  Optional, but bust be specified if inner is
 * specified.
 * @param inner The innerHTML of the tag, either as a string or as an
 * array of triplets which will be recursively created by calls to
 * this method.  Optional.
 */
function create(name, attrs, inner) {
  var elt = document.createElement(name);
  d('elt: '+ elt);
  if (attrs)
    for (var attr in attrs) {
      var val = attrs[attr];
      if (val.constructor == String)
        elt.setAttribute(attr, val);
      else
        elt[attr] = val;
    }
  if (inner)
    if (!(inner instanceof Array)) {
      elt.innerHTML = inner;
    } else
      for (var i = 0; i < inner.length; i++) {
        var e = inner[i];
        if (!(e instanceof Array)) {
          alert('Attempt to create node: '+ name +' with non-array node list.');
          return;
        } if (e.length == 0) {
          alert('Attempt to create node: '+ name +' with empty inner node list.');
          return;
        }
        else if (e.length == 1)
          elt.appendChild(create(e[0]));
        else if (e.length == 2)
          elt.appendChild(create(e[0], e[1]));
        else if (e.length == 3)
          elt.appendChild(create(e[0], e[1], e[2]));
      }
  return elt;
}

function stopHandler(e) {
  return false;
}

function checkClass(obj, clazz) {
  return new RegExp('\\b'+ clazz +'\\b').test(obj.className);
}

function removeClass(obj, clazz) {
  var rep = obj.className.match(' '+ clazz) ? ' '+ clazz:clazz;
  obj.className = obj.className.replace(rep, '');
}

function addClass(obj, clazz) {
  if (!checkClass(obj, clazz)) {
    obj.className += obj.className ? ' '+ clazz:clazz;
  }
}

function findLabelForControl(name, startAt) {
  if (!startAt)
    var startAt = document;
  var labels = startAt.getElementsByTagName('label');
  for (var i = 0; i < labels.length; i++) {
    var label = labels[i];
    if (label.htmlFor == name)
      return label;
  }
  return null;
}

/** BEGIN AJAX CODE */
// From: http://www.captain.at/howto-ajax-form-post-request.php
var http_request = false;
var callback = null;

function formEncode(params) {
  var encoded = '';
  for (var name in params) {
    var value = params[name];
    var part = encodeURI(name) +'='+ encodeURI(value);
    if (encoded != '')
      encoded += '&';
    encoded += part;
  }
  return encoded;
}

var boundary = 'AaB03xbleh42';

/** http://www.w3.org/TR/html401/interact/forms.html#h-17.13.4.2 */
function multipartEncode(params) {
  var body = '--'+ boundary;
  for (var name in params) {
    body += '\r\nContent-Disposition: form-data; name="'+ name +'"\r\n\r\n'+ params[name] +'\r\n--'+ boundary;
  }
  return body + '--';
}

function makePOSTRequest(url, params, cb) {
  httpRequest(url, multipartEncode(params), cb, 'POST', "multipart/form-data; boundary="+ boundary);
}

function httpRequest(url, encodedParams, cb, verb, contentType) {
  http_request = false;
  if (window.XMLHttpRequest) { // Mozilla, Safari,...
    http_request = new XMLHttpRequest();
  } else if (window.ActiveXObject) { // IE
    try {
      http_request = new ActiveXObject("Msxml2.XMLHTTP");
    } catch (e) {
      try {
        http_request = new ActiveXObject("Microsoft.XMLHTTP");
      } catch (e) {}
    }
  }
  if (!http_request) {
    alert('Cannot create XMLHTTP instance');
    return false;
  }

  callback = cb;
  http_request.onreadystatechange = alertContents;
  http_request.open(verb, url, true);
  http_request.setRequestHeader("Content-Type", contentType);
  http_request.send(encodedParams);
}

function alertContents() {
  if (http_request.readyState == 4) {
    if (http_request.status == 200) {
      //result = http_request.responseText;
      //document.getElementById('myspan').innerHTML = result;
      if (callback)
        callback.call();
    } else {
      alert('There was a problem with this request.  '+ http_request.responseText);
    }
  }
}

/** END AJAX CODE */

/** BEGIN VIZ CODE */

var itemCharts = new Array();

function vizQuery() {
  var activeQuery = window.location.search;
  var queryStr = '/chart?format='+ vizFormat;
  if (activeQuery != null) {
    queryStr += '&' + activeQuery.substring(1);
  }
  new google.visualization.Query(queryStr).send(handleDetailResponse);
  new google.visualization.Query(queryStr + '&summary').send(handleStatsResponse);
}

function handleDetailResponse(response) {
  if (response.isError()) {
    alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
    return;
  }
  var data = response.getDataTable();
  var elt = document.getElementById('listChart');
  if (data.getTableProperty('hasMap')) {
    var dataWithoutMapSummaryColumn = data.clone();
    dataWithoutMapSummaryColumn.removeColumns(0,3);
    drawDetailTable(elt, dataWithoutMapSummaryColumn);
    var mapElt = document.getElementById('mapChart');
    if (mapElt)
      drawMap(mapElt, data.clone());
  } else {
    drawDetailTable(elt, data);
  }
}

function handleStatsResponse(response) {
  if (response.isError()) {
    alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
    return;
  }
  var data = response.getDataTable();
  var chartElt = document.getElementById('statsChart');
  if (!chartElt)
    return;
  drawSummary(chartElt, data);
}

function drawSummary(elt, data) {
  var chart = new viz.StatsTable(elt, {width:'100%',height:'100%'});
  chart.draw(data);
}

function drawMap(elt, data) {
  var chart = new google.visualization.Map(elt);
  if (data.getNumberOfColumns() > 3)
    data.removeColumns(3, data.getNumberOfColumns());
  chart.draw(data, {showTip: true, mapType: 'hybrid', useMapTypeControl: true});
  itemCharts.push(chart);
  google.visualization.events.addListener(chart, 'select', function() { selectHandler(chart); });
}

var detailData;

function drawDetailTable(elt, data) {
  detailData = data;
  var tableChart = new google.visualization.Table(elt);
  var options = {allowHtml: true, width: '100%', is3D: true};
  options['page'] = 'enable';
  options['pageSize'] = 10;
  options['pagingSymbols'] = {prev: 'Previous', next: 'Next'};
  options['pagingButtonsConfiguration'] = 'auto';
  tableChart.draw(data, options);
  itemCharts.push(tableChart);
  google.visualization.events.addListener(tableChart, 'select', function() { selectHandler(tableChart); });
}

function selectHandler(chart) {
  var selection = chart.getSelection();
  for (var chart in itemCharts)
    itemCharts[chart].setSelection(selection);
}

function get(id) {
  return document.getElementById(id);
}

function create(name, text) {
  var e = document.createElement(name);
  if (text)
    e.innerHTML = text;
  return e;
}

function editItem(elt, rowId, save) {
  var cell = elt.parentNode;
  var fields = []; // Used during save only.
  for (var col = 0, n = detailData.getNumberOfColumns(); col < n; col++) {
    if (col == 0) {
      cell.innerHTML = null;
      if (save) {
        cell.innerHTML = '<a href="#" onclick="editItem(this, \''+ rowId +'\');return false;">'+ rowId +'</a>';
      } else {
        var button = create('button', 'Done');
        button.setAttribute('onclick', 'editItem(this, '+ rowId +', true);return false;');
        cell.appendChild(button);
      }
    } else {
      var val = save ? cell.firstChild.value : cell.innerHTML;
      cell.innerHTML = null;
      if (save) {
        cell.innerHTML = val;
        var convertedVal = val;
        var type = detailData.getColumnType(col);
        if (type == 'number') {
          convertedVal = parseInt(val);
        } else if (type == 'boolean') {
          convertedVal = val.toLowerCase() == 'true' ? true : false;
        } else if (type == 'date' || type == 'datetime') {
          convertedVal = new Date(val);
        } else if (type == 'timeofday') {
          convertedVal = val.split('\s');
        }
        fields[detailData.getColumnId(col)] = convertedVal;
      } else {
        var input = create('input');
        input.setAttribute('value', val);
        cell.appendChild(input);
      }
    }
    cell = cell.nextSibling;
  }
  if (save) {
    var docId = detailData.getValue(rowId, 0);
    makePOSTRequest(location.href +'/'+ docId, fields, function(){alert('sent!');});
  }
}
/** BEGIN VIZ CODE */

/** BEGIN TABS CODE */

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

/** END TABS CODE */
