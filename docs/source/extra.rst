Service Icons
=============

The default icon of a item is determined by its item type (e.g. server, messagequeue, database...). To change the
appearance of a item to a vendor logo, for instance, the *icon* property can be set. This property takes a valid
URL.

.. code-block:: yaml
   :linenos:

   items:
      - identifier: foo
        shortName: blog1
        icon: http://my.custom/icon.png


Vendor Icons
------------

The *icon* property can also work with a predefined vendor name like "redis", prefixed with "vendor://" as scheme.

Vendor icons are work in progress.

.. code-block:: yaml
   :linenos:

    items:
      - identifier: bar
        icon: vendor://redis


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