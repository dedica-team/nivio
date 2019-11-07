Model and Syntax
================

Landscape
---------

A landscape is defined as a collection of items and applications which somehow belong together, be it for technical
or business reasons. For example, a company department might model ALL its applications in production as one landscape and use grouping
or tagging to further separate the applications. A second landscape could be used to model a future layout with a different
infrastructure. Both landscapes could have items in common (like a database, load balancer etc.), so their configuration can be reused.


Landscape Items and Groups
--------------------------

A landscape consists of several groups (think of bounded contexts) and the three layers ingress, items, and infrastructure
for technical separation. Any item can only be part of one group and layer.

**Service configuration file**

.. code-block:: yaml
   :linenos:

    items:
      - identifier: blog-server
        shortName: blog1
        group: content

      - identifier: auth-gateway
        shortName: blog1
        layer: ingress
        group: content

      - identifier: DB1
        software: MariaDB
        version: 10.3.11
        type: database
        layer: infrastructure

    groups:
      content:
        description: All services responsible to provide information on the web.
        owner: Joe Armstrong
        team: Team Content
        contact: joe@acme.org
        color: "#345345"
        links:
          wiki: http://wiki.acme.org/teamContent

      infrastructure:
        team: Admins
        contains:
          - DB1
          - "identifier LIKE 'DB1'" #same


A item can have the following attributes:

* **identifier**: a unique identifier in the landscape. Use a name or an URN, validated against ^[a-z0-9\\.\\:_-]{3,256}$
* **group** name of the group (optional). If a group is given it becomes part of the global identifier
* **name** human readable, displayed name
* **type** e.g. item, database, proxy, loadbalancer, ...
* **layer** ingress, applications, or infrastructure
* **shortName** abbreviation
* **capability** the capability the item provides for the business, or in case of infrastructure the technical purpose like enabling item discovery, configuration, secrets or persistence.
* **version** any string describing a item version (e.g. 1.2.5)
* **software** optional name of the used software/product
* **owner** owning party (e.g. Marketing)
* **description** a short description
* **team** technical owner
* **contact** support/notification contact (email) may be addressed in case of errors
* **links** a map/dictionary of urls to more information
* **visibility** whether the item is publicly exposed
* **tags** list of strings used as tag
* **networks** list of network names (can be defined somewhere else)
* **machine** description of the underlying virtual or physical machine
* **scale** number of instances (or other description)
* **hostType** e.g. docker, VM, bare metal
* **note** any note attached to the item
* **costs** running costs of the item. Stored as string
* **lifecycle** life cycle phase. One of "planned", "integration", "production", "end of life" (abbrevs work)
* **statuses** status objects, represented in colors
  * label: stability, capability, health, security ....)
  * status: green, yellow, orange, red, brown
  * message: Everything ok.
* **interfaces** an array of provided interfaces or endpoints
  * description: description
  * format: media type or binary format
  * url: an url pointing to the interface
* **relations** connections to other items
  * type: provider (hard dependency) or data flow (soft dependency)
  * description: description
  * target: a item identifier
  * format: media type or binary format
* **providedBy** array of references to other items (identifiers)


Groups can have the following attributes:

* **identifier**: a unique identifier in the landscape. Provided automatically via the dictionary key, do not set it
* **contains** array of references to other items (identifiers and CQN queries)
* **owner** owning party (e.g. Marketing)
* **description** a short description
* **team** technical owner
* **contact** support/notification contact (email) may be addressed in case of errors
* **color** a hex color code for rendering
* **links** a map/dictionary of urls to more information

Item Identification and Referencing
------------------------------------

A item can be uniquely identified by its landscape, its group and its identifier. A fully qualified
identifier is composed of these three: **mylandscape/agroup/theitem**. Since the group is optional, items with unique
identifier can also be addressed using **mylandscape/theitem** or just **theitem**. Nivio tries to resolve the correct item and raises
an error if it cannot be found or the result is ambiguous.

Service references are required to describe a provider relation or data flows.

.. code-block:: yaml
   :linenos:

    items:
      - identifier: theservice
        group: agroup
        relations:
          - target: anothergroup/anotherservice
            format: json
            type: dataflow





Using Templates to dynamically assign data
---------------

To prevent repetitive configuration of items, i.e. entering the same owner again and again,
templates can be used to prefill values. Templates a just item descriptions, except that
the identifier is used for referencing and that names are ignored. A template value is ony applied
if the target value is null.

Multiple templates can be assigned to items, too. In this case the first assigned value "wins" and
will not be overwritten by templates applied later.

.. code-block:: yaml
   :linenos:

    identifier: nivio:example
    name: Landscape example

    sources:
      - url: "./items/docker-compose.yml"
        format: docker-compose-v2
        assignTemplates:
          endOfLife: [web]
          myGroupTemplate: ["*"]

    templates:

      myGroupTemplate:
        group: billing

      endOfLife:
        tags: [eol]
        statuses

For CQ queries, read https://github.com/npgall/cqengine#string-based-queries-sql-and-cqn-dialects.
