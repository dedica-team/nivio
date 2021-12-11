LayoutConfig
---------------

Layout configuration for landscapes with unusual number or ratios of items, groups and relations.


.. list-table::
   :header-rows: 1

   * - Name
     - Type
     - Description
     - Remarks
     - Example

   * - groupLayoutInitialTemp
     - Integer
     - The initial temperature for layouts of groups.
     - optional, defaults to 900
     - 900
   * - groupMaxDistanceLimit
     - Integer
     - A maximum distance between groups up to where forces are applied.
     - optional, defaults to 1000
     - 1000
   * - groupMinDistanceLimit
     - Integer
     - The minimum distance between groups.
     - optional, defaults to 50
     - 50
   * - itemLayoutInitialTemp
     - Integer
     - The initial temperature for layouts of items within groups.
     - optional, defaults to 300
     - 300
   * - itemMaxDistanceLimit
     - Integer
     - A maximum distance between items up to where forces are applied.
     - optional, defaults to 350
     - 350
   * - itemMinDistanceLimit
     - Integer
     - The minimum distance between items.
     - optional, defaults to 100
     - 100

