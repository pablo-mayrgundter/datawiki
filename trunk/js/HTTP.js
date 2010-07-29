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

var boundary = 'AaB03xbleh42';

/** http://www.w3.org/TR/html401/interact/forms.html#h-17.13.4.2 */
function multipartEncode(params) {
  var body = '--'+ boundary;
  for (var name in params) {
    body += '\r\nContent-Disposition: form-data; name="'+ name +'"\r\n\r\n'+ params[name] +'\r\n--'+ boundary;
  }
  return body + '--';
}

function makePOSTRequest(url, params, cb) {
  httpRequest(url, multipartEncode(params), cb, 'POST', "multipart/form-data; boundary="+ boundary);
}

function httpRequest(url, encodedParams, cb, verb, contentType) {
  http_request = false;
  if (window.XMLHttpRequest) { // Mozilla, Safari,...
    http_request = new XMLHttpRequest();
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
  http_request.open(verb, url, true);
  http_request.setRequestHeader("Content-Type", contentType);
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
