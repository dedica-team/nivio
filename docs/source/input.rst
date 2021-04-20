Input Sources
=============


Kubernetes cluster inspection
-----------------------------

Kubernetes clusters are inspected using Fabric8.io's Java client. See https://github.com/fabric8io/kubernetes-client#configuring-the-client
for configuration. Parsing can be configured via a URL, i.e. the examined namespace can be given (otherwise all namespaces
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
from environment variables. To exclude internal stacks, like those responsible for internal networking, blacklist them.

.. code-block:: yaml
   :linenos:

    identifier: rancher:example
    name: Rancher 1.6 API example
    config:
      groupBlacklist: [".*infra.*"]

    sources:
      - url: "http://rancher-server/v2-beta/"
        projectName: Default
        apiAccessKey: ${API_ACCESS_KEY}
        apiSecretKey: ${API_SECRET_KEY}
        format: rancher1



Nivio proprietary format
------------------------

Nivio provides its own format which allows to set all model properties manually (see :ref:`Model and Syntax` section).

Reading from CSV
-----------------------------

Nivio can parse CSV files regarding rows as landscape items. The order of the columns in the file is important because
headers are ignored and not mapped automatically. Instead, each column number, starting at zero, can be assigned to an
item property in the ``mapping`` configuration. Additionally, the CSV separator char and the number of lines to
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


External data
-------------

Nivio can load external data that cannot be used directly to build landscapes, but is still valuable. For example, the
number of GitHub issues might be interesting to see on a landscape item that is an open source component. To attach such
data to landscape components, use links having special known identifiers like "*github*" or "*sonar*".

This is work in progress. Currently supported link identifiers are:

* ``github`` for GitHub repositories
* ``gitlab`` for GitLab repositories
* ``spring.health`` for Spring Boot health actuators https://docs.spring.io/spring-boot/docs/current/actuator-api/htmlsingle/#health

.. code-block:: yaml
   :linenos:

    items:
      - identifier: nivio
        links:
          github: https://github.com/dedica-team/nivio
          spring.health: http://localhost:8090/actuator/health
          # sonar: http://hihi.huhu not implemented yet

