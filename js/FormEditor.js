function FormEditor(startEdit) {
  this.form = get('formEdit');
  this.init();
  if (startEdit) {
    this.editForm(get('button-form-edit'));
  }
};

FormEditor.prototype.init = function() {
  var formContainer = get('formatBox');
  var button = create('button',
                     {'id':'button-form-edit',
                      'class':'button edit text'},
                      '<div></div>Edit');
  before(button, formContainer.firstChild);
  button.onclick = func(this, this.editForm, [button]);
};

FormEditor.prototype.editRow = function(row) {
  new FieldEditor(this, row);
};

FormEditor.prototype.deleteRow = function(row) {
  row.parentNode.removeChild(row);
};

FormEditor.prototype.addEditButtonActions = function(editButton, deleteButton) {
  var row = editButton.parentNode.parentNode.parentNode;
  editButton.onclick = func(this, this.editRow, [row]);
  deleteButton.onclick = func(this, this.deleteRow, [row]);
};

/** Modify form controls for field editing. */
FormEditor.prototype.editForm = function(button) {

  // Activate hover behavior.
  addClass(get('formEdit-table'), 'editing');

  // Deactivate buttons.
  this.form.onsubmit = stopHandler;
  get('formEdit-buttons').style.display = 'none';

  // Deactivate input elements.
  var elts = this.form.getElementsByTagName('input');
  for (var ndx in elts) {
    var elt = elts[ndx];
    if (!elt.parentNode)
      continue;
    var buttons = elt.parentNode.getElementsByTagName('button');
    if (!buttons || buttons.length != 2)
      continue;
    if (elt.setAttribute)
      elt.setAttribute('disabled');
    this.addEditButtonActions(buttons[1], buttons[0]);
  }

  // Title editing control.
  var title = get('title');
  var curTitle = title.innerHTML;
  var titleEdit = create('input', {'id':'titleEdit',
                                   'name':'title',
                                   'size':'40',
                                   'value':curTitle});
  child = title.childNodes[0];
  if (child)
    title.removeChild(child);
  add(title, 'label', {'for':'titleEdit'}, 'Title:');
  title.appendChild(titleEdit);

  // Description editing control.
  var desc = get('description');
  var curText = desc.innerHTML;
  var ta = create('textarea', {'id':'descEdit',
                               'name':'description',
                               'cols':'80',
                               'rows':'20'},
                  curText);
  var child = desc.childNodes[0];
  if (child)
    desc.removeChild(child);
  add(desc, 'label', {'for':'descEdit'}, 'Description:');
  desc.appendChild(ta);

  this.newFieldButton(button);
  titleEdit.focus();

  // Change save button behavior.
  button.onclick = func(this, this.saveForm, [button]);
  button.childNodes[1].nodeValue = 'Save';
};

FormEditor.prototype.saveForm = function(button) {
  makePOSTRequest(location.href,
                  this.getFields(),
                  func(this, this.wakeForm, [button]));
};

FormEditor.prototype.wakeForm = function(button) {
  remove(get('button-add-field'));
  button.childNodes[1].nodeValue = 'Edit';
  button.onclick = func(this, this.editForm, [button]);
  this.form.onsubmit = null;

  var desc = get('description');
  var ta = get('descEdit');
  desc.innerHTML = ta.value;

  var title = get('title');
  var titleEdit = get('titleEdit');
  title.innerHTML = titleEdit.value;

  var inputs = this.form.getElementsByTagName('input');
  for (var ndx in inputs) {
    var input = inputs[ndx];
    if (input.removeAttribute)
      input.removeAttribute('disabled');
  }

  get('formEdit-buttons').style.display = 'block';
  removeClass(this.form, 'editing');
};

FormEditor.prototype.visitFields = function() {
  var form = get('formatBox');
  var fields = [];
  var nodes = [];
  var tags = form.getElementsByTagName('input');
  for (var ndx in tags)
    nodes.push(tags[ndx]);
  tags = form.getElementsByTagName('textarea');
  for (var ndx in tags)
    nodes.push(tags[ndx]);
  var fieldCount = 0;
  for (var i = 0; i < nodes.length; i++) {
    var node = nodes[i];
    var attrs = node.attributes;
    var value = node.value ? node.value : null;
    var parsedAttrs = {};
    if (node.nodeName == 'INPUT') {
      if (node.name == null)
        continue;
      if (attrs['type']) {
        var type = attrs['type'].value.toLowerCase();
        if (type == 'submit' || type == 'reset')
          continue;
        if (type == 'radio' || type == 'checkbox')
          if (attrs['checked'] == null)
            continue;
      }
      var label = findLabelForControl(node.name, form);
      if (label)
        parsedAttrs['help_text'] = label.innerHTML;
      if (node.name == 'title')
        parsedAttrs['value'] = node.value;
      else {
        // TODO(pmy): Order only attached to form fields.  If title
        // also had order, server will consider it a form field.
        parsedAttrs['order'] = fieldCount++;
      }
    } else if (node.nodeName == 'TEXTAREA') {  // Description.
      parsedAttrs = {'value':node.value};
    }
    fields.push({'name': node.name, 'attrs': parsedAttrs});
  }
  return fields;
};

FormEditor.prototype.getFields = function() {
  var arrIn = this.visitFields();
  var mapOut = {};
  for (var idx in arrIn) {
    var field = arrIn[idx];
    mapOut[field.name] = encodeFieldAttrs(field.attrs);
  }
  return mapOut;
}

FormEditor.prototype.getXsd = function() {
  var arrIn = this.visitFields();
  var arrOut = [];
  var title = "";
  for (idx in arrIn) {
    var field = arrIn[idx];
    // TODO(pmy): Why is this filtering necessary?
    if (field.name === undefined ||
	field.name === 'item') {
      continue;
    } else if (field.name === 'title') {
      title = field.attrs.value;
      continue;
    } else if (field.name === 'description') {
      // TODO(pmy): where does description go?
      continue;
    }
    arrOut.push(field);
  }

  var xsd = '<?xml version="1.0" encoding="utf-8"?>\n';
  xsd += '<xs:schema elementFormDefault="unqualified" xmlns:xs="http://www.w3.org/2001/XMLSchema" targetNamespace="http://datawiki/wiki/format/' + title + '">\n';
  xsd += '  <xs:element name="' + title + '">\n';
  xsd += '    <xs:complexType>\n';
  xsd += '      <xs:sequence>\n';
  for (var idx in arrOut) {
    var field = arrOut[idx];
    xsd += '        <xs:element name="' + field.name + '" type="xs:string" />\n';
  }
  xsd += '      </xs:sequence>\n';
  xsd += '    </xs:complexType>\n';
  xsd += '  </xs:element>\n';
  xsd += '</xs:schema>\n';

  return xsd;
}
/**
 * @param attrs An map of name:value attributes.
 * @return "field1Attr1Name,field1Attr1Value;field1Attr2Name,field1Attr2Value;..."
 */
function encodeFieldAttrs(attrs) {
  var encField = '';
  for (var name in attrs) {
    if (encField != '')
      encField += ";";
    encField += encodeURI(name) +','+ encodeURI(attrs[name]);
  }
  return encField;
}

FormEditor.prototype.newFieldButton = function(editButton) {
  var button = create('button',
                     {'id':'button-add-field',
                      'class':'button button-add-field plus text'},
                      '<div></div>Add Field');
  // TODO(pmy): can't get this working with func.
  var me = this;
  button.onclick = function() { new FieldEditor(me); };
  after(button, get('formEdit-buttons'));
  get('formEdit-buttons').parentNode.parentNode.style.backgroundColor = 'inherit';
};

FormEditor.prototype.newField = function(editorRow, label, name, required) {
  var fieldRow = create('tr');
  var labelCell = add(fieldRow, 'td');
  var inputCell = add(fieldRow, 'td');
  add(labelCell, 'label', {'for':name}, label);
  labelCell.innerHTML += ":";
  var input = add(inputCell, 'input', {'name':name, 'value':''});
  input.setAttribute('disabled');
  before(fieldRow, editorRow);
  var editButtons = add(inputCell, 'div', {'class':'edit-buttons hover-reveal'});
  // Delete first as floating both to the right reverses direction.
  var deleteButton = add(editButtons, 'button', {'class':'button delete'}, '<div></div>&nbsp;');
  var editButton = add(editButtons, 'button', {'class':'button edit'}, '<div></div>&nbsp;');
  this.addEditButtonActions(editButton, deleteButton);
  get('button-add-field').focus();
  return true;
};
