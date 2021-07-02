Model and Syntax
================


Landscape
---------

A landscape is defined as a collection of items which somehow belong together, be it for technical
or business reasons. For example, a company department might model ALL its applications in production as one landscape and use grouping
or tagging to further separate the applications. A second landscape could be used to model a future layout with a different
infrastructure. Both landscapes could have items in common (like a database, load balancer, etc.), so their configuration can be reused.

.. include:: ./schema/Models/LandscapeDescription.rst
.. include:: ./schema/Models/SourceReference.rst
.. include:: ./schema/Models/LandscapeConfig.rst
.. include:: ./schema/Models/KPIConfig.rst
.. include:: ./schema/Models/LayoutConfig.rst
.. include:: ./schema/Models/Branding.rst
.. include:: ./schema/Models/GroupDescription.rst
.. include:: ./schema/Models/ItemDescription.rst
.. include:: ./schema/Models/InterfaceDescription.rst
.. include:: ./schema/Models/Link.rst

Plus, there are labels having a special meaning:

.. include:: inc_labels.rst


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
        frameworks:
          php: 7.1

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

* ``identifier``: A unique identifier in the landscape. Provided automatically via the dictionary key, so do not set it.
* ``contains`` Array of references to other items (identifiers and CQN queries).
* ``owner`` Owning party (e.g. marketing).
* ``description`` A short description.
* ``team`` Technical owner.
* ``contact`` Support/notification contact (email). May be addressed in case of errors.
* ``color`` A hex color code for rendering.
* ``links`` A map/dictionary of URLs to more information.

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
identifier is composed of these three: ``mylandscape``, ``agroup``, and ``theitem``. Since the group is optional, items with unique
identifier can also be addressed using ``mylandscape`` and ``theitem``, or just ``theitem``. Nivio tries to resolve the correct item and raises
an error if it cannot be found or the result is ambiguous.

Service references are required to describe a ``provider`` relation or ``dataflow``.

.. code-block:: yaml
   :linenos:

    items:
      - identifier: theservice
        group: agroup
        relations:
          - target: anothergroup/anotherservice
            format: json
            type: dataflow
