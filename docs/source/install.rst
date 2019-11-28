Getting Started
===============

The easiest way to get started is run to Nivio using Docker. **Make sure to read about using templates before putting too much effort in item configuration.**


Requirements
------------

Nivio can either be run using Docker or Java 11.

The Docker image is about 350MB and can be started with:

.. code-block:: bash

    docker run -e DEMO=1 bonndan/nivio

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

    # landscape configuration
    config:
      groupBlacklist: [".*infra.*", "hidden"]
      groups:
        content:
          color: "24a0ed"

      # https://jgraph.github.io/mxgraph/java/docs/com/mxgraph/layout/mxOrganicLayout.html
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

Kubernetes cluster inspection
-----------------------------

Kubernetes clusters are inspected using Fabric8.io's Java client. See https://github.com/fabric8io/kubernetes-client#configuring-the-client
for configuration. Parsing can be configured via an URL, i.e. the examined namespace can be given (otherwise all namespaces
are scanned) and a label for building groups can be named. Both parameters and even the whole URL are optional.

.. code-block:: yaml
   :linenos:

    identifier: k8s:example
    name: Kubernetes example
    sources:
      - url: http://192.168.99.100?namespace=mynamespace&groupLabel=labelToUseForGrouping
        format: kubernetes



Rancher 1 Cluster Inspection
----------------------------

Rancher clusters can be indexed one project (aka environment in the GUI speak) at a time. Access credentials can be read
from environment variables.

.. code-block:: yaml
   :linenos:

    identifier: rancher:example
    name: Rancher 1.6 API example
    sources:
      - url: "http://rancher-server/v2-beta/"
        projectName: Default
        apiAccessKey: ${API_ACCESS_KEY}
        apiSecretKey: ${API_SECRET_KEY}
        format: rancher1


Deleting items
-----------------

Items not referenced anymore in the descriptions will be deleted automatically on a complete and successful re-index run.
If an error occurs fetching the source while indexing, the behaviour of the indexer changes to treat the available data as
 partial input. This means only upserts will happen, and no deletion.

