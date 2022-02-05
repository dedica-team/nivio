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

   * - \_links
     - Map<Link>
     - Key-value pairs of related links. Some keys like &#39;github&#39; cause that the endpoint data is parsed and added to to corresponding landscape component.
     - optional, defaults to null
     - github: https://github.com/dedica-team/nivio
   * - address
     - String
     - The technical address of the item (should be an URI). Taken into account when matching relation endpoints.
     - optional, defaults to null
     - null
   * - color
     - String
     - The HTML (hexcode only!) color used to draw the group and its items. If no color is given, one is computed.
     - optional, defaults to null
     - 05ffaa
   * - contact
     - String
     - A contact method, preferably email.
     - optional, defaults to null
     - null
   * - contains
     - List
     - A list of item identifiers or SQL-like queries to easily assign items to this group.
     - optional, defaults to null
     - identifier LIKE 'DB1'
   * - description
     - String
     - A brief description.
     - optional, defaults to null
     - null
   * - frameworks
     - Map
     - The parts used to create the item. Usually refers to technical frameworks.
     - optional, defaults to null
     - java: 8
   * - fullyQualifiedIdentifier
     - URI<URI>
     - 
     - optional, defaults to null
     - null
   * - group
     - String
     - The identifier of the group this item belongs in. Every item requires to be member of a group internally, so if nothing is given, the value is set to its layer.
     - optional, defaults to null
     - shipping
   * - identifier
     - String
     - A unique identifier for the group (also used as name). Descriptions are merged based on the identifier.
     - **required**, defaults to null
     - shipping
   * - interfaces
     - Set<InterfaceDescription>
     - A collection of low level interfaces. Can be used to describe HTTP API endpoints for instance.
     - optional, defaults to null
     - null
   * - labels
     - Map
     - 
     - optional, defaults to null
     - null
   * - layer
     - String
     - The technical layer
     - optional, defaults to null
     - infrastructure
   * - lifecycle
     - String
     - The lifecycle state of an item.
     - optional, defaults to null
     - null
   * - links
     - Map<Link>
     - Key-value pairs of related links. Some keys like &#39;github&#39; cause that the endpoint data is parsed and added to to corresponding landscape component.
     - optional, defaults to null
     - github: https://github.com/dedica-team/nivio
   * - name
     - String
     - A human-readable name
     - **required**, defaults to null
     - null
   * - owner
     - String
     - The business owner of the group.
     - optional, defaults to null
     - null
   * - parentIdentifier
     - String
     - 
     - optional, defaults to null
     - null
   * - providedBy
     - List
     - A collection of identifiers which are providers for this item (i.e. hard dependencies that are required). This is a convenience field to build relations.
     - optional, defaults to null
     - shipping-mysqldb
   * - status
     - List<map>
     - A list of statuses that works like hardcoded KPIs.
     - optional, defaults to null
     - null
   * - statuses
     - List<map>
     - A list of statuses that works like hardcoded KPIs.
     - optional, defaults to null
     - null
   * - tags
     - List
     - 
     - optional, defaults to null
     - null
   * - type
     - String
     - The type of the component. A string describing its nature. If no icon is set, the type determines the displayed icon.
     - optional, defaults to null
     - null

