function setTarget(elt) {
  var link = get('link');
  var newHref = elt.value;
  var trans = newHref.replace(/[^a-zA-Z_-]+/, '');
  if (newHref != trans) {
    elt.value = '';
    alert('Format name must be composed of only upper and lower-case letters, underscores and dashes.');
    return;
  }
  link.href = newHref;
}
