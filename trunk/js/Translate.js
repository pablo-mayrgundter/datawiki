var translateLoaded = false;
try {
  google.load('language', '1');
  translateLoaded = true;
} catch(e) {
  console.log('Could not load translate library: '+ e);
}

window.onload = function() { translateInit(); }

function translateInit() {
  if (!translateLoaded)
    return;
  var eltId = 'langSelect';
  var curLang = 'en';
  if (document.cookie) {
    var langParam = document.cookie.match(/lang=[^;]+/);
    if (langParam) {
      curLang = langParam[0].split('=')[1];
    }
  }
  languageSelector(curLang);
  if (curLang != 'en') {
    document.getElementById('transProgress').style.visibility = 'visible';
    setTimeout('failsafeProgressHide()', 3000);
    setTimeout('translate(\''+ curLang + '\')', 1000);
  }
};

function languageSelector(curLang) {
  var elt = document.getElementById('langSelect')
  var languages = google.language.Languages;
  var select = document.createElement('select');
  select.setAttribute('onchange', 'setLangCookie(this.value)');
  for (var lang in languages) {
    var langCode = languages[lang];
    var translatable = google.language.isTranslatable(langCode);
    if (translatable) {
      var option = document.createElement('option');
      option.value = langCode;
      option.innerHTML = lang.substring(0,1) + lang.substring(1).toLowerCase();
      if (langCode == curLang) {
        option.setAttribute('selected');
      }
      select.appendChild(option);
    }
  }
  elt.appendChild(select);
  var img = document.createElement('img');
  img.id = 'transProgress';
  img.style.visibility = 'hidden';
  img.src = '/loader.gif';
  elt.appendChild(img);
};

function translate(toLang) {
  var translateHandler = 'translateDone';
  // This technique below is called JSONP.  We are going to add a script to the page
  // that has the src of the query we wish to run.  When the query completes, it
  // will return JSON that will be passed to the translateComplete function
  var newScript = document.createElement('script');
  newScript.type = 'text/javascript';
  var source = 'http://ajax.googleapis.com/ajax/services/language/translate'
    + '?v=1.0&callback='+ translateHandler;

  // TODO(pmy): fix globals;
  transToLang = toLang;
  //transNodes = eltsByClass('trans');
  transNodes = eltsToTranslate();
  for (var ndx in transNodes) {
    var transNode = transNodes[ndx];
    // find/create a new attribute on the node which stores the
    // original text so successive translations don't degrade.
    var origText = transNode['origText'];
    if (!origText) {
      origText = transNode.nodeValue;
      transNode['origText'] = origText;
    }
    // Keep the corresponding language, or 
    var origLang = transNode['origLang'];
    if (!origLang) {
      origLang = transNode.lang;
      if (!origLang) {
        origLang = ''; // Trans API uses this to signal auto-detect.
      }
    }

    // TODO(pmy): too easy to get big URIs that server denies.
    //source += '&q='+ origText + '&langpair='+ origLang +'|'+ toLang;
    source += '&q='+ origText;
  }
  source += '&langpair=|'+ toLang;

  newScript.src = source;
  // When we add this script to the head, the request is sent off.
  document.getElementsByTagName('head')[0].appendChild(newScript);
};

function translateDone(response) {
  var responseData = response['responseData'];
  if (responseData) {
    if (!(responseData instanceof Array)) {
      responseData = [{'responseData':responseData}];
    }
    for (var i = 0; i < responseData.length; i++) {
      var result = responseData[i].responseData;
      var translation = result.translatedText;
      var detectedSourceLanguage = result.detectedSourceLanguage;
      if (translation) {
        var transNode = transNodes[i];
        transNode.nodeValue = translation;
        transNode.lang = transToLang;
        if (!transNode['origLang']) {
          if (detectedSourceLanguage) {
            transNode['origLang'] = detectedSourceLanguage;
          } else {
            transNode['origText'] = translation;
            transNode['origLang'] = transToLang;
          }
        }
      }
    }
  } else {
    alert('Cannot translate page.');
  }
  document.getElementById('transProgress').style.visibility = 'hidden';
};

function eltsToTranslate() {
  var withText = [];
  eltsWithText(eltsByClass('trans'), withText);
  return withText;
};

function eltsWithText(elts, withText) {
  for (var i = 0; i < elts.length; i++) {
    var elt = elts[i];
    var childNodes = elt.childNodes;
    if (childNodes.length == 0) {
      if (hasText(elt)) {
        withText.push(elt);
      }
    } else {
      eltsWithText(childNodes, withText);
    }
  }
};

var textRegex = new RegExp('[ "\'a-zA-Z0-9!()]+');
function hasText(elt) {
  if (elt.nodeType == 3) {
  }
  // TODO(pmy): non-portable check for text node.
  if (elt.nodeType == 3
      && textRegex.test(elt.nodeValue.trim())) {
    return true;
  }
  return false;
};

function eltsByClass(className) {
  var elts = [];
  var classRegex = new RegExp('\\b'+ className +'\\b');
  var nodes = document.getElementsByTagName('*');
  for (var i = 0; i < nodes.length; i++) {
    var classes = nodes[i].className;
    if (classRegex.test(classes)) {
      elts.push(nodes[i]);
    }
  }
  return elts;
};

function setLangCookie(lang) {
  document.cookie = 'lang='+ lang +'; path=/';
  // TODO(pmy): Rethink this.
  // Need the timeout so cookie can register before refresh.
  setTimeout('location.href = location.href', 10);
};

function failsafeProgressHide() {
  document.getElementById('transProgress').style.visibility = 'hidden';
};
