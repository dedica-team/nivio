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
   * - url
     - String(string.rst)
     - A URL, but can also be a relative path
     - optional, defaults to null
     - ./a/items.yaml
   * - format
     - String(string.rst)
     - The input format.
     - optional, defaults to null
     - null
   * - assignTemplates
     - Map(array.rst)
     - A map with template identifier as key and item identifier matchers as value
     - optional, defaults to null
     - endOfLife: [web, "java6*"]

