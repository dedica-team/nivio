Getting Started
===============

The easiest way to get started is run to Nivio using Docker. **Make sure to read about using templates before putting too much effort in item configuration.**


Requirements
------------

Nivio can either be run using Docker or Java 11.

The Docker image is about 350MB and can be started with:

.. code-block:: bash

    docker run -e DEMO=1 dedica/nivio

Demo mode
---------

In the demo mode Nivio loads sample data for demonstration purposes. From the `nivio/src/test` directory, run

.. code-block:: bash

    DEMO=1 docker-compose up

then point your browser to http://localhost:8080/

There is also a demo project "nivio-demo" in the repository under `nivio/nivio-demo`, which starts an nginx to simulate a remote server.

Seed config
-----------

Nivio expects a seed configuration at start time (unless you want to run the demo mode). You need to set an environment variable
*SEED* to a path or URL nivio can read from.

.. code-block:: bash

    SEED=/my/files/here docker-compose up

.. code-block:: bash

    SEED=https://foo.com/bar.yml java -jar nivio

then point your browser to the GUI at http://localhost:8080 or the API at http://localhost:8080/api/.

Behind a proxy
--------------

If you deploy nivio to run under a different path than root ("/"), make sure to set the environment variables
SERVER_CONTEXT_PATH and NIVIO_BASEURL to the path.

.. code-block:: bash

   SERVER_SERVLET_CONTEXT_PATH: /my-landscape
   NIVIO_BASEURL: https://foo.com/my-landscape/



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


Reading from csv
-----------------------------

Nivio can parse csv files regarding one row as landscape item. The order of the columns in the file is important, since
headers are ignored and not mapping automatically. Instead, each column number (starting at zero) can be assigned to an
item property in the "mapping" configuration. Additionally, the csv separator char and the number of lines to
skip (usually 1 for the header row) can be set.

.. code-block:: yaml
   :linenos:

    sources:
     - url: "./services/test.csv"
       format: csv
       mapping:
         identifier: 1
         name: 0
         description: 2
         providedBy: 3
       separator: ";"
       skipLines: 1


Deleting items
-----------------

Items not referenced anymore in the descriptions will be deleted automatically on a complete and successful re-index run.
If an error occurs fetching the source while indexing, the behaviour of the indexer changes to treat the available data as
 partial input. This means only upserts will happen, and no deletion.

