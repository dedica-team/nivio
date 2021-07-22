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
     - String
     - Description of the purpose of the KPI
     - optional, defaults to null
     - null
   * - label
     - String
     - Key of the label to evaluate
     - **required**, defaults to null
     - costs
   * - messageTemplate
     - String
     - Template for the displayed message, containing a placeholder for the assessed value &#39;%s
     - optional, defaults to null
     - The current value is: %s
   * - ranges
     - Map
     - A map of number based ranges that determine the resulting status (GREEN|YELLOW|ORANGE|RED|BROWN). Use a semicolon to separate upper and lower bounds. Tries to evaluate label values as numbers.
     - optional, defaults to null
     - GREEN: 0;99.999999
   * - matches
     - Map
     - A map of string based matchers that determine the resulting status (GREEN|YELLOW|ORANGE|RED|BROWN). Use a semicolon to separate matchers.
     - optional, defaults to null
     - RED: BAD;err.*
   * - enabled
     - Boolean
     - A flag indicating that the KPI is active. Can be used to disable default kpis.
     - optional, defaults to null
     - null

