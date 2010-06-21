/** Namespace, implemented as a global variable. */
var viz = {};

/**
 * StatsTable class constructor.
 *
 * @param container The HTML element where the visualization will be
 * rendered in.
 */
viz.StatsTable = function(container) {
  // Stores the container HTML element.
  this.containerElement = container;
}

/**
 * @param data The google.visualization.DataTable to display.
 * @param options name/value map of options. 
 */
viz.StatsTable.prototype.draw = function(data, options) {
  // showLineNumber boolean option.
  var showLineNumber = options && options.showLineNumber;
  
  var count = 0, firstDate, lastDate;
  for (var row = 0, n = data.getNumberOfRows(); row < n; row++) {
    if (row == 0)
      firstDate = data.getFormattedValue(row, 0);
    if (row == n - 1)
      lastDate = data.getFormattedValue(row, 0);
    count += data.getValue(row, 1);
  }
  var html = [];
  html.push('<ul class="statsTable">');
  html.push('<li>Documents: '+ count +'</li>');
  html.push('<li>First Record Date: '+ firstDate +'</li>');
  html.push('<li>Last Record Date: '+ lastDate +'</li>');
  html.push('</ul>');

  this.containerElement.innerHTML = html.join('');
}

viz.StatsTable.prototype.escapeHtml = function(text) {
  if (text == null) {
    return '';
  }
  return text.replace(/&/g, '&').
  replace(/</g, '<').
  replace(/>/g, '>').
  replace(/"/g, '"');
}
