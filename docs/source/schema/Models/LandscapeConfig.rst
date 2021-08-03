LandscapeConfig
---------------

Configuration of key performance indicators (i.e. status indicators) and layouting tweaks.


.. list-table::
   :header-rows: 1

   * - Name
     - Type
     - Description
     - Remarks
     - Example

   * - branding
     - Branding<Branding>
     - 
     - optional, defaults to null
     - null
   * - greedy
     - Boolean
     - Flag that enables instant creation items based relation targets that cannot be found in the sources.
     - optional, defaults to null
     - null
   * - groupBlacklist
     - List
     - Names or patterns of groups that should be excluded from the landscape. Used to improve automatic scanning results.
     - optional, defaults to null
     - .*infra.*
   * - groupLayoutConfig
     - LayoutConfig<LayoutConfig>
     - 
     - optional, defaults to null
     - null
   * - itemLayoutConfig
     - LayoutConfig<LayoutConfig>
     - 
     - optional, defaults to null
     - null
   * - kpis
     - Map<KPIConfig>
     - Key performance indicator configs. Each KPI must have a unique identifier.
     - optional, defaults to null
     - null
   * - labelBlacklist
     - List
     - Names or patterns of labels that should be ignored. Used to improve automatic scanning results.
     - optional, defaults to null
     - .*COMPOSITION.*

