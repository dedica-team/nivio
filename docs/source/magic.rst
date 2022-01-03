Shortcuts and convenience functions
===================================

Assigning items to groups
-------------------------

Often lots of items can be read from input data sources, but no information on logical grouping is available. To mitigate
that, you can describe groups and use the ``contains`` field:

* To pick items by their identifier, add single strings which are treated as identifiers.
* Furthermore you can use SQL-like WHERE conditions to assign items to groups. In the following example ``identifier LIKE 'DB1%'`` is the query which would match both items.

.. code-block:: yaml
   :linenos:

    items:
      - identifier: DB1-gateway
        shortName: blog1
        layer: ingress

      - identifier: DB1
        software: MariaDB
        version: 10.3.11
        type: database
        layer: infrastructure

    groups:
      infrastructure:
        team: Admins
        contains:
          - DB1
          - "identifier LIKE 'DB1%'"


Using Templates to dynamically assign data
------------------------------------------

To prevent repetitive configuration of items, i.e. entering the same owner again and again,
templates can be used to prefill values. Templates are just item descriptions, except that
the identifier is used for referencing and that names are ignored. A template value is only applied
if the target value is null.

Multiple templates can be assigned to items too. In this case the first assigned value "wins" and
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

For CQ queries read https://github.com/npgall/cqengine#string-based-queries-sql-and-cqn-dialects.


Using Labels to assign data
---------------------------

You can set labels (string:string) to items which are evaluated as model fields if

* the key contains ``nivio.`` **AND**
* the rest of the key equals a field name.

Labels can be set using docker-compose files too. However, docker labels do not allow arrays, so use comma separated strings:

.. code-block:: yaml
   :linenos:

    services:
      foo:
        labels:
          nivio.name: A nice name
          nivio.providedBy: "bar, baz"
          nivio.relations: "atarget, anotherTarget"
          nivio.link.repo: "https://github.com/foo/bar"

Remember to escape URLs with double quotes.

Relations between landscape items
---------------------------------

Usually environments such as Docker or K8s provide few to none information on the relation between landscape items (e.g.
which database a service uses). However, in 12-factor apps there is configuration through environment variables (https://12factor.net/config)
and these can be parsed. Nivio provides an experimental feature which regards these variables as :abbr:`DSL (???)`. They
are read and assigned as item labels, then examined:

* The key is split using the underscore character.
* If it contains parts like ``url``, ``uri``, ``host`` etc., the label is taken into account as **identifier**, i.e. Nivio looks for a target having the identifier, name, or address equal to the value.

Labels are examined as follows:

* In the case of being an URI, the host and name path components are extracted and used as names or identifiers.


To prevent false positives, certain labels can be omitted:

.. code-block:: yaml
   :linenos:

    identifier: some-landscape

    items:
      - identifier: foo
        labels:
          HOST: bar
          SOME_LABEL: mysql://ahost/foobar

      - identifier: bar
        type: database
