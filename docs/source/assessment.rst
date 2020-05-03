Data Assessment using KPIs
==========================

Built in KPIs
-------------

* scaling
* health

.. code-block:: yaml
   :linenos:

    identifier: kpi_example
    name: Using KPIs for data assessment

    config:
      kpis:
        monthlyCosts:
          description: Evaluates the monthly maintenance costs
          label: costs
          ranges:
            GREEN: 0;99.999999
            YELLOW: 100;199.999999
            RED: 200;499.999999
            BROWN: 500;1000000
        health:
          description: can be overridden
