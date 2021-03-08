Model and Syntax
================


Landscape
---------

A landscape is defined as a collection of items which somehow belong together, be it for technical
or business reasons. For example, a company department might model ALL its applications in production as one landscape and use grouping
or tagging to further separate the applications. A second landscape could be used to model a future layout with a different
infrastructure. Both landscapes could have items in common (like a database, load balancer, etc.), so their configuration can be reused.

A landscape can/must have the following attributes:

* **identifier**: A unique identifier. Use a name or an URN, validated against ``^[a-z0-9\\.\\:_-]{3,256}$``
* **name** Human readable, displayed name
* **contact** E.g. an email
* **description** A short text describing the landscape

Landscape Items
---------------

An item represents anything that has a meaning in the landscape. It can be a server, a service, some hardware, or a person.

A item should have the following attributes:

* **identifier**: A unique identifier in the landscape. Use a name or a URN, validated against ``^[a-z0-9\\.\\:_-]{3,256}$``
* **group** Name of the group (optional). If a group is given, it becomes part of the global identifier. All items can only be part of one group.
* **name** Human readable, displayed name
* **contact** Support/notification contact (email). May be addressed in case of errors.
* **description** A short description
* **icon** An icon URL
* **color** An HTML color

Other fields:

* **address** A technical address like a URI
* **links** A map/dictionary of URLs to more information
* **lifecycle** Life cycle phase. One of "planned", "integration", "production", "end of life" (abbreviations work).
* **status** Status objects, represented in colors
    * label: Stability, capability, health, security, ...
    * status: Green, yellow, orange, red, brown
    * message: Everything OK.
* **interfaces** An array of provided interfaces or endpoints
    * description: Description
    * format: Media type or binary format
    * url: A URL pointing to the interface
* **relations** Connections to other items
    * type: Provider (hard dependency) or data flow (soft dependency)
    * description: Description
    * target: An item identifier
    * format: Media type or binary format
* **providedBy** Array of references to other items (identifiers)


Plus, there are labels having a special meaning:

.. include:: labels.rst


You can also store **custom properties** as labels, but keep in mind that

* label keys are converted to lowercase and
* label values are stored as string.

**Item configuration**

.. code-block:: yaml
   :linenos:

    items:
      - identifier: blog-server
        shortName: blog1
        group: content
        mycustomlabel1: foo
        mycustomlabel_2: bar
        any: entry is stored as label

      - identifier: auth-gateway
        shortName: blog1
        layer: ingress
        group: content

      - identifier: DB1
        software: MariaDB
        version: 10.3.11
        type: database
        layer: infrastructure

Item Groups
-----------
Groups can have the following attributes:

* **identifier**: A unique identifier in the landscape. Provided automatically via the dictionary key, so do not set it
* **contains** Array of references to other items (identifiers and CQN queries)
* **owner** Owning party (e.g. marketing)
* **description** A short description
* **team** Technical owner
* **contact** Support/notification contact (email). May be addressed in case of errors
* **color** A hex color code for rendering
* **links** A map/dictionary of URLs to more information

**Group configuration**

.. code-block:: yaml
   :linenos:

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


Item Identification and Referencing
------------------------------------

An item can be uniquely identified by its landscape, its group, and its identifier. A fully qualified
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
