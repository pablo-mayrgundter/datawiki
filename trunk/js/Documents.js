function Documents() {
  new Tabs(get('formTabs').getElementsByTagName('li'),
           get('tabbedForms').getElementsByTagName('form'));
  new Tabs(get('chartTabs').getElementsByTagName('li'),
           [get('detailCharts'), get('statsCharts')]);
};

function init() {
  Documents();
  translateInit('langSelect');
};
