# Overview #
Create a system that allows for rapid development of many simple but interrelated datasets. Include mechanisms for crowd-sourcing and open syndication to create a network of sites capable of providing compatible dataset applications and updates.

# Dataset Discovery and Creation #
The process of dataset discovery and creation should be closely linked to encourage reuse and improvement of existing formats.

Wikipedia dedicates a narrative page to each concept, uses a flat namespace to encourage canonical naming, and encourages heavy use of interlinking to achieve a network effect of enriching concepts by hyper-definition.  All of these design elements should be used.

# Interlinked Formats #
A wiki gains most of its utility from interlinking its content.  In the data wiki, it should be possible to reuse existing formats, by linking to their pages as inlined definitions.  From the Person Finder example, had PFIF and many Google volunteers not been available, a data wiki should have been able to rapidly allow the definition of a schema from vCard + GeoRSS + some primitive free-text fields for status.

# Narrative Article #
The conventional use of a free text document for the main article allows an anarchy of contributors to construct a shared narrative of a concept. In the case of our running example, this kind of narrative is in fact the first thing you see on any of the person finder applications that were created, e.g. a story on the NY Times describing the situation in Haiti and the existence of a system on their site for reporting or searching for missing people, or on the Google instance a simple choice of "What is your situation? I'm looking for someone; I have information about someone."

# Application Creation #
Person Finder gives a good example of how a structured record can be more useful than a free text report. The particular semantics of each field, say name or place or status, each can be used to enable useful applications, such as searching for a loved one's name, showing how many people are missing in an area or checking just for updates to an existing record. This structure needs to be easy for a user to understand, and also map seamlessly through to open machine readable formats (e.g. XML) and syndication feeds (e.g. Atom). So structured data needs to be supported as a primary interface type. Such a system could be seen as a wiki for structured data, or simply a data wiki.

# Catalyzing Standards #
For further relief efforts in Haiti, standard schemas for resource definitions such as UPC and UNSPSC codes could be included into a resource have or need and used immediately to improve logistics accounting and routing.


# Syndication #
TBD