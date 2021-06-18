ItemDescription
---------------

List of configuration sources. Handled in the given order, latter extend/overwrite earlier values like items etc.


.. list-table::
   :header-rows: 1

   * - Name
     - Type
     - Description
     - Remarks
     - Example

   * - labels
     - Map
     - Key-value pair labels for an item.
     - optional, defaults to null
     - null
   * - identifier
     - String
     - Immutable unique identifier (maybe use an URN). Primary means to identify items in searches.
     - **required**, defaults to null
     - null
   * - name
     - String
     - A human readable name/title. The name is considered when items are searched.
     - optional, defaults to null
     - my beautiful service
   * - owner
     - String
     - The business owner of the item. Preferably use an email address.
     - optional, defaults to null
     - johnson@acme.com
   * - description
     - String
     - A brief description.
     - optional, defaults to null
     - null
   * - contact
     - String
     - The primary way to contact a responsible person or team. Preferably use an email address.
     - optional, defaults to null
     - johnson@acme.com
   * - group
     - String
     - The identifier of the group this item belongs in. Every item requires to be member of a group internally, so if nothing is given, the value is set to common.
     - optional, defaults to null
     - shipping
   * - interfaces
     - Set<InterfaceDescription>
     - A collection of low level interfaces. Can be used to describe HTTP API endpoints for instance.
     - optional, defaults to null
     - null
   * - providedBy
     - List
     - A collection of identifiers which are providers for this item (i.e. hard dependencies that are required). This is a convenience field to build relations.
     - optional, defaults to null
     - shipping-mysqldb
   * - icon
     - String
     - An icon name or URL to set the displayed map icon. The default icon set is https://materialdesignicons.com/ and all names can be used (aliases do not work).
     - optional, defaults to null
     - null
   * - color
     - String
     - Overrides the group color. Use an HTML hex color code without the leading hash.
     - optional, defaults to null
     - 4400FF
   * - address
     - String
     - The technical address of the item (should be an URI). Taken into account when matching relation endpoints.
     - optional, defaults to null
     - null
   * - type
     - String
     - The type of the item. A string describing its nature. If no icon is set, the type determines the displayed icon.
     - optional, defaults to null
     - service|database|volume
   * - statuses
     - List<map>
     - A list of statuses that works like hardcoded KPIs.
     - optional, defaults to null
     - null
   * - statuses
     - List<map>
     - A list of statuses that works like hardcoded KPIs.
     - optional, defaults to null
     - null
   * - frameworks
     - Map
     - The parts used to create the item. Usually refers to technical frameworks.
     - optional, defaults to null
     - java: 8
   * - lifecycle
     - String
     - The lifecycle state of an item.
     - optional, defaults to null
     - null
   * - tags
     - List
     - 
     - optional, defaults to null
     - null
   * - links
     - Map<Link>
     - Key-value pairs of related links. Some keys like &#39;github&#39; cause that the endpoint data is parsed and added to to corresponding landscape component.
     - optional, defaults to null
     - github: https://github.com/dedica-team/nivio

