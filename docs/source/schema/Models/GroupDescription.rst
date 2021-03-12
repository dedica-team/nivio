GroupDescription
---------------

A group of items. Could be used as bounded context, for instance.


.. list-table::
   :header-rows: 1

   * - Name
     - Type
     - Description
     - Remarks
     - Example

   * - labels
     - Map(string.rst)
     - labels related to the group
     - optional, defaults to null
     - null
   * - identifier
     - String(string.rst)
     - A unique identifier for the group (also used as name). Descriptions are merged based on the identifier
     - **required**, defaults to null
     - shipping
   * - owner
     - String(string.rst)
     - The business owner of the group.
     - optional, defaults to null
     - null
   * - description
     - String(string.rst)
     - A brief description.
     - optional, defaults to null
     - null
   * - contact
     - String(string.rst)
     - A contact method, preferably email.
     - optional, defaults to null
     - null
   * - color
     - String(string.rst)
     - The HTML (hexcode only!) color used to draw the group and its items. If no color is given, one is computed.
     - optional, defaults to null
     - 05ffaa
   * - contains
     - List(string.rst)
     - A list of item identifiers or sql-like queries to easily assign items to this group.
     - optional, defaults to null
     - identifier LIKE 'DB1'
   * - name
     - String(string.rst)
     - 
     - optional, defaults to null
     - null
   * - links
     - Map(Link.rst)
     - Key-value pairs of related links. Some keys like &#39;github&#39; cause that the endpoint data is parsed and added to to corresponding landscape component.
     - optional, defaults to null
     - github: https://github.com/dedica-team/nivio

