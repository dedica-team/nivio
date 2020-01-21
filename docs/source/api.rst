Api Endpoints
=============

The API is not final and may change in the future.

JSON Endpoints
--------------

* /api/landscape/{landscape.identifier}
* /api/landscape/{landscape.identifier}/items
* /api/landscape/{landscape.identifier}/items/{fully qualified item identifier} DELETE

Document Endpoints
------------------

* /render/{landscape.identifier}/graph.png returns the landscape as rendered graph
* /render/{landscape.identifier}/map.svg returns the landscape as rendered graph
* /docs/{landscape.identifier}/report.html returns a formatted html summary of the landscape
* /docs/{landscape.identifier}/owners.html returns a formatted html summary of the item owners

