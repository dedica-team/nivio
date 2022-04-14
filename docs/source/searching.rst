Searching
=========

Nivio indexes all landscape items in an search engine called Lucene. You can build sophisticated queries on
various item fields (see :ref:`Model and Syntax`).

Keyword matching
----------------

Search for word "foo" in the ``identifier`` field.

.. code-block:: yaml
   :linenos:

    identifier:foo


Search for phrase "foo bar" in the ``name`` field.

.. code-block:: yaml
   :linenos:

    title:"foo bar"


Search for phrase "foo" in the ``identifier`` field AND the phrase "quick fox" in the ``description`` field.

.. code-block:: yaml
   :linenos:

    title:"foo bar" AND description:"quick fox"


Search for either the phrase "foo bar" in the ``name`` field AND the phrase "quick fox" in the ``description`` field, or the word "fox" in the ``identifier`` field.

.. code-block:: yaml
   :linenos:

    (name:"foo bar" AND description:"quick fox") OR identifier:fox

Search for word "foo" and not "bar" in the ``name`` field.

.. code-block:: yaml
   :linenos:

    name:foo -name:bar


Wildcard matching
-----------------

Search for any word that starts with "foo" in the ``name`` field.

.. code-block:: yaml
   :linenos:

    name:foo*


Search for any word that starts with "foo" and ends with bar in the ``name`` field.

.. code-block:: yaml
   :linenos:

    name:foo*bar

For further information see https://www.lucenetutorial.com/lucene-query-syntax.html


Fields
------

.. include:: inc_searchFields.rst
