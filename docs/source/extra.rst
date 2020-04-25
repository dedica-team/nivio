Item Icons and Background
=========================

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

In rare cases the layout needs some manual improvements. Internally nivio uses mxGraph (for Java), which can be influences
by tweaking some parameters (see https://jgraph.github.io/mxgraph/java/docs/com/mxgraph/layout/mxOrganicLayout.html).

.. code-block:: yaml
   :linenos:

    identifier: nivio:example
    name: Landscape example
    sources:
      - url: "./items/dashboard.yml"
        format: nivio

    # landscape configuration
    config:
      jgraphx:
        triesPerCell: 8
        edgeLengthCostFactor: 0.0001
        nodeDistributionCostFactor: 900000.0
        borderLineCostFactor: 7.0

        #
        # for group alignment
        #

        # the higher, the longer the edges between groups
        forceConstantFactor: 2.8

        # higher value is cpu intensive, but can lead to better layouts
        maxIterations: 1000

        # can also influence edge length and layout
        minDistanceLimitFactor: 3.05