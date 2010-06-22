// From: http://www.captain.at/howto-ajax-form-post-request.php
var http_request = false;
var callback = null;

function formEncode(params) {
  var encoded = '';
  for (var name in params) {
    var value = params[name];
    var part = encodeURI(name) +'='+ encodeURI(value);
    if (encoded != '')
      encoded += '&';
    encoded += part;
  }
  return encoded;
}

function makePOSTRequest(url, encodedParams, cb) {
  http_request = false;
  if (window.XMLHttpRequest) { // Mozilla, Safari,...
    http_request = new XMLHttpRequest();
    if (http_request.overrideMimeType) {
      // set type accordingly to anticipated content type
      //http_request.overrideMimeType('text/xml');
      http_request.overrideMimeType('text/html');
    }
  } else if (window.ActiveXObject) { // IE
    try {
      http_request = new ActiveXObject("Msxml2.XMLHTTP");
    } catch (e) {
      try {
        http_request = new ActiveXObject("Microsoft.XMLHTTP");
      } catch (e) {}
    }
  }
  if (!http_request) {
    alert('Cannot create XMLHTTP instance');
    return false;
  }

  callback = cb;
  http_request.onreadystatechange = alertContents;
  http_request.open('POST', url, true);
  http_request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
  http_request.send(encodedParams);
}

function alertContents() {
  if (http_request.readyState == 4) {
    if (http_request.status == 200) {
      //result = http_request.responseText;
      //document.getElementById('myspan').innerHTML = result;
      if (callback)
        callback.call();
    } else {
      alert('There was a problem with this request.  '+ http_request.responseText);
    }
  }
}
