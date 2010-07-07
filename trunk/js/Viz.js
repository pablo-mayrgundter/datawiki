var itemCharts = new Array();

function vizQuery() {
  var activeQuery = window.location.search;
  var queryStr = '/viz?';
  if (activeQuery != null) {
    queryStr += '&' + activeQuery.substring(1);
  }
  new google.visualization.Query(queryStr + '&summary').send(handleSummaryResponse);
  new google.visualization.Query(queryStr).send(handleDetailResponse);
}

function handleSummaryResponse(response) {
  if (response.isError()) {
    alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
    return;
  }
  var data = response.getDataTable();
  drawSummary(document.getElementById('timelineChart'), data);
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
    drawMap(mapElt, data.clone());
  } else {
    drawDetailTable(elt, data);
  }
}

function drawSummary(elt, data) {
  var chart = new viz.StatsTable(elt, {width:'100%',height:'100%'});
  chart.draw(data);
}

function drawMap(elt, data) {
  var chart = new google.visualization.Map(elt);
  if (data.getNumberOfColumns() > 3)
    data.removeColumns(3, data.getNumberOfColumns());
  chart.draw(data, {showTip: true});
  itemCharts.push(chart);
  google.visualization.events.addListener(chart, 'select', function() { selectHandler(chart); });
}

function drawDetailTable(elt, data) {
  var chart = new google.visualization.Table(elt);
  var options = {allowHtml: true, width: '100%', is3D: true};
  options['page'] = 'enable';
  options['pageSize'] = 10;
  options['pagingSymbols'] = {prev: 'Previous', next: 'Next'};
  options['pagingButtonsConfiguration'] = 'auto';
  chart.draw(data, options);
  itemCharts.push(chart);
  google.visualization.events.addListener(chart, 'select', function() { selectHandler(chart); });
}

function selectHandler(chart) {
  var selection = chart.getSelection();
  for (var chart in itemCharts)
    itemCharts[chart].setSelection(selection);
}