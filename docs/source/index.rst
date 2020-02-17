.. nivio documentation master file, created by
   sphinx-quickstart on Mon Mar 25 21:55:12 2019.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree` directive.

Welcome to Nivio's documentation!
=================================

Nivio is a tool for application landscape management targeted at teams (developers, operators, managers). It follows a no-op
approach, i.e. there is no interface for manual data maintenance. Instead, nivio pulls all its information from data
sources like files or web apis (e.g. monitoring items) or allows pushing information via its API.


.. image:: https://raw.githubusercontent.com/dedica-team/nivio/master/docs/graph.png
   :width: 100%
   :alt: Rendered graph

* **It is easy to install and to maintain.** Runs dockerized on a single server with moderate to low hardware requirements. It store the items, so it can be discarded at any time and be refilled with the next start.
* **No-op usage** Besides its initial configuration it is designed to gather the application landscape information from configurable items, preferably code repos.
* **Renders the landscapes as a graph** see above
* **Multiple configuration sources** while nivio has its proprietary format, you can also use docker-compose files or use them as basis and enrich them using further files
* **PULL: Single seed** basic indexing of landscapes driven by a configuration file
* **PUSH: incremental data aggregation** send configuration files and other formats to build a landscape
* **Aggregation of item state** using sources like Prometheus and marking items accordingly


.. toctree::
   :maxdepth: 2
   :caption: Contents:

   install
   input
   model
   magic
   api
   extra
   references

