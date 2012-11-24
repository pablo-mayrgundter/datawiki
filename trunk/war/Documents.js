console.log('Documents script loaded.');
function Documents() {
  console.log('Documents script init.');
  new Tabs(get('formTabs').getElementsByTagName('li'),
           get('tabbedForms').getElementsByTagName('form'));
  new Tabs(get('chartTabs').getElementsByTagName('li'),
           [get('detailCharts'), get('statsCharts')]);
};
