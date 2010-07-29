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