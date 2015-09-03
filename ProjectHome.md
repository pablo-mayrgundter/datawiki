Hello!  Welcome to the DataWiki project page :)

You can use the live instance here:

> http://datawiki.appspot.com/

DataWiki has the features listed below, with TODO items currently being developed.

## User Flows ##
  * Create and edit simple formats to define the content of a dataset.
  * Can create, edit, delete, list and search items within the dataset.
  * Paging and sorting for list and search.
  * If the format includes geo coords, can be displayed on a map, though limited to the same paging operation limits, i.e. N at a time.
  * TODO: complex format definition, via defining formats in terms of others, e.g. Missing Person reports = vCard (who) + GeoRSS (last seen) + string (current status note)
  * TODO: image upload

## Security ##
  * Login managed by Google (i.e. @gmail and also third-party)
  * Access Control Lists, kept separately for dataset and for items, so users can create and control their own entries.
  * TODO: differential views based on ACLs
  * TODO: CAPTCHAs

## Platforms ##
  * Java on Google App Engine, so highly reliable and scalable (modulo bugs!)
  * Angular JS frontend (modified from angularjs.org sources).
  * REST API supporting JSON.
  * TODO: REST w/XML
  * TODO: Federation via Atom
  * TODO: Open Data Kit for Android-based collection.
  * TODO: Plugin for Google Spreadsheets/Fusion Tables.

DataWiki runs on the Google AppEngine for Java platform and so may also be run on private networks or offline on a laptop in the field.

# Demo #

The current code is running here:

> http://datawiki.appspot.com/


There's an older version running here:

http://rhokhub.appspot.com/wiki/Connectivity_Mapper

NOTE: this instance is using the releases circa 2010 for a hackathon, and is not compatible with the current codebase.

# Developers #

See the GettingStartedGuide.

NOTE: the issues list, especially defects, is out of date.

# REST API #

See the DataHub page that datawiki uses for its server-side platform:

> http://code.google.com/p/datahub