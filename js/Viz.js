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
  if (window.location.search.indexOf('map') != -1)
    drawMap(document.getElementById('mapChart'), data);
}

function handleDetailResponse(response) {
  if (response.isError()) {
    alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
    return;
  }
  var data = response.getDataTable();
  var elt = document.getElementById('listChart');
  drawDetailTable(elt, data);
}

function drawSummary(elt, data) {
  var chart = new viz.StatsTable(elt, {width:'100%',height:'100%'});
  chart.draw(data);
}

function drawMap(elt, data) {
  var chart = new google.visualization.Map(elt);
  chart.draw(data);
}

function drawDetailTable(elt, data) {
  var chart = new google.visualization.Table(elt);
  var options = {allowHtml: true, width: '100%', is3D: true};
  options['page'] = 'enable';
  options['pageSize'] = 10;
  options['pagingSymbols'] = {prev: 'Previous', next: 'Next'};
  options['pagingButtonsConfiguration'] = 'auto';
  chart.draw(data, options);
}
