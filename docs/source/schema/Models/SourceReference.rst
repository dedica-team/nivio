SourceReference
---------------

This is a reference to a configuration file.


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
   * - url
     - String
     - A URL, but can also be a relative path.
     - optional, defaults to null
     - ./a/items.yaml
   * - format
     - String
     - The input format.
     - optional, defaults to null
     - null
   * - assignTemplates
     - Map<array>
     - A map with template identifier as key and item identifier matchers as value
     - optional, defaults to null
     - endOfLife: [web, "java6*"]

