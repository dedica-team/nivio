LandscapeDescription
---------------




.. list-table::
   :header-rows: 1

   * - Name
     - Type
     - Description
     - Remarks
     - Example

   * - assignTemplates
     - Map<array>
     - 
     - optional, defaults to null
     - null
   * - color
     - String
     - The HTML (hexcode only!) color used to draw the group and its items. If no color is given, one is computed.
     - optional, defaults to null
     - 05ffaa
   * - config
     - LandscapeConfig<LandscapeConfig>
     - 
     - optional, defaults to null
     - null
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
   * - contexts
     - Map<ContextDescription>
     - Description of contexts (optional, can also be given in sources).
     - optional, defaults to null
     - null
   * - description
     - String
     - A brief description.
     - optional, defaults to null
     - null
   * - fullyQualifiedIdentifier
     - URI<URI>
     - 
     - optional, defaults to null
     - null
   * - groups
     - Map<GroupDescription>
     - Description of item groups (optional, can also be given in sources).
     - optional, defaults to null
     - null
   * - identifier
     - String
     - A unique identifier for the group (also used as name). Descriptions are merged based on the identifier.
     - **required**, defaults to null
     - shipping
   * - items
     - List<ItemDescription>
     - List of configuration sources. Handled in the given order, latter extend/overwrite earlier values like items etc.
     - optional, defaults to null
     - null
   * - labels
     - Map
     - 
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
   * - partial
     - Boolean
     - marks that the landscape is not complete, but an update
     - optional, defaults to null
     - null
   * - tags
     - List
     - 
     - optional, defaults to null
     - null
   * - templates
     - Map<ItemDescription>
     - Item descriptions to be used as templates. All values except identifier and name will be applied to the assigned items.
     - optional, defaults to null
     - null
   * - type
     - String
     - The type of the component. A string describing its nature. If no icon is set, the type determines the displayed icon.
     - optional, defaults to null
     - null
   * - units
     - Map<UnitDescription>
     - Description of units (optional, can also be given in sources).
     - optional, defaults to null
     - null

