Getting Started
===============

The easiest way to get started is run to Nivio using Docker. **Make sure to read about using templates before putting too much effort in item configuration.**


Requirements
------------

Nivio can either be run using Docker or Java 11.

The Docker image is about 350MB and can be started with:

.. code-block:: bash

    docker run -e DEMO=1 bonndan/nivio

Set SERVER_CONTEXT_PATH env var to the path if nivio won't be runner under "/".

Demo mode
---------

In the demo mode Nivio loads sample data for demonstration purposes. Run

.. code-block:: bash

    DEMO=1 docker-compose up

then point your browser to http://localhost:8080/render/nivio:example/graph.png

There is also a demo project "nivio-demo" in the repository, which starts an nginx to simulate a remote server.


Seed config
-----------

Nivio expects a seed configuration at start time. You can either set an landscapeDescription variable *SEED* to a path to read from,
or you can omit the variable and place landscapeDescription files in /opt/nivio/environments.

.. code-block:: bash

    SEED=/my/files/here docker-compose up

then point your browser to http://localhost:8080/render/dld4e/{landscape} where landscape is the identifier of the landscape
to be rendered. The seed can also be an URL.


Landscape configuration
-----------------------

The configuration file contains basic data, references to item descriptions ("sources"), which can be local paths or URLs.
The descriptions can be gathered by http, i.e. it is possible to fetch files from protected sources via authentication headers.
Think of GitLab or GitHub and the related tokens.

You can also add state providers which are used to gather live data and thereby provide state for the items.

To finetune the visual appearance of rendered landscapes, the automatic color choice for groups can be overridden as well.
For jgraphx output, some force directed graph params can be set. More configuration options will be added over time.

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

