# Nivio

[![Codefresh build status]( https://g.codefresh.io/api/badges/pipeline/bonndan_marketplace/bonndan%2Fnivio%2Fnivio?branch=master&key=eyJhbGciOiJIUzI1NiJ9.NWJlYTgxZWRhNzdkMDhhODRjODYxZmU2.88EHYpdcpUKruW-DV6OcNQJxl90u4b7dlUCsHlYSlww&type=cf-1)]( https://g.codefresh.io/pipelines/nivio/builds?repoOwner=bonndan&repoName=nivio&serviceName=bonndan%2Fnivio&filter=trigger:build~Build;branch:master;pipeline:5bea8282f75e1713cc9ed5ad~nivio)
[![Documentation Status](https://readthedocs.org/projects/nivio/badge/?version=master)](https://nivio.readthedocs.io/en/master/?badge=master)


 ![layoutedArtifact graph](https://raw.githubusercontent.com/dedica-team/nivio/develop/docs/gui.png)
 
Nivio is an application landscape management for teams (developers, operators, managers). It follows a bottom-up no-op 
approach, i.e. there is no interface for manual data maintenance. It is designed to gather the application landscape
 information from configurable items, preferably code repositories.

## Get Started: [try Nivio at Heroku](https://nivio-demo.herokuapp.com/)

 and read the [documentation](https://nivio.readthedocs.io/en/latest) 
 

## Example Graph, Input, and Output

 ![layoutedArtifact graph](https://raw.githubusercontent.com/dedica-team/nivio/develop/docs/graph.png)

![input output_graph](https://raw.githubusercontent.com/dedica-team/nivio/develop/docs/inout.png)


## Copyright & Trademark Acknowledgment Statements

* Redis™ is a trademark of Redis Labs Ltd. Any rights therein are reserved to Redis Labs Ltd.
* Apache HTTP Server and its logo are trademarks of the ASF.
* WordPress and its logo are trademarks of the WordPress Foundation. http://wordpressfoundation.org/trademark-policy/

Nivio uses the [Material Design Icons](https://materialdesignicons.com/) released under the MIT / Apache 2.0 licenses.  

## Development Setup

**Requirements**

To run Nivio, you need the following software installed:
- [nodejs v12.6.2 or higher](https://nodejs.org/en/), [How to update node?](https://www.hostingadvice.com/how-to/update-node-js-latest-version/)
- [Maven](https://maven.apache.org/install.html)
- Java 11

---

**Docker Setup (Linux)**

Clone Nivio, build, and run a Docker image:

    git clone https://github.com/dedica-team/nivio.git && cd nivio
    mvn clean package
    docker build -t nivio:latest .
    docker run -e SEED=/tmp/nivio/inout.yml --mount type=bind,source="$(pwd)"/src/test/resources/example,target=/tmp/nivio -p 8080:8080 nivio:latest
    
  then open http://localhost:8080
  
**Docker Setup (Windows)**
 
  Clone Nivio, build, and run a Docker image:
  
      git clone https://github.com/dedica-team/nivio.git && cd nivio
      mvn clean package
      docker build -t nivio:latest .
      docker run -e SEED=//tmp/nivio/inout.yml --mount type=bind,source="C:\<your>\<path>\<to>\nivio\src\test\resources\example",target=/tmp/nivio -p 8080:8080 nivio:latest
      
   then open http://localhost:8080
   
   (Note, the double slash at the beginning of the path for the SEED environment variable works as a fix to make MSYS/MinGW consoles
   *not* translate the `/tmp` path to a local DOS path. This is safe to use with the PowerShell. Further reading: https://stackoverflow.com/a/14189687/10000398)
   
   ---
 
**Development Setup (IntelliJ)**
 
 Create a Spring Boot configuration in IntelliJ that looks like this:
 
  ![Spring Boot Config](https://github.com/dedica-team/nivio/develop/docs/SpringConfig.png)
  
  If you need a clean build, you can run 
  
    mvn clean package
  
  You can use your own configuration files if you add SEED=/path/to/config as an environment variable.
  
  Open http://localhost:8080
  
  If you want to contribute to our frontend, read further into our [Frontend Readme](https://github.com/dedica-team/nivio/tree/develop/src/main/app/)
  
**Nivio Backend Architecture**

If you want to contribute to our backend, maybe the following diagram is of use to you. It shows some of the most important classes and 
interfaces. It is supposed to give you an idea on how the backend is structured, but note that not all details are displayed:

 ![layoutedArtifact graph](backend_architecture_api.png)
 
If you use `ApiController` as the entry point to the backend, you can see that it retrieves information and triggers events
to the most important parts of the application. 
 
`LandscapeRespository` gives access to the stored landscapes.

`LandscapeDescriptionFactory` is used to generate a `LandscapeDescription` from various sources, such as a `String` input, 
e.g. from a YAML file.

`LandscapeDescription` has to be enriched with `ItemDescription` for all items in the landscape.
This is managed by `InputFormatHandler` which are able to read several input formats, such as e.g. Kubernetes files or the
Nivio description format. Access to these handlers is managed by `InputFormatHandlerFactory`.

To actually create a landscape, `Indexer` is used. The indexer is able to compute the landscape graph from a `LandscapeDescription`.
It also uses several `Resolver` to resolve groups and item relations in landscapes and e.g. the appearance of the graph.

`Indexer` can be triggered either directly through `ApiController` to index or re-index a landscape. Or it is triggered
by an observer mechanism on files. These are the files located under the path provided through the `SEED` environment variable.

