'use strict';

/**
 * The FormEditor provides UI actions for adding, deleting and
 * modifying form fields.
 */
function FormEditor(formId, startEdit, saveCb) {
  this.formId = formId;
  this.form = $(this.formId);
  this.fieldTable = $(this.formId + '-table');
  this.fieldTableBody = $(this.formId + '-table-body');
  this.editButton = $(this.formId + '-edit-button');
  this.modfieldButtons = $(this.formId + '-modfield-buttons');
  this.submitButtons = $(this.formId + '-submit-buttons');
  this.saveCallback = saveCb;
  //if (startEdit) {
  //  this.editForm(this.editButton);
  //}
};

/** Modify form controls for field editing. */
FormEditor.prototype.editForm = function() {

  // Deactivate form.
  this.form.onsubmit = function(){ return false; };

  // Activate hover behavior.
  addClass(this.fieldTable, 'editing');

  // Deactivate input elements.
  var elts = this.form.getElementsByTagName('input');
  for (var ndx in elts) {
    var elt = elts[ndx];
    if (!elt.parentNode) {
      continue;
    }
    var buttons = elt.parentNode.getElementsByTagName('button');
    if (!buttons || buttons.length != 2) {
      continue;
    }
    if (elt.setAttribute) {
      elt.setAttribute('disabled', true);
    }
    this.addEditButtonActions(buttons[1], buttons[0]);
  }

  // Title editing control.
  var title = $('title');
  if (title) {
    var curTitle = title.innerHTML;
    var titleEdit = create('input', {'id':'titleEdit',
                                     'name':'title',
                                     'size':'40',
                                     'value':curTitle});
    child = title.childNodes[0];
    if (child) {
      title.removeChild(child);
    }
    add(title, 'label', {'for':'titleEdit'}, 'Title:');
    title.appendChild(titleEdit);
  }

  // Description editing control.
  var desc = $('description');
  if (desc) {
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
    titleEdit.focus();
  }

  this.newFieldButton();
  this.editButton.childNodes[1].nodeValue = 'Save';
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

FormEditor.prototype.saveForm = function(button) {
  if (this.saveCallback) {
    this.saveCallback();
  }
  this.wakeForm();
};

FormEditor.prototype.wakeForm = function() {
  remove($('button-add-field'));
  var button = this.editButton;
  button.childNodes[1].nodeValue = 'Modify';

  /*
  var desc = $('description');
  var ta = $('descEdit');
  desc.innerHTML = ta.value;

  var title = $('title');
  var titleEdit = $('titleEdit');
  title.innerHTML = titleEdit.value;
  */

  var inputs = this.form.getElementsByTagName('input');
  for (var ndx in inputs) {
    var input = inputs[ndx];
    if (input.removeAttribute)
      input.removeAttribute('disabled');
  }

  removeClass(this.fieldTable, 'editing');
};

FormEditor.prototype.visitFields = function() {
  var form = this.fieldTableBody;
  var fields = [];
  var nodes = form.getElementsByTagName('input');
  var fieldCount = 0;
  for (var i = 0; i < nodes.length; i++) {
    var node = nodes[i];
    // TODO(pmy): Why is this filtering necessary?
    if (node.name === undefined ||
	node.name === 'item') {
      continue;
    }
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

FormEditor.prototype.toJson = function() {
  var fieldArr = this.visitFields();
  var json = {};
  for (var ndx in fieldArr) {
    json[fieldArr[ndx].name] = null;
  }
  return json;
};

/**
 * Quick and broken json. TODO(pmy): use json.org's.
 *
 * @param obj A map of name:value attributes.
 * @return "field1Attr1Name: "field1Attr1Value"; ..."
 */
FormEditor.prototype.toJsonStr = function() {
  var fieldArr = this.visitFields();
  var encField = '';
  for (var ndx in fieldArr) {
    var field = fieldArr[ndx];
    if (encField != '')
      encField += ",";
    console.log(field, field['attrs']);
    encField += '"'+ encodeURI(field.name) +'":null';
  }
  return '{'+ encField +'}';
};

FormEditor.prototype.toXsd = function() {
  var arrIn = this.visitFields();
  var arrOut = [];
  var title = "";
  for (var idx in arrIn) {
    var field = arrIn[idx];
    // TODO(pmy): Why is this filtering necessary?
    if (field.name === 'title') {
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
};

FormEditor.prototype.newFieldButton = function() {
  var button = create('button',
                      {'id':'button-add-field',
                       'class':'button button-add-field plus text'},
                      '<div></div>Add Field');
  before(button, $(this.formId + '-submit-buttons'));
  // TODO(pmy): can't get this working with func.
  var me = this;
  // TODO(pmy): add to tbody, not table.
  button.onclick = function() { new FieldEditor(me, null, me.fieldTableBody); };
};

FormEditor.prototype.newField = function(editorRow, label, name, required) {
  var fieldRow = create('tr');
  var labelCell = add(fieldRow, 'td');
  var inputCell = add(fieldRow, 'td');
  add(labelCell, 'label', {'for':name}, label);
  labelCell.innerHTML += ":";
  var input = add(inputCell, 'input', {'name':name, 'value':''});
  input.setAttribute('disabled', 'true');
  before(fieldRow, editorRow);
  var editButtons = add(inputCell, 'div', {'class':'edit-buttons hover-reveal'});
  // Delete first as floating both to the right reverses direction.
  var deleteButton = add(editButtons, 'button', {'class':'button delete'}, '<div></div>&nbsp;');
  var editButton = add(editButtons, 'button', {'class':'button edit'}, '<div></div>&nbsp;');
  this.addEditButtonActions(editButton, deleteButton);
  $('button-add-field').focus();
  return true;
};
