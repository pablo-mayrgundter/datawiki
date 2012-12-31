'use strict';

/**
 * The FieldEditor provides controls for editing a row of a form.
 */
function FieldEditor(form, row, table) {
  var oldHelpText = null;
  var oldFieldName = null;
  if (row) {
    this.editorRow = row;
    var fieldTableCells = row.getElementsByTagName('td');
    oldHelpText = fieldTableCells[0].getElementsByTagName('label')[0].innerHTML;
    oldFieldName = fieldTableCells[1].getElementsByTagName('input')[0].name;
  } else {
    this.editorRow = add(table, 'tr', {'class':'fieldEditor'});
  }
  var prefixAndObjById = loadTemplate(this.editorRow, 'fieldEditor');
  this.prefix = prefixAndObjById[0];
  this.objsById = prefixAndObjById[1];
  this.myElt('delete').onclick = func(this, this.remove);
  this.myElt('done').onclick = func(this, this.done, [this.editorRow, form]);
  if (oldHelpText)
    this.myElt('text').value = oldHelpText;
  if (oldFieldName)
    this.myElt('name').value = oldFieldName;
  this.myElt('text').focus();
};

FieldEditor.prototype.myElt = function(tplId) {
  return this.objsById[this.prefix+tplId];
};

FieldEditor.prototype.remove = function() {
  this.editorRow.parentNode.removeChild(this.editorRow);
};

FieldEditor.prototype.done = function(editorRow, form) {
  if (form.newField(editorRow,
                    this.myElt('text').value,
                    this.myElt('name').value))
    this.remove();
};
