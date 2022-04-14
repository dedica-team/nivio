<p style="text-align: center"><img src="https://raw.githubusercontent.com/dedica-team/nivio/develop/src/main/resources/static/icons/svg/nivio.svg" width="150" height="150" alt="Logo" style="vertical-align: bottom" /> </p>

# Nivio

[![Codefresh build status]( https://g.codefresh.io/api/badges/pipeline/bonndan_marketplace/bonndan%2Fnivio%2Fnivio?branch=master&key=eyJhbGciOiJIUzI1NiJ9.NWJlYTgxZWRhNzdkMDhhODRjODYxZmU2.88EHYpdcpUKruW-DV6OcNQJxl90u4b7dlUCsHlYSlww&type=cf-1)]( https://g.codefresh.io/pipelines/nivio/builds?repoOwner=bonndan&repoName=nivio&serviceName=bonndan%2Fnivio&filter=trigger:build~Build;branch:master;pipeline:5bea8282f75e1713cc9ed5ad~nivio)
[![Documentation Status](https://readthedocs.org/projects/nivio/badge/?version=master)](https://nivio.readthedocs.io/en/master/?badge=master)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=dedica-team_nivio&metric=alert_status)](https://sonarcloud.io/dashboard?id=dedica-team_nivio)
[![Nivio](https://img.shields.io/endpoint?url=https://dashboard.cypress.io/badge/simple/7dzbuq&style=flat&logo=cypress)](https://dashboard.cypress.io/projects/7dzbuq/runs)
 ![gui](https://user-images.githubusercontent.com/84507051/148235136-7078a8d2-f299-401f-9532-46209d028d3e.png)
 
Nivio is an application landscape management for teams (developers, operators, managers). It follows a bottom-up no-op 
approach, i.e. there is no interface for manual data maintenance. It is designed to gather the application landscape
information from configurable items, preferably code repositories.



## Getting Started

### Online Demo

&rarr; [Try out the Pet Clinic demo](https://nivio-demo.herokuapp.com/)


### Running locally in Docker

You need [docker](https://docker.com) to run the demo:

    docker run -e DEMO=1 -p 8080:8080 dedica/nivio

Point your browser to http://localhost:8080/#/landscape/petclinic to explore the features. An OpenAPI documentation is available at http://localhost:8080/swagger-ui.html.


### Further Reading

* We recommend reading the [User Manual](https://nivio.readthedocs.io/en/latest) for configuration options. 
* Refer to the [developer docs](development.md) if you want to work on the source code.


### Copyright & Trademark Acknowledgment Statements


Nivio uses the [Material Design Icons](https://materialdesignicons.com/) released under the MIT / Apache 2.0 licenses.

The nivio logo has been designed by [Alfred Rehbach](https://alfredrehbach.de)
