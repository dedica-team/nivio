Getting Started
===============

The easiest way to get started is run to nivio using Docker (to compile it, you need Java 11).

The Docker image is about 350MB and can be started with:

.. code-block:: bash

    docker run dedica/nivio

Demo mode
---------

.. code-block:: bash

    docker run -e DEMO=1 dedica/nivio

In the demo mode Nivio loads sample data for demonstration purposes.


There is a demo in the directory `./nivio-demo` which starts an nginx to serve all example configs from the project and
starts nivio as docker container.

From the directory, run

.. code-block:: bash

    docker-compose up

then point your browser to http://localhost:8080/


Adding your own content (seed config)
--------------------------------------

**Make sure to read about using templates before putting too much effort in item configuration.**


Nivio expects a seed configuration at start time (unless you want to run the demo mode). You need to set an environment variable
*SEED* to a path or URL nivio can read from. If you want to use files on the host, modify the `docker-compose.yml` to
bind to the corresponding folder, e.g:

.. code-block:: docker

   version: '3.2'

   services:
     nivio:
       image: dedica/nivio:latest
       environment:
         SEED: ${SEED}
         DEMO: ${DEMO}
       volumes:
         - type: bind
           source: /onmyhost/my/files/here
           target: /my/files/here
       ports:
         - 8080:8080

Then you can point to a specific file with the *SEED* environment variable:

.. code-block:: bash

    SEED=/my/files/here/file.yml docker-compose up

Or you provide an URL that serves the yml files to nivio:

.. code-block:: bash

    SEED=https://foo.com/bar.yml java -jar nivio

then point your browser to the GUI at http://localhost:8080 or the API at http://localhost:8080/api/.

Environment variables
---------------------

The following environment variables can be set to configure nivio:

.. include:: inc_env_config.rst


Landscape configuration
-----------------------

The configuration file contains basic data, references to item descriptions ("sources"), which can be local paths or URLs.
The descriptions can be gathered by http, i.e. it is possible to fetch files from protected sources via authentication headers.
Think of GitLab or GitHub and the related tokens.

You can also add state providers which are used to gather live data and thereby provide state for the items.

To finetune the visual appearance of rendered landscapes, the automatic color choice for groups can be overridden as well.

.. code-block:: yaml
   :linenos:

    identifier: nivio:example
    name: Landscape example
    contact: mail@acme.org
    description: This is an example landscape.
    sources:
      - "./items/wordpress.yml"
      - url: "./items/dashboard.yml"
        format: nivio
      - url: "http://some.server/docker-compose.yml"
        format: docker-compose-v2
      - url: https://gitlab.com/bonndan/nivio-private-demo/raw/docker-compose.yml
        headerTokenName: PRIVATE_TOKEN
        headerTokenValue: ${MY_SECRET_TOKEN_ENV_VAR}
      - url: xxx
        format: kubernetes

    config:
      groups:
        content:
          color: "24a0ed"


Deleting items
-----------------

Items not referenced anymore in the descriptions will be deleted automatically on a complete and successful re-index run.
If an error occurs fetching the source while indexing, the behaviour of the indexer changes to treat the available data as
partial input. This means only upserts will happen, and no deletion.



Behind a proxy
--------------

If you deploy nivio to run under a different path than root ("/"), make sure to set the environment variables
SERVER_CONTEXT_PATH and NIVIO_BASEURL to the path.

.. code-block:: bash

   SERVER_SERVLET_CONTEXT_PATH: /my-landscape
   NIVIO_BASEURL: https://foo.com/my-landscape/
