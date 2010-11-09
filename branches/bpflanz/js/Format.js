function Format(startEdit) {
  new FormEditor(startEdit);
  //new FormConverter(get('formEdit'), get('xmlCode'), 'test').toXml();
};

function init(startEdit) {
  new Format(startEdit);
  translateInit('langSelect');
}
