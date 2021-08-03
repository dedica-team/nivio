LandscapeDescription
---------------




.. list-table::
   :header-rows: 1

   * - Name
     - Type
     - Description
     - Remarks
     - Example

   * - color
     - String
     - 
     - optional, defaults to null
     - null
   * - config
     - LandscapeConfig<LandscapeConfig>
     - 
     - optional, defaults to null
     - null
   * - contact
     - String
     - Primary contact method, preferably an email address.
     - optional, defaults to null
     - null
   * - description
     - String
     - A brief description of the landscape.
     - optional, defaults to null
     - null
   * - groups
     - Map<GroupDescription>
     - Description of item groups (optional, can also be given in sources).
     - optional, defaults to null
     - null
   * - icon
     - String
     - 
     - optional, defaults to null
     - null
   * - identifier
     - String
     - Immutable unique identifier. Maybe use an URN.
     - **required**, defaults to null
     - null
   * - items
     - List<ItemDescription>
     - List of configuration sources. Handled in the given order, latter extend/overwrite earlier values like items etc.
     - optional, defaults to null
     - null
   * - labels
     - Map
     - Additional labels for the landscape.
     - optional, defaults to null
     - null
   * - links
     - Map<Link>
     - Key-value pairs of related links. Some keys like &#39;github&#39; cause that the endpoint data is parsed and added to to corresponding landscape component.
     - optional, defaults to null
     - github: https://github.com/dedica-team/nivio
   * - name
     - String
     - Human readable name.
     - **required**, defaults to null
     - null
   * - owner
     - String
     - The business owner (person or team), preferably an email address.
     - optional, defaults to null
     - null
   * - partial
     - Boolean
     - marks that the landscape is not complete, but an update
     - optional, defaults to null
     - null
   * - sources
     - List<SourceReference>
     - 
     - optional, defaults to null
     - null
   * - templates
     - Map<ItemDescription>
     - Item descriptions to be used as templates. All values except identifier and name will be applied to the assigned items.
     - optional, defaults to null
     - null

