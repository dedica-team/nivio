# Nivio

[![Build Status](https://api.travis-ci.org/bonndan/nivio.svg?branch=master)](https://travis-ci.org/bonndan/nivio)


Nivio is a tool for application landscape management targeted at teams (developers, operators, managers). It follows a no-op 
approach, i.e. there is no interface for manual data maintenance. Instead, nivio pulls all its information from data
sources like files or web apis (e.g. monitoring services).

* **It is easy to install and to maintain.** Runs dockerized on a single server with moderate to low
hardware requirements. It uses a H2 database, but it can be discarded at any time and will be refilled with the next start.
* **No-op usage besides initial configuration.** Designed to gather the application landscape information
from configurable services, preferably code repos.



## Features

* **Renders the landscapes as a graph** using DLD4E (decent looking diagrams for engineers)
* **Single seed** basic indexing of landscapes driven by a configuration file
* **Multiple configuration sources** while nivio has its proprietary format, you can also use docker-compose files or use
them as basis and enrich them using further files
* **Rendering of landscapes** to svg, png 
* **Aggregation of service state** using sources like Prometheus and marking services accordingly
* Future feature: Error tracking on application level with an API compatible to Sentry

## Getting Started

Nivio expects a seed configuration at start time. You can either set an environment variable *SEED* to a path to read from,
or you can omit the variable and place environment files in /opt/nivio/environments.

    SEED=/my/files/here docker-compose up
    
then point your browser to http://localhost:8080/render/dld4e/{landscape} where landscape is the identifier of the landscape
to be rendered.
    
### Demo mode

In the demo mode Nivio loads immutable demo data for demonstration purposes. Run

    DEMO=1 docker-compose up
    
then point your browser to http://localhost:8080/render/dld4e/nivio:example
    
## Model and Syntax

### Landscape
A landscape is defined as a collection of services and applications which somehow belong together, be it for technical
or business reasons. For example, a company department might model ALL its applications in production as one landscape and use grouping
or tagging to further separate the applications. A second landscape could be used to model a future layout with a different
infrastructure. Both landscapes could have services in common (like a database, load balancer etc.), so their configuration can be reused.


**Landscape configuration file**

The configuration file contains basic data, references to service descriptions (sources), which can be local paths or URLs.
You can also add state providers which are used to gather live data and thereby provide state for the services. 

    identifier: nivio:example
    name: Landscape example
    contact: mail@acme.org
    sources:
      - "./services/wordpress.yml"
      - url: "./services/dashboard.yml"
        format: nivio
    stateProviders:
      - type: prometheus-exporter
        target: http://prometheus_exporter.url
     
        
### Landscape Items and Layers
A landscape consists of the three layers ingress, services, and infrastructure. Within these layers you can allocate services.

**Service configuration file**

    services:
      - identifier: blog-server
        short_name: blog1
    infrastructure:
      - identifier: mysql
        version: 5.6.0
        
A service can have the following attributes:

* **identifier**: a unique identifier in the landscape. Use a name or an URN
* **name** human readable, displayed name
* **short_name** abbreviation
* **version** any string describing a service version (e.g. 1.2.5)
* **software** optional name of the used software/product
* **owner** owning party (e.g. Marketing)
* **description** a short description
* **team** technical owner
* **contact** support/notification contact (email) may be addressed in case of errors 
* **homepage** url to more information
* **repository** source code repo url
* **group** name of an arbitrary group
* **visibility** whether the service is publicly exposed
* **tags** list of strings used as tag
* **networks** list of network names (can be defined somewhere else)
* **machine** description of the underlying virtual or physical machine
* **scale** number of instances (or other description)
* **host_type** e.g. docker
* **note** any note attached to the service
* **statuses** status evaluation in colors (green, yellow, orange, red)
  * lifecycle
  * security
  * stability
  * business_capability
* **interfaces** an array of provided interfaces
  * description: description
  * format: media type or binary format
* **dataflow** connections to other services
  * description: description
  * target: a service identifier
  * format: media type or binary format
 * **provided_by** array of references to other services (identifiers)


## References and similar projects

Nivio is heavily inspired by [pivio.io](http://pivio.io) and uses similar semantics, but has a different focus.

https://github.com/AOEpeople/vistecture
https://github.com/jgrapht/jgrapht/blob/master/jgrapht-demo/src/main/java/org/jgrapht/demo/GraphMLDemo.java
https://github.com/jgrapht/jgrapht/wiki/HelloWorld
https://stackoverflow.com/questions/51574/good-java-graph-algorithm-library
