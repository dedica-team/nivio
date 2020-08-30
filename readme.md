# Nivio

[![Codefresh build status]( https://g.codefresh.io/api/badges/pipeline/bonndan_marketplace/bonndan%2Fnivio%2Fnivio?branch=master&key=eyJhbGciOiJIUzI1NiJ9.NWJlYTgxZWRhNzdkMDhhODRjODYxZmU2.88EHYpdcpUKruW-DV6OcNQJxl90u4b7dlUCsHlYSlww&type=cf-1)]( https://g.codefresh.io/pipelines/nivio/builds?repoOwner=bonndan&repoName=nivio&serviceName=bonndan%2Fnivio&filter=trigger:build~Build;branch:master;pipeline:5bea8282f75e1713cc9ed5ad~nivio)
[![Documentation Status](https://readthedocs.org/projects/nivio/badge/?version=master)](https://nivio.readthedocs.io/en/master/?badge=master)


 ![layoutedArtifact graph](https://raw.githubusercontent.com/dedica-team/nivio/develop/docs/gui.png)
 
Nivio is application landscape management for teams (developers, operators, managers). It follows a bottom-up no-op 
approach, i.e. there is no interface for manual data maintenance. It is designed to gather the application landscape
 information from configurable items, preferably code repos.

## Get Started: [try nivio at Heroku](https://nivio-demo.herokuapp.com/)

 and read the [documentation](https://nivio.readthedocs.io/en/latest) 
 

## Example Graph, Input and Output

 ![layoutedArtifact graph](https://raw.githubusercontent.com/dedica-team/nivio/develop/docs/graph.png)

![input output_graph](https://raw.githubusercontent.com/dedica-team/nivio/develop/docs/inout.png)


## Copyright & Trademark Acknowledgement Statements

* Redis™ is a trademark of Redis Labs Ltd. Any rights therein are reserved to Redis Labs Ltd.
* Apache Httpd and the logo are trademarks of the ASF.
* WordPress and the logo are trademarks of Wordpress Foundation. http://wordpressfoundation.org/trademark-policy/
* Flame, Danger, Warning, Checked icon made by <a href="https://www.flaticon.com/authors/vectors-market" title="Vectors Market">Vectors Market</a> from <a href="https://www.flaticon.com/" 			    title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" 			    title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a>

## Development Setup

**Requirements**

To run nivio you need the following software installed:
- [nodejs v12.6.2 or higher](https://nodejs.org/en/), [How to update node?](https://www.hostingadvice.com/how-to/update-node-js-latest-version/)
- [Maven](https://maven.apache.org/install.html)
- Java 11

---

**Docker Setup (Linux)**

Clone nivio, build and run a Docker image:

    git clone https://github.com/dedica-team/nivio.git && cd nivio
    mvn clean package
    docker build -t nivio:latest .
    docker run -e SEED=/tmp/nivio/inout.yml --mount type=bind,source="$(pwd)"/src/test/resources/example,target=/tmp/nivio -p 8080:8080 nivio:latest
    
  then open http://localhost:8080
  
**Docker Setup (Windows)**
 
  Clone nivio, build and run a Docker image:
  
      git clone https://github.com/dedica-team/nivio.git && cd nivio
      mvn clean package
      docker build -t nivio:latest .
      docker run -e SEED=/tmp/nivio/inout.yml --mount type=bind,source="C:\<your>\<path>\<to>\nivio\src\test\resources\example",target=/tmp/nivio -p 8080:8080 nivio:latest
      
   then open http://localhost:8080
   
   ---
 
**Development Setup (IntelliJ)**
 
 Create a Spring Boot Configuration in IntelliJ that looks like this:
 
  ![Spring Boot Config](https://raw.githubusercontent.com/dedica-team/nivio/develop/docs/SpringConfig.png)
  
  If you need a clean build you can run 
  
    mvn clean package
  
  You can use your own configuration files, if you add SEED=/path/to/config as an environment variable.
  
  Open http://localhost:8080
  
  If you want to contribute to our frontend, read further into our [Frontend Readme](https://github.com/dedica-team/nivio/tree/develop/src/main/app)