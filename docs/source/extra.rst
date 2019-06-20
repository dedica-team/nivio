Service Icons
=============

The default icon of a service is determined by its service type (e.g. server, messagequeue, database...). To change the
appearance of a service to a vendor logo, for instance, the *icon* property can be set. This property takes a valid
URL.

.. code-block:: yaml
   :linenos:

   services:
      - identifier: foo
        short_name: blog1
        icon: http://my.custom/icon.png


Vendor Icons
------------

The *icon* property can also work with a predefined vendor name like "redis", prefixed with "vendor://" as scheme.

Vendor icons are work in progress.

.. code-block:: yaml
   :linenos:

    services:
      - identifier: bar
        icon: vendor://redis


Icon Caching
------------

All icons not served by the application can be cached (otherwise rendering may take a while). To enable caching, provide
an external caching proxy. Currently only https://github.com/willnorris/imageproxy is supported. Once the proxy is running,
it is important to set the environment variable *IMAGE_CACHE* to the proxy base url.

.. code-block:: bash

   docker run -p 8888:8888 willnorris/imageproxy -addr 0.0.0.0:8888 -cache memory
   IMAGE_CACHE=http://localhost:8888