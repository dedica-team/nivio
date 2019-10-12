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
