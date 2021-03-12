ItemDescription
---------------

List of configuration sources. Handled in the given order, latter extended/overwrite earlier values like items etc.


.. list-table::
   :header-rows: 1

   * - Name
     - Type
     - Description
     - Remarks
     - Example

   * - labels
     - Map(string.rst)
     - Key-value pair labels for an item.
     - optional, defaults to null
     - null
   * - identifier
     - String(string.rst)
     - Immutable unique identifier (maybe use an URN). Primary means to identify items in searches.
     - **required**, defaults to null
     - null
   * - name
     - String(string.rst)
     - A human readable name/title. The name is considered when items are searched
     - optional, defaults to null
     - my beautiful service
   * - owner
     - String(string.rst)
     - The business owner of the item. Preferably use an email address.
     - optional, defaults to null
     - johnson@acme.com
   * - description
     - String(string.rst)
     - A brief description
     - optional, defaults to null
     - null
   * - contact
     - String(string.rst)
     - The primary way to contact a responsible person or team . Preferably use an email address.
     - optional, defaults to null
     - johnson@acme.com
   * - group
     - String(string.rst)
     - The identifier of the group this item belongs in. Every requires a group internally, so if nothing is given, the value is set to &#39;common&#39;
     - optional, defaults to null
     - shipping
   * - interfaces
     - Set(InterfaceDescription.rst)
     - A collection of low level interfaces. Can be used to describe Http API endpoints for instance.
     - optional, defaults to null
     - null
   * - providedBy
     - List(string.rst)
     - A collection of identifiers which are providers for this item (i.e. hard dependencies that are required). This is a convenience fields to build relations.
     - optional, defaults to null
     - shipping-mysqldb
   * - icon
     - String(string.rst)
     - An icon name or url to set the displayed map icon. The default icon set is https://materialdesignicons.com/ and all names can be used (aliases do not work).
     - optional, defaults to null
     - null
   * - color
     - String(string.rst)
     - Overrides the group color. Use a HTML hex color code without leading hash.
     - optional, defaults to null
     - 4400FF
   * - address
     - String(string.rst)
     - The technical address of the item (should be an URI). Taken into account when matching relation endpoints.
     - optional, defaults to null
     - null
   * - type
     - String(string.rst)
     - The type of the item. A string describing its nature. If no icon is set, the type determines the displayed icon.
     - optional, defaults to null
     - service|database|volume
   * - status
     - List(map.rst)
     - 
     - optional, defaults to null
     - null
   * - statuses
     - List(map.rst)
     - A list of statuses that works like hardcoded KPIs.
     - optional, defaults to null
     - null
   * - lifecycle
     - String(string.rst)
     - The lifecycle state of an item.
     - optional, defaults to null
     - null
   * - tags
     - List(string.rst)
     - 
     - optional, defaults to null
     - null
   * - links
     - Map(Link.rst)
     - Key-value pairs of related links. Some keys like &#39;github&#39; cause that the endpoint data is parsed and added to to corresponding landscape component.
     - optional, defaults to null
     - github: https://github.com/dedica-team/nivio

