# Overview #
The DataWiki system presents a wiki for structured data, a federated approach to users interacting in complex organizational roles and a flexible architecture for online, mobile and offline use.

# Data Model #
DataWiki exists as a set of named resources.  Resources may be datasets, formats or documents.  Datasets contain documents related to a given format.  Documents and formats may be changed after creation, possibly diverging from each other.  A user interface is provided for users to understanding and resolving document-format divergence.

## Resource ##
A resource can be created, modified, expired or revived.

## Dataset ##
A dataset is a named list of documents and their format versions.  A dataset also contains metadata annotations such as an index of its formats and documents and a narrative description of the purpose of the data.

## Format ##
A format specifies a unique namespace for itself and defines a data schema for documents of the format.

Formats may be modified over time, by the addition, mutation or removal of fields.  A set of changes is saved as a new version of the format and previous versions are preserved.  The version is included in the namespace.

The schema is an ordered listing of document fields defined as (name,type,annotations) triplets.  Types are either primitives (e.g. text, integer, decimal) or complex types recursively defined by composition of primitive types and other formats.  Annotations are optional and provide metadata such as help text.

## Document ##
A document of a given format contains a reference to the format's namespace and a listing of field values for the fields defined in the format.

### Document Validation ###
A document can be validated if it contains a reference to a format and is valid if it also contains an ordered listing of all of the fields defined in the format.  TODO(pmy): discuss required vs. optional fields.

## Names ##
Names are strings with character restrictions to secure referential integrity.

# User Interaction #
Users are identifiable agents that perform actions on resources.  A user may be a person or another system.

## Authentication ##
A user's identity may be obtained from a trusted authentication system.

## Actions ##
Users may create, modify, expire or revive resources and search their index annotations.

## Capability ##
A capability is a resource annotation defined as (action,identity).

## Authorization ##
A user's action may be restricted if a capability annotation exists their identity does not pass the .

## Accounting ##
User actions are annotated on resources as (resource,action,identity,time).

# System Architecture #
## Data Model ##
Documents are stored as XML, formats as XSD, and an XForm represents the UI for document creation according to the format.

## Storage ##
Data is stored in the GAE Datastore, with JDO objects for Document and Format, and a Dataset being represented as a container of these objects.

## Web ##
Web access to the stored directories of these objects is presented as a RESTful web service.

## Syndication ##
Updates to documents and directories will be published using Atom feeds.  Documents will be in-lined into the Atom entry.

## Mobile ##
TBD: Mobile data collection and review will be provided by integration with ODK aggregate&collect for Android/smart-phones, and possibly FrontlineSMS for dumb-phones.

## Offline ##
GAE dev application server.