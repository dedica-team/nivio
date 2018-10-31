# Nivio

Nivio is a tool for application landscape management targeted at teams (developers, operators, managers). It follows a no-op 
approach, i.e. there is no interface for manual data maintenance. Instead, nivio pulls all its information from data
sources like files or web apis (e.g. monitoring services).

* **It is easy to install and to maintain.** Runs dockerized on a single server with moderate to low
hardware requirements. It uses a H2 database, but it can be discarded at any time and will be refilled with the next start.
* **No-op usage besides initial configuration.** Designed to gather the application landscape information
from configurable services, preferably code repos.



## Features

* **Renders the landscapes as a graph** using DLD4E (decent looking diagrams for engineers)
* **Single seed** basic indexing of landscape driven by a configuration file
* **Rendering of landscapes** to svg, png 
* **Aggregation of service state** using sources like Prometheus and marking services accordingly
* Future feature: Error tracking on application level with an API compatible to Sentry

## Getting Started

    docker-compose
    
### Demo mode

    DEMO=1 docker-compose up
    
## References and similar projects

Nivio is heavily inspired by [pivio.io](http://pivio.io) and uses similar semantics, but has a different focus.

https://github.com/AOEpeople/vistecture
https://github.com/jgrapht/jgrapht/blob/master/jgrapht-demo/src/main/java/org/jgrapht/demo/GraphMLDemo.java
https://github.com/jgrapht/jgrapht/wiki/HelloWorld
https://stackoverflow.com/questions/51574/good-java-graph-algorithm-library
