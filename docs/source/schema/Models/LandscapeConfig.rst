LandscapeConfig
---------------

Configuration of key performance indicators (i.e. status indicators) and layouting tweaks

Properties
==========

.. list-table::
   :header-rows: 1

   * - Name
     - Type
     - Description
     - Remarks
     - Example

   * - greedy
     - Boolean(boolean.rst)
     - Flag that enables instant creation items based relation targets that cannot be found in the sources.
     - optional, defaults to null
     - null
   * - groupLayoutConfig
     - LayoutConfig(LayoutConfig.rst)
     - 
     - optional, defaults to null
     - null
   * - itemLayoutConfig
     - LayoutConfig(LayoutConfig.rst)
     - 
     - optional, defaults to null
     - null
   * - groupBlacklist
     - List(string.rst)
     - Names or patterns of groups that should be excluded from the landscape. Used to improve automatic scanning results.
     - optional, defaults to null
     - .*infra.*
   * - labelBlacklist
     - List(string.rst)
     - Names or patterns of labels that should be ignored. Used to improve automatic scanning results.
     - optional, defaults to null
     - .*COMPOSITION.*
   * - branding
     - Branding(Branding.rst)
     - 
     - optional, defaults to null
     - null
   * - kpis
     - Map(KPIConfig.rst)
     - Key performance indicator configs. Each KPI must have a unique identifier.
     - optional, defaults to null
     - null

