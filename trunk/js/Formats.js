var titleValid = false;
var shortNameValid = false;

function setTarget(elt) {
  var link = get('link');
  var newHref = elt.value;
  newHref = newHref.replace(/ /g, '_')
  link.href = newHref;
}

function init() {
  translateInit('langSelect');
  checkValid();
}

function checkTitle(elt) {
  var title = elt.value;
  var msg = '';
  if (!title.match(/^[-a-zA-Z0-9_ ]+$/)) {
    msg = 'The given format title is invalid.';
    titleValid = false;
  } else {
    titleValid = true;
  }
  elt.nextSibling.innerHTML = msg;
  checkValid();
}

function checkShortName(elt) {
  var shortName = elt.value;
  var msg = '';
  if (!shortName.match(/^[a-z]+$/)) {
    msg = 'The given short name is invalid.';
    shortNameValid = false;
  } else {
    shortNameValid = true;
  }
  elt.nextSibling.innerHTML = msg;
  checkValid();
}

function checkValid() {
  if (titleValid && shortNameValid) {
    document.getElementById('createButton').removeAttribute('disabled');
  } else {
    document.getElementById('createButton').disabled = 'true';
  }
}
