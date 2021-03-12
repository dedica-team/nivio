GroupDescription
---------------
Properties
==========


*  **labels** | [**Map**](string.rst) | labels related to the group | [optional] [default to null]
*  **identifier** | [**String**](string.rst) | A unique identifier for the group (also used as name). Descriptions are merged based on the identifier | [default to null]
*  **owner** | [**String**](string.rst) | The business owner of the group. | [optional] [default to null]
*  **description** | [**String**](string.rst) | A brief description. | [optional] [default to null]
*  **contact** | [**String**](string.rst) | A contact method, preferably email. | [optional] [default to null]
*  **color** | [**String**](string.rst) | The HTML (hexcode only!) color used to draw the group and its items. If no color is given, one is computed. | [optional] [default to null]
*  **contains** | [**List**](string.rst) | A list of item identifiers or sql-like queries to easily assign items to this group. | [optional] [default to null]
*  **name** | [**String**](string.rst) |  | [optional] [default to null]
*  **links** | [**Map**](Link.rst) | Key-value pairs of related links. Some keys like &#39;github&#39; cause that the endpoint data is parsed and added to to corresponding landscape component. | [optional] [default to null]

