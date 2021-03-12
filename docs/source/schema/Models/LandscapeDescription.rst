LandscapeDescription
---------------
Properties
==========


*  **identifier** | [**String**](string.rst) | Immutable unique identifier. Maybe use an URN. | [default to null]
*  **name** | [**String**](string.rst) | Human readable name. | [default to null]
*  **contact** | [**String**](string.rst) | Primary contact method, preferably an email address | [optional] [default to null]
*  **description** | [**String**](string.rst) | A brief description of the landscape | [optional] [default to null]
*  **owner** | [**String**](string.rst) | The business owner (person or team), preferably an email address | [optional] [default to null]
*  **templates** | [**Map**](ItemDescription.rst) | Item descriptions to be used as templates. All values excepted identifier and name will be applied to the assigned items. | [optional] [default to null]
*  **sources** | [**List**](SourceReference.rst) |  | [optional] [default to null]
*  **config** | [**LandscapeConfig**](LandscapeConfig.rst) |  | [optional] [default to null]
*  **groups** | [**Map**](GroupDescription.rst) | Description of item groups (optional, can also be given in sources). | [optional] [default to null]
*  **labels** | [**Map**](string.rst) | Additional labels for the landscape. | [optional] [default to null]
*  **items** | [**List**](ItemDescription.rst) | List of configuration sources. Handled in the given order, latter extended/overwrite earlier values like items etc. | [optional] [default to null]
*  **sourceReferences** | [**List**](SourceReference.rst) |  | [optional] [default to null]
*  **icon** | [**String**](string.rst) |  | [optional] [default to null]
*  **color** | [**String**](string.rst) |  | [optional] [default to null]
*  **partial** | [**Boolean**](boolean.rst) | marks that the landscape is not complete, but an update | [optional] [default to null]
*  **links** | [**Map**](Link.rst) | Key-value pairs of related links. Some keys like &#39;github&#39; cause that the endpoint data is parsed and added to to corresponding landscape component. | [optional] [default to null]

