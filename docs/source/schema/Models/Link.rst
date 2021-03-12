Link
---------------

A link to an external resource. Contains a href (URL) plus various attributes for authentication and/or hateoas.

Properties
==========

.. list-table::
   :header-rows: 1

   * - Name
     - Type
     - Description
     - Remarks
     - Example

   * - rel
     - String(string.rst)
     - hateoas relation type
     - optional, defaults to null
     - null
   * - href
     - String(string.rst)
     - The link target.
     - **required**, defaults to null
     - null
   * - hreflang
     - String(string.rst)
     - hateoas language
     - optional, defaults to null
     - null
   * - media
     - String(string.rst)
     - hateoas media type
     - optional, defaults to null
     - null
   * - title
     - String(string.rst)
     - hateoas title
     - optional, defaults to null
     - null
   * - type
     - String(string.rst)
     - 
     - optional, defaults to null
     - null
   * - deprecation
     - String(string.rst)
     - deprecation info (typically used in OpenAPI specs)
     - optional, defaults to null
     - null
   * - name
     - String(string.rst)
     - HateOAS / OpenAPI name
     - optional, defaults to null
     - null
   * - basicAuthUsername
     - String(string.rst)
     - 
     - optional, defaults to null
     - null
   * - basicAuthPassword
     - String(string.rst)
     - 
     - optional, defaults to null
     - null
   * - headerTokenName
     - String(string.rst)
     - 
     - optional, defaults to null
     - null
   * - headerTokenValue
     - String(string.rst)
     - 
     - optional, defaults to null
     - null

