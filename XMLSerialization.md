# Goal #

DataWiki is designed to allow for easy use of multiple formats for structured data.  This is achieved by storing data in its serialized form as XML and in memory as paired Format/MultiPartDocument objects.

These representations are then used as the basis for further transformations to other formats.

# Requirements #

Here is a simple example of the various transformations used to represent  a missing person data format in DataWiki.

1) Go to http://wiki.acme.org/pfif and since it's an empty page you're presented with a form composer.  You create the following form, either as HTML or with a WYSIWYG editor:

```
<form action="http://wiki.acme.org/pfif" method="POST">
  Name: <input type="text" name="first">
  Surname: <input type="text" name="last">
  Status: <input type="text" name="status">
</form>
```

Then, save your form.

2) Now you're ready to enter data.  You can do this directly on the wiki by entering data in the form and submitting it, or by taking "the code" and distributing it to other sites or devices.  So long as the action is kept the same, the data will come back to the same wiki.  Alternatively, a feed of updates from another site using the format could be submitted.


3) Export data or view in other apps.  Represented as rows in a spreadsheet, here with multiple rows for a collection:

```
# wiki.acme.org-pfif.csv, note that filename represents format type.
# first,last,status
Pablo,M,l33t
Steve,H,l33t too
```

and a single record in an SMS message:

```
First: Pablo
Last: M
Status: l33t
# From http://wiki.acme.org/pfif
```


4) As XML documents and Atom feeds of collections.

```
<pfif xmlns="http://wiki.acme.org/pfif">
  <first>Pablo</first>
  <last>M</last>
  <status>l33t</status>
</pfif>
```

A collection of which would be:

```
<feed>
...
<entry>
... <!-- metadata for entry -->
<content type="text/xml">
<pfif>
  <first>Pablo</first>
  <last>M</last>
  <status>l33t</status>
</pfif>
</entry>
...
</feed>
```

Note that the form action is the format namespace, the file component can then become the root node in the XML.  More generally, imagine taking the most specific part of the collection name as the root tag, so action="http://wiki.acme.org/formats/people/pfif" would become namespace="http://wiki.acme.org/formats/people/pfif" and the root node would stay pfif.

In summary, this is dead simple, requires no systems-level programming and so is language neutral and the data is lossless across encodings.  More importantly, there's no format-specific code on the sever-side to support this kind of usage.. you can create a new format by specifying a new RESTful enpoint, e.g. action="/my/special/format" and this also becomes the new namespace.

Generating a Form from a Document, example with KML

The above discussion uses a very simple example where form is specified and its format in other encodings is derived.  What if you want to go in the reverse direction, from a format to a Form?  One approach is to use form field names that represent the XPath to each node in the source XML document.  This approach has not yet been fully considered, but appears to work on some simple examples.

Here's some sample KML:

```
<?xml version="1.0" encoding="UTF-8"?>
<kml xmlns="http://www.opengis.net/kml/2.2">
  <Placemark>
    <name>Simple placemark</name>
    <description>Attached to the ground. Intelligently places itself 
       at the height of the underlying terrain.</description>
    <Point>
      <coordinates>-122.0822035425683,37.42228990140251,0</coordinates>
    </Point>
  </Placemark>
</kml>
```

and here's a form to losslessly represent it:

```
<form action="/kml?category=haiti-quake" method="POST">
  <!-- Names values are case-insensitive. &#x50; is the character entity for "P". -->
  <input type="text" name="&#x50;lacemark/name">Simple placemark</input>
  <input type="text" name="&#x50;lacemark/description">Attached to the ground. Intelligently places itself 
                                                  at the height of the underlying terrain.</input>
  <input type="text" name="&#x50;lacemark/coordinates">-122.0822035425683,37.42228990140251,0</input>
</form>
```

A similar approach could be used to name the columns in a CSV or spreadsheet, and to express attributes in a text format.

XPath Representation in Form Name Fields

The HTML spec defines the value of a name field as CDATA [CI](CI.md):

> http://www.w3.org/TR/html401/interact/forms.html#adef-name-INPUT

with some additional rules on whitespace handling.  This means the value can be any sequence of characters, with the constraint that the characters not break HTML validity and that they be interpreted case-insensitive [CI](CI.md).  However, XML tags, and so XPath expressions, are case-sensitive.  An ugly hack (shown above) is to use &#xNN; entities for upper-case letters and assume all others are lower-case.  Alternatively, it may be that all current browser implementations pass them back and forth as-is.  But since DataWiki gets to control most of the process, it can use character entities for correctness, perhaps also with an auto-generated comment for readability.