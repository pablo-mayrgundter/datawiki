# Motivation #

The need for a data sharing platform and the concept for DataWiki was developed out of Google's experiences deveoping Person Finder for the Haiti earthquake, which itself was developed to accept missing person reports and to act as a hub for sharing of missing person data between other emerging sites.  More history on that later.

More examples for now:

  * http://radar.oreilly.com/2010/03/truly-open-data.html
  * http://www.readwriteweb.com/archives/boom_tweets_maps_swarm_to_pinpoint_a_mysterious_ex.php


# Requirements - A wiki approach to sharing for structured data #

An approach is needed which allows for rapid development of simple structured data definitions and mechanisms for crowd-sourcing and open syndication to create a network of sites capable of providing compatible dataset updates along with specialized services to collectively develop communal information.

The wiki is a natural medium for this kind of rapid, open sharing.  How can the existing wiki models inform the design of a data wiki?  Existing wikis like Wikipedia dedicate a narrative page to each concept, use a flat namespace to encourage canonical naming, and encourage heavy use of interlinking to achieve a network effect of enriching concepts by hyper-definition.


## Narrative Article ##

The conventional use of a free text document for the main article allows an anarchy of contributors to construct a shared narrative of a concept.  In the case of our running example, this kind of narrative is in fact the first thing you see on any of the person finder applications that were created, e.g. a story on the NY Times describing the situation in Haiti and the existence of a system on their site for reporting or searching for missing people, or on the Google instance a simple choice of "What is your situation? I'm looking for someone; I have information about someone."


## Pages as Datasets ##

TBD - A data wiki could follow this model by using a page to represent a conceptual grouping of data, i.e. a dataset of documents of a certain format.


## Structured Records enabling Applications ##

Person Finder gives a good example of how a structured record can be more useful than a free text report.  The particular semantics of each field, say name or place or status, each can be used to enable useful applications, such as searching for a loved one's name, showing how many people are missing in an area or checking just for updates to an existing record.  This structure needs to be easy for a user to understand, and also map seamlessly through to open machine readable formats (e.g. XML) and syndication feeds (e.g. Atom).  So structured data needs to be supported as a primary interface type.  Such a system could be seen as a wiki for structured data, or simply a data wiki.


## Interlinked Formats ##

The canonical namespace, the page/dataset URL, could be used as a canonical name for that type.  This would provide seamless support for an associated XML namespace for documents in that collection.  Also, by using the composibility of XML schemas, data definitions could be made in terms of each other, thereby enabling a network effect of rich data definitions.  Had PFIF and many Google volunteers not been available, a data wiki should have been able to rapidly allow the definition of a schema from vCard + GeoRSS + some primitive free-text fields for status.


## Syndication ##

TBD

## Catalyzing Standards ##

TBD