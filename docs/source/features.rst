Features
========

.. image:: https://raw.githubusercontent.com/bonndan/nivio/master/docs/graph.png
   :width: 100%
   :alt: Rendered graph

* **It is easy to install and to maintain.** Runs dockerized on a single server with moderate to low hardware requirements. It uses a H2 database, but it can be discarded at any time and will be refilled with the next start.
* **No-op usage** Besides its initial configuration it is designed to gather the application landscape information from configurable items, preferably code repos.
* **Renders the landscapes as a graph**
* **Generates a report** see [here](https://raw.githubusercontent.com/bonndan/nivio/master/docs/Landscape_example.png)
* **Multiple configuration sources** while nivio has its proprietary format, you can also use docker-compose files or use them as basis and enrich them using further files
* **PULL: Single seed** basic indexing of landscapes driven by a configuration file
* **PUSH: incremental data aggregation** send configuration files and other formats to build a landscape
* **Aggregation of item state** using sources like Prometheus and marking items accordingly
