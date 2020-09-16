Output
======

HTTP API
--------

The API offers three types of output:

* JSON under /api
* rendered maps under /render
* generated landscape documentation under /docs
* search under /api/{lanscape}/search/{lucene style query}

Please see the swagger documentation under /v2/api-docs (JSON) or /swagger-ui.html (html gui) for details.

Searching
---------

Nivio indexes all landscape items in an in-memory search engine called Lucene. You can build sophisticated queries on
various item fields (see Model). For further information, see https://www.lucenetutorial.com/lucene-query-syntax.html


Item Icons and Background
-------------------------

The default icon of a item is determined by its item type (e.g. server, messagequeue, database...). To change the
appearance of a item to a vendor logo, for instance, the *icon* or *fill* property can be set. Both properties take
a valid URL.

While icon is rendered as centered image on the node, fill is used to paint the entire background and is more suitable
to be used with images, photos and so on.

.. code-block:: yaml
   :linenos:

   items:
      - identifier: foo
        shortName: blog1
        icon: http://my.custom/icon.png
      - identifier: bar
        shortName: db2
        fill: http://my.custom/background.png


Vendor Icons
------------

The *icon* property can also work with a predefined vendor name like "redis", prefixed with "vendor://" as scheme.

Vendor icons are work in progress.

.. code-block:: yaml
   :linenos:

    items:
      - identifier: bar
        icon: vendor://redis

Custom(er) Branding
===================

The appearance of rendered maps can be altered to match corporate identities. When a svg map is created, nivio tries to
load and include custom css from an URL that can be configured in the landscape configuration. Furthermore, a logo can be
included. A logo is configured in the landscape config, too, and must be a URL pointing to an includable file.

.. code-block:: yaml
   :linenos:

   identifier: branded_landscape
   name: branded

   config:
     branding:
       mapStylesheet: https://acme.com/css/acme.css
       mapLogo: https://acme.com/images/logo.png

   items:
     ...


Graph Layout Tweaking
=====================

In rare cases the layout needs some manual improvements. Internally nivio uses a force directed layout, which can be
influence dby tweaking some parameters (although mxgraph is not used anymore, for further explanation see https://jgraph.github.io/mxgraph/java/docs/com/mxgraph/layout/mxFastOrganicLayout.html).

.. code-block:: yaml
   :linenos:

    identifier: nivio:example
    name: Landscape example
    sources:
      - url: "./items/dashboard.yml"
        format: nivio

    # landscape configuration
    config:
      groupLayoutConfig:

        # the higher, the longer the edges between groups
        forceConstantFactor: 2.8

        # higher value is cpu intensive, but can lead to better layouts
        maxIterations: 1000

        # can also influence edge length and layout
        minDistanceLimitFactor: 3.05

        # multiplies the max distance limit (where repul
        maxDistanceLimitFactor: 2

      itemLayoutConfig:

        # the higher, the longer the edges between groups
        forceConstantFactor: 2.8

        # higher value is cpu intensive, but can lead to better layouts
        maxIterations: 1000

        # can also influence edge length and layout
        minDistanceLimitFactor: 3.05

        # multiplies the max distance limit (where repul
        maxDistanceLimitFactor: 2