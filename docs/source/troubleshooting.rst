Troubleshooting
===============


Behind a proxy
--------------

If you deploy Nivio to run under a different path than root (:file:`/`), make sure to set the environment variables
:envvar:`SERVER_SERVLET_CONTEXT_PATH` and :envvar:`NIVIO_BASE_URL` to the path.

.. code-block:: bash

   SERVER_SERVLET_CONTEXT_PATH: /my-landscape
   NIVIO_BASE_URL: https://foo.com/my-landscape/


Graph Layout Tweaking
---------------------

In rare cases the layout needs some manual improvements. Internally Nivio uses a forced directed layout, which can be
influenced by tweaking some parameters (although mxgraph is not used anymore, for further explanation see https://jgraph.github.io/mxgraph/java/docs/com/mxgraph/layout/mxFastOrganicLayout.html). In order to change the default setting of the  :ref:`LayoutConfig`, add a section to the landscape description as follows:

.. code-block:: yaml
   :linenos:

    identifier: nivio:example
    name: Landscape example
    config:
      layoutConfig:
        itemMinDistanceLimit: 60
        itemMaxDistanceLimit: 360
        groupMinDistanceLimit: 140
        groupMaxDistanceLimit: 300
        itemLayoutInitialTemp: 380
        groupLayoutInitialTemp: 1000

