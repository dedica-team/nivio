Link
---------------

A link to an external resource. Contains a href (URL) plus various attributes for authentication and/or hateoas.


.. list-table::
   :header-rows: 1

   * - Name
     - Type
     - Description
     - Remarks
     - Example

   * - rel
     - String
     - hateoas relation type
     - optional, defaults to null
     - null
   * - href
     - String
     - The link target.
     - **required**, defaults to null
     - null
   * - hreflang
     - String
     - hateoas language
     - optional, defaults to null
     - null
   * - media
     - String
     - hateoas media type
     - optional, defaults to null
     - null
   * - title
     - String
     - hateoas title
     - optional, defaults to null
     - null
   * - type
     - String
     - 
     - optional, defaults to null
     - null
   * - deprecation
     - String
     - deprecation info (typically used in OpenAPI specs)
     - optional, defaults to null
     - null
   * - name
     - String
     - HateOAS / OpenAPI name
     - optional, defaults to null
     - null
   * - basicAuthUsername
     - String
     - 
     - optional, defaults to null
     - null
   * - basicAuthPassword
     - String
     - 
     - optional, defaults to null
     - null
   * - headerTokenName
     - String
     - 
     - optional, defaults to null
     - null
   * - headerTokenValue
     - String
     - 
     - optional, defaults to null
     - null

