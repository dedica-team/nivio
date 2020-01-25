Dark Magic
==========

Assigning items to groups
-------------------------

**Service configuration file, see groups**

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
      infrastructure:
        team: Admins
        contains:
          - DB1
          - "identifier LIKE 'DB1'" #same


Using Templates to dynamically assign data
------------------------------------------

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


Using Labels to assign data
---------------------------

You can set labels (string:string) to items which are evaluated as model fields if

* the key contains "nivio." AND
* the rest of the key equals a field name.

For instance

.. code-block:: yaml
   :linenos:

    items:
      - identifier: theservice
        labels:
          nivio.name: A nice name
          nivio.providedBy: ["foo", "bar"]


will set the related values (here: name and relations). Remember to scape URLs with double quotes.

Labels can be set using docker-compose files, too. However, docker labels not not allow arrays, so use comma separated strings:

.. code-block:: yaml
   :linenos:

    services:
      foo:
        labels:
          nivio.name: A nice name
          nivio.providedBy: "bar, baz"


Relations between landscape items
---------------------------------

Usually environments such as Docker, K8s provide few to none information on the relation between landscape items (e.g.
which database a service uses). However, in 12-factor apps there is configuration through environment variables (https://12factor.net/config)
and these can be parsed hopefully. Nivio provides an experimental feature which regards these env vars as DSL. Env vars
are read and assigned as item labels, then examined in the following way:

* the key contains words like "url", "uri" etc.
* the value is an URL

If a criteria is matched, the value of the label is examined. In the of being an URL, the host and name path components are extracted as names or identifiers.
Using these, the landscape is searched for possible relation targets.
