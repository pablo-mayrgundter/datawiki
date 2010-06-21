function include(file) {
  var head = document.getElementsByTagName('head')[0];
  var script = document.createElement('script');
  script.src = file;
  script.type = 'text/javascript';
  head.appendChild(script);
}

// http://ejohn.org/blog/flexible-javascript-events
function addEvent( obj, type, fn ) {
  if ( obj.attachEvent ) {
    obj['e'+type+fn] = fn;
    obj[type+fn] = function(){obj['e'+type+fn]( window.event );}
    obj.attachEvent( 'on'+type, obj[type+fn] );
  } else
    obj.addEventListener( type, fn, false );
}
function removeEvent( obj, type, fn ) {
  if ( obj.detachEvent ) {
    obj.detachEvent( 'on'+type, obj[type+fn] );
    obj[type+fn] = null;
  } else
    obj.removeEventListener( type, fn, false );
}

function func(obj, handler, args) {
  if (!args)
    args = [];
  return function() { handler.apply(obj, args); };
}

function get(id) {
  return document.getElementById(id);
}

function elts(name) {
  return document.getElementsByTagName(name);
}

function eltsByIdPrefix(prefix, node)  {
  if (!node)
    node = document.getElementsByTagName("body")[0];
  var elts = {};
  var re = new RegExp('^'+ prefix);
  var subNodes = node.getElementsByTagName("*");
  for (var i = 0; i < subNodes.length; i++)
    if (re.test(subNodes[i].id))
      elts[subNodes[i].id] = subNodes[i];
  return elts;
}

var serial = 0;
function loadTemplate(parent, tplId) {
  var prefix = tplId +'_'+ (serial++) +'-';
  return [prefix, loadTemplateWithPrefix(prefix, parent, tplId)];
}

function loadTemplateWithPrefix(prefix, parent, tplId) {
  var tpl = document.getElementById('template-'+ tplId).innerHTML;
  parent.innerHTML = tpl;
  var elts = eltsByIdPrefix('template-', parent);
  var objsByNewId = {};
  for (var id in elts) {
    var elt = elts[id];
    var newId = prefix + id.substring('template-'.length, id.length);
    elt['id'] = newId;
    objsByNewId[newId] = elt;
  }
  return objsByNewId;
}

function add(parent, name, attrs, text) {
  var e = create(name, attrs, text);
  parent.appendChild(e);
  return e;
}

function remove(node) {
  node.parentNode.removeChild(node);
}

function before(newElt, node) {
  node.parentNode.insertBefore(newElt, node);
}

function after(newElt, node) {
  if (!node.nextSibling) {
    node.parentNode.appendChild(newElt);
  } else {
    node.parentNode.insertBefore(newElt, node.nextSibling);
  }
}

var debugElt;
function d(msg) {
  if (null == debugElt) {
    debugElt = get('debug');
    if (null == debugElt)
      return;
  }
  debugElt.innerHTML += msg + '<br/>';
}

/**
 * @param attrs A map of attribute names to objects, which may include
 * event handlers.  Optional, but bust be specified if inner is
 * specified.
 * @param inner The innerHTML of the tag, either as a string or as an
 * array of triplets which will be recursively created by calls to
 * this method.  Optional.
 */
function create(name, attrs, inner) {
  var elt = document.createElement(name);
  d('elt: '+ elt);
  if (attrs)
    for (var attr in attrs) {
      var val = attrs[attr];
      if (val.constructor == String)
        elt.setAttribute(attr, val);
      else
        elt[attr] = val;
    }
  if (inner)
    if (!(inner instanceof Array)) {
      elt.innerHTML = inner;
    } else
      for (var i = 0; i < inner.length; i++) {
        var e = inner[i];
        if (!(e instanceof Array)) {
          alert('Attempt to create node: '+ name +' with non-array node list.');
          return;
        } if (e.length == 0) {
          alert('Attempt to create node: '+ name +' with empty inner node list.');
          return;
        }
        else if (e.length == 1)
          elt.appendChild(create(e[0]));
        else if (e.length == 2)
          elt.appendChild(create(e[0], e[1]));
        else if (e.length == 3)
          elt.appendChild(create(e[0], e[1], e[2]));
      }
  return elt;
}

function stopHandler(e) {
  return false;
}

function checkClass(obj, clazz) {
  return new RegExp('\\b'+ clazz +'\\b').test(obj.className);
}

function removeClass(obj, clazz) {
  var rep = obj.className.match(' '+ clazz) ? ' '+ clazz:clazz;
  obj.className = obj.className.replace(rep, '');
}

function addClass(obj, clazz) {
  if (!checkClass(obj, clazz)) {
    obj.className += obj.className ? ' '+ clazz:clazz;
  }
}

function findLabelForControl(name, startAt) {
  if (!startAt)
    var startAt = document;
  var labels = startAt.getElementsByTagName('label');
  for (var i = 0; i < labels.length; i++) {
    var label = labels[i];
    if (label.htmlFor == name)
      return label;
  }
  return null;
}