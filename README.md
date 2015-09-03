# Datawiki

Hello! Welcome to the DataWiki project page :)

DataWiki runs on the Google AppEngine for Java platform and so may also be run on private networks or offline on a laptop in the field.

DataWiki has the design and features listed below, with TODO items currently being developed.

## Demo
http://datawiki.appspot.com/

## REST API
See the DataHub page that datawiki uses for its server-side platform:

http://code.google.com/p/datahub

## Design
### User Flows
Create and edit simple formats to define the content of a dataset.
Can create, edit, delete, list and search items within the dataset.
Paging and sorting for list and search.
If the format includes geo coords, can be displayed on a map, though limited to the same paging operation limits, i.e. N at a time.

TODO:
  * complex format definition, via defining formats in terms of others, e.g. Missing Person reports = vCard (who) + GeoRSS (last seen) + string (current status note)
  * image upload

### Security
Login managed by Google (i.e. @gmail and also third-party)
Access Control Lists, kept separately for dataset and for items, so users can create and control their own entries.

TODO:
  * differential views based on ACLs
  * CAPTCHAs

### Platforms
Java on Google App Engine, so highly reliable and scalable (modulo bugs!)
Angular JS frontend (modified from angularjs.org sources).
REST API supporting JSON.

TODO:
  * REST w/XML
  * Federation via Atom
  * Open Data Kit for Android-based collection.
  * Plugin for Google Spreadsheets/Fusion Tables.

