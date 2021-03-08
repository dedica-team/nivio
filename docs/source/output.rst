Output
======

HTTP API
--------

The API offers three types of output:

* JSON under /api
* rendered maps under /render
* generated landscape documentation under /docs
* search under /api/{landscape}/search/{lucene style query}

Please see the swagger documentation under /v2/api-docs (JSON) or /swagger-ui.html (HTML GUI) for details.

Searching
---------

Nivio indexes all landscape items in an in-memory search engine called Lucene. You can build sophisticated queries on
various item fields (see :ref:`Model and Syntax`). For further information see https://www.lucenetutorial.com/lucene-query-syntax.html


Modifying Item Appearance
-------------------------


Icons by Type
^^^^^^^^^^^^^

The icon of an item is determined by its item type (e.g. server, database, ...) and defaults to a cog () .

.. code-block:: yaml
   :linenos:

    items:
      - identifier: bar
        type: database

As type values all items from https://materialdesignicons.com/ can be chosen. Just add the icon name without the "SVG" suffix,
like "account".

.. code-block:: yaml
   :linenos:

    items:
      - identifier: bar
        type: account

The following types are translated icons to maintain backward compatibility:

* CACHE -> "flash-circle"
* CONTAINER -> "inbox"
* FIREWALL -> "wall"
* HUMANUSER -> "account"
* INTERFACE -> "connection"
* KEYVALUESTORE -> "keyvaluestore"
* LOADBALANCER -> "loadbalancer"
* MESSAGEQUEUE -> "tray-full"
* MOBILECLIENT -> "cellphone"
* VOLUME -> "harddisk"
* WEBSERVICE -> "application"

Vendor Logos
^^^^^^^^^^^^^
The *icon* property can also work with a predefined vendor name, like "redis", prefixed with "vendor://" as scheme.

Vendor icons are work in progress.

.. code-block:: yaml
   :linenos:

    items:
      - identifier: bar
        icon: vendor://redis

To change the appearance of an item to a vendor logo the *icon* or *fill* property can be set. Both properties take
a valid URL.

External Images
^^^^^^^^^^^^^^^

To include external images in the map, just set the icon (or fill, see :ref:`Background fill`) property to a valid URL.

.. code-block:: yaml
   :linenos:

   items:
      - identifier: foo
        icon: http://my.custom/icon.png


Background fill
^^^^^^^^^^^^^^^

While icon (see :ref:`External Images`) is rendered as centered image on the node, fill is used to paint the entire
background and is more suitable to be used with images, photos, and so on.

.. code-block:: yaml
   :linenos:

   items:
      - identifier: bar
        fill: http://my.custom/background.png

UTF-8 Symbols and shortname as Icons
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

If **NO** icon, type, or fill value is set, but a shortname value is given, the value is displayed on the icon. The first
example would display "FOOBAR" on the item and the second an enlarged unicorn symbol (shortnames less than three characters are
enlarged).

.. code-block:: yaml
   :linenos:

   items:
      - identifier: bar
        shortname: FOOBAR
      - identifier: pony
        shortname: 🦄


Custom(er) Branding
===================

The appearance of rendered maps can be altered to match corporate identities. When an SVG map is created, Nivio tries to
load and include custom CSS from a URL which can be configured in the landscape configuration. Furthermore, a logo can be
included. A logo is configured in the landscape config and must be a URL pointing to an includable file.

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

You can also apply custom colors to the user interface. Set the following environment variables to hex values (e.g. "#23423"):

* NIVIO_BRANDING_FOREGROUND to set the primary color for interface elements
* NIVIO_BRANDING_BACKGROUND for the background color (dark grey is default)
* NIVIO_BRANDING_SECONDARY to set the accent color used for active elements

Graph Layout Tweaking
=====================

In rare cases the layout needs some manual improvements. Internally Nivio uses a forced directed layout, which can be
influenced by tweaking some parameters (although mxgraph is not used anymore, for further explanation see https://jgraph.github.io/mxgraph/java/docs/com/mxgraph/layout/mxFastOrganicLayout.html).

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

        # higher value is CPU intensive, but can lead to better layouts
        maxIterations: 1000

        # can also influence edge length and layout
        minDistanceLimitFactor: 3.05

        # multiplies the max distance limit
        maxDistanceLimitFactor: 2

      itemLayoutConfig:

        # the higher, the longer the edges between groups
        forceConstantFactor: 2.8

        # higher value is CPU intensive, but can lead to better layouts
        maxIterations: 1000

        # can also influence edge length and layout
        minDistanceLimitFactor: 3.05

        # multiplies the max distance limit
        maxDistanceLimitFactor: 2
