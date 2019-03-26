Getting Started
===============

Requirements
------------

Nivio can either be run using Docker or Java 11.

The Docker image is about 350MB and can be started with:

.. code-block:: bash

    docker run -e DEMO=1 bonndan/nivio:latest

Demo mode
---------

In the demo mode Nivio loads sample data for demonstration purposes. Run

.. code-block:: bash

    DEMO=1 docker-compose up

then point your browser to http://localhost:8080/render/nivio:example/graph.png

There is also a demo project at https://github.com/bonndan/nivio-demo


Seed config
-----------

Nivio expects a seed configuration at start time. You can either set an environment variable *SEED* to a path to read from,
or you can omit the variable and place environment files in /opt/nivio/environments.

.. code-block:: bash

    SEED=/my/files/here docker-compose up

then point your browser to http://localhost:8080/render/dld4e/{landscape} where landscape is the identifier of the landscape
to be rendered.


