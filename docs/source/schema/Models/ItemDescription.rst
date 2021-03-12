ItemDescription
---------------
Properties
==========


*  **labels** | [**Map**](string.rst) | Key-value pair labels for an item. | [optional] [default to null]
*  **identifier** | [**String**](string.rst) | Immutable unique identifier (maybe use an URN). Primary means to identify items in searches. | [default to null]
*  **name** | [**String**](string.rst) | A human readable name/title. The name is considered when items are searched | [optional] [default to null]
*  **owner** | [**String**](string.rst) | The business owner of the item. Preferably use an email address. | [optional] [default to null]
*  **description** | [**String**](string.rst) | A brief description | [optional] [default to null]
*  **contact** | [**String**](string.rst) | The primary way to contact a responsible person or team . Preferably use an email address. | [optional] [default to null]
*  **group** | [**String**](string.rst) | The identifier of the group this item belongs in. Every requires a group internally, so if nothing is given, the value is set to &#39;common&#39; | [optional] [default to null]
*  **interfaces** | [**Set**](InterfaceDescription.rst) | A collection of low level interfaces. Can be used to describe Http API endpoints for instance. | [optional] [default to null]
*  **providedBy** | [**List**](string.rst) | A collection of identifiers which are providers for this item (i.e. hard dependencies that are required). This is a convenience fields to build relations. | [optional] [default to null]
*  **icon** | [**String**](string.rst) | An icon name or url to set the displayed map icon. The default icon set is https://materialdesignicons.com/ and all names can be used (aliases do not work). | [optional] [default to null]
*  **color** | [**String**](string.rst) | Overrides the group color. Use a HTML hex color code without leading hash. | [optional] [default to null]
*  **address** | [**String**](string.rst) | The technical address of the item (should be an URI). Taken into account when matching relation endpoints. | [optional] [default to null]
*  **type** | [**String**](string.rst) | The type of the item. A string describing its nature. If no icon is set, the type determines the displayed icon. | [optional] [default to null]
*  **fullyQualifiedIdentifier** | [**String**](string.rst) |  | [optional] [default to null]
*  **status** | [**List**](map.rst) |  | [optional] [default to null]
*  **statuses** | [**List**](map.rst) | A list of statuses that works like hardcoded KPIs. | [optional] [default to null]
*  **lifecycle** | [**String**](string.rst) | The lifecycle state of an item. | [optional] [default to null]
*  **tags** | [**List**](string.rst) |  | [optional] [default to null]
*  **links** | [**Map**](Link.rst) | Key-value pairs of related links. Some keys like &#39;github&#39; cause that the endpoint data is parsed and added to to corresponding landscape component. | [optional] [default to null]

