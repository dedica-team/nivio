LandscapeDescription
---------------



Properties
==========

.. list-table::
   :header-rows: 1

   * - Name
     - Type
     - Description
     - Remarks
     - Example

   * - identifier
     - String(string.rst)
     - Immutable unique identifier. Maybe use an URN.
     - **required**, defaults to null
     - null
   * - name
     - String(string.rst)
     - Human readable name.
     - **required**, defaults to null
     - null
   * - contact
     - String(string.rst)
     - Primary contact method, preferably an email address
     - optional, defaults to null
     - null
   * - description
     - String(string.rst)
     - A brief description of the landscape
     - optional, defaults to null
     - null
   * - owner
     - String(string.rst)
     - The business owner (person or team), preferably an email address
     - optional, defaults to null
     - null
   * - templates
     - Map(ItemDescription.rst)
     - Item descriptions to be used as templates. All values excepted identifier and name will be applied to the assigned items.
     - optional, defaults to null
     - null
   * - sources
     - List(SourceReference.rst)
     - 
     - optional, defaults to null
     - null
   * - config
     - LandscapeConfig(LandscapeConfig.rst)
     - 
     - optional, defaults to null
     - null
   * - groups
     - Map(GroupDescription.rst)
     - Description of item groups (optional, can also be given in sources).
     - optional, defaults to null
     - null
   * - labels
     - Map(string.rst)
     - Additional labels for the landscape.
     - optional, defaults to null
     - null
   * - items
     - List(ItemDescription.rst)
     - List of configuration sources. Handled in the given order, latter extended/overwrite earlier values like items etc.
     - optional, defaults to null
     - null
   * - sourceReferences
     - List(SourceReference.rst)
     - 
     - optional, defaults to null
     - null
   * - color
     - String(string.rst)
     - 
     - optional, defaults to null
     - null
   * - partial
     - Boolean(boolean.rst)
     - marks that the landscape is not complete, but an update
     - optional, defaults to null
     - null
   * - icon
     - String(string.rst)
     - 
     - optional, defaults to null
     - null
   * - links
     - Map(Link.rst)
     - Key-value pairs of related links. Some keys like &#39;github&#39; cause that the endpoint data is parsed and added to to corresponding landscape component.
     - optional, defaults to null
     - github: https://github.com/dedica-team/nivio

