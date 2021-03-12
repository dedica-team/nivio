KPIConfig
---------------

The configuration of landscape specific key performance indicators that derive status information from landscape components. Usually the KPIs work on labels


.. list-table::
   :header-rows: 1

   * - Name
     - Type
     - Description
     - Remarks
     - Example

   * - description
     - String(string.rst)
     - Description of the purpose of the KPI
     - optional, defaults to null
     - null
   * - label
     - String(string.rst)
     - Key of the label to evaluate
     - optional, defaults to null
     - costs
   * - ranges
     - Map(string.rst)
     - A map of number based ranges that determine the resulting status (GREEN|YELLOW|ORANGE|RED|BROWN). Use a semicolon to separate upper and lower bounds. Tries to evaluate label values as numbers.
     - optional, defaults to null
     - GREEN: 0;99.999999
   * - matches
     - Map(string.rst)
     - A map of string based matchers that determine the resulting status (GREEN|YELLOW|ORANGE|RED|BROWN). Use a semicolon to separate matchers.
     - optional, defaults to null
     - RED: BAD;err.*
   * - enabled
     - Boolean(boolean.rst)
     - A flag indicating that the KPI is active. Can be used to disable default kpis.
     - optional, defaults to null
     - null

