function setTarget(elt) {
  var link = get('link');
  var newHref = elt.value;
  if (!newHref.match(/^[a-zA-Z_-]+$/)) {
    elt.value = '';
    alert('The given format name is invalid.');
    return;
  }
  link.href = newHref;
}

function init() {
  translateInit('langSelect');
}
