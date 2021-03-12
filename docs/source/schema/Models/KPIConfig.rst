KPIConfig
---------------
Properties
==========


*  **description** | [**String**](string.rst) | Description of the purpose of the KPI | [optional] [default to null]
*  **label** | [**String**](string.rst) | Key of the label to evaluate | [optional] [default to null]
*  **ranges** | [**Map**](string.rst) | A map of number based ranges that determine the resulting status (GREEN|YELLOW|ORANGE|RED|BROWN). Use a semicolon to separate upper and lower bounds. Tries to evaluate label values as numbers. | [optional] [default to null]
*  **matches** | [**Map**](string.rst) | A map of string based matchers that determine the resulting status (GREEN|YELLOW|ORANGE|RED|BROWN). Use a semicolon to separate matchers. | [optional] [default to null]
*  **enabled** | [**Boolean**](boolean.rst) | A flag indicating that the KPI is active. Can be used to disable default kpis. | [optional] [default to null]

