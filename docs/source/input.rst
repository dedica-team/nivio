Input Sources
=============


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
from environment variables. To exclude internal stacks (like those responsible for internal networking), blacklist them.

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

Nivio provided an own format, which allows to set all model properties manually (see Model and Syntax section)