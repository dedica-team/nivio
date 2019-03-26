Model and Syntax
================

Landscape
---------

A landscape is defined as a collection of services and applications which somehow belong together, be it for technical
or business reasons. For example, a company department might model ALL its applications in production as one landscape and use grouping
or tagging to further separate the applications. A second landscape could be used to model a future layout with a different
infrastructure. Both landscapes could have services in common (like a database, load balancer etc.), so their configuration can be reused.


**Landscape configuration file**

The configuration file contains basic data, references to service descriptions ("sources"), which can be local paths or URLs.
The descriptions can be gathered by http, i.e. it is possible to fetch files from protected sources via authentication headers.
Think of GitLab or GitHub and the related tokens.

You can also add state providers which are used to gather live data and thereby provide state for the services.

.. code-block:: yaml
   :linenos:

    identifier: nivio:example
    name: Landscape example
    contact: mail@acme.org
    sources:
      - "./services/wordpress.yml"
      - url: "./services/dashboard.yml"
        format: nivio
      - url: "http://some.server/docker-compose.yml"
        format: docker-compose-v2
        autoGroup: billing #adds all services without group to "billing"
      - url: https://gitlab.com/api/v4/projects/10972253/repository/files/dashboard.yml/raw?ref=master
        headerTokenName: PRIVATE-TOKEN #gitlab example
        headerTokenValue: ${MY_SECRET_TOKEN_ENV_VAR}
    stateProviders:
      - type: prometheus-exporter
        target: http://prometheus_exporter.url


Landscape Items and Layers
--------------------------

A landscape consists of several groups (think of bounded contexts) and the three layers ingress, services, and infrastructure
for technical separation. Any service can only be part of one group and layer.

**Service configuration file**

.. code-block:: yaml
   :linenos:

    services:
      - identifier: blog-server
        short_name: blog1
        group: content

      - identifier: auth-gateway
        short_name: blog1
        layer: ingress
        group: content

      - identifier: mysql
        version: 5.6.0
        type: database
        layer: infrastructure
        group: content

A service can have the following attributes:

* **identifier**: a unique identifier in the landscape. Use a name or an URN, validated against ^[a-z0-9\\.\\:_-]{3,256}$
* **group** name of the group (optional). If a group is given it becomes part of the global identifier
* **name** human readable, displayed name
* **type** e.g. service, database, proxy, loadbalancer, ...
* **layer** ingress, applications, or infrastructure
* **short_name** abbreviation
* **capability** the capability the service provides for the business, or in case of infrastructure the technical purpose like enabling service discovery, configuration, secrets or persistence.
* **version** any string describing a service version (e.g. 1.2.5)
* **software** optional name of the used software/product
* **owner** owning party (e.g. Marketing)
* **description** a short description
* **team** technical owner
* **contact** support/notification contact (email) may be addressed in case of errors
* **homepage** url to more information
* **repository** source code repo url
* **visibility** whether the service is publicly exposed
* **tags** list of strings used as tag
* **networks** list of network names (can be defined somewhere else)
* **machine** description of the underlying virtual or physical machine
* **scale** number of instances (or other description)
* **host_type** e.g. docker, VM, bare metal
* **note** any note attached to the service
* **costs** running costs of the service. Stored as string
* **statuses** status objects, represented in colors
  * label: lifecycle (etc: stability, capability ....)
  * status: green, yellow, orange, red, brown
  * message: Everything ok.
* **interfaces** an array of provided interfaces or endpoints
  * description: description
  * format: media type or binary format
  * url: an url pointing to the interface
* **dataflow** connections to other services
  * description: description
  * target: a service identifier
  * format: media type or binary format
* **provided_by** array of references to other services (identifiers)

Service identification and referencing
--------------------------------------

A service can be uniquely identified by its landscape, its group and its identifier. A fully qualified
identifier is composed of these three: **mylandscape/agroup/theservice**. Since the group is optional, services with unique
identifier can also be addressed using **mylandscape/theservice** or just **theservice**. Nivio tries to resolve the correct service and raises
an error if it cannot be found or the result is ambiguous.

Service references are required to describe a provider relation or dataflows.

.. code-block:: yaml
   :linenos:

    services:
      - identifier: theservice
        group: agroup
        dataflow:
          - target: anothergroup/anotherservice
            format: json
