function FormConverter(form, xml, format) {
  this.form = form;
  this.xml = xml;
  this.format = format;
};

FormConverter.prototype.xmlTag = function(name, value, attrs) {
  if (!attrs)
    attrs = {};
  if (!value)
    add(this.xml, 'div', attrs, '&lt;'+ name +'/&gt;');
  else
    add(this.xml, 'div', attrs, '&lt;'+ name +'&gt;'+ value +'&lt;/'+ name +'&gt;');
};

// TODO(pmy): root and namespace, option groups, files, testing (esp. hidden/password/submit/reset handling).
FormConverter.prototype.toXml = function() {
  this.xml.innerHTML = '<h2>XML</h2>';
  add(this.xml, 'div', {}, '&lt;'+ this.format +' xmlns="fixme!"&gt;');
  var nodes = this.form.getElementsByTagName('input');
  for (var i = 0; i < nodes.length; i++) {
    var node = nodes[i];
    var attrs = node.attributes;
    var value = attrs['value'] ? attrs['value'].value : null;
    if (node.nodeName == 'INPUT') {
      var name = attrs['name'] ? attrs['name'].value : null;
      if (name == null || name == 'format' || name == 'namespace')
        continue;
      if (attrs['type']) {
        var type = attrs['type'].value;
        if (type == 'radio'.toLowerCase() || type == 'checkbox'.toLowerCase())
          if (attrs['checked'] == null)
            continue;
      }
      if (name.indexOf('/') != -1) {
        var parts = name.split('/');
        name = parts[parts.length - 1];
      }
      this.xmlTag(name, value);
    } else if (node.nodeName == 'TEXTAREA') {
      this.xmlTag(attrs['name'].value, node.innerHTML);
    }
  }
  add(this.xml, 'div', {}, '&lt/'+ this.format +'&gt;');
};
