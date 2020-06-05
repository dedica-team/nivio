Data Assessment using KPIs
==========================

KPIs (Key Performance Indicator) can be used to evaluate landscape components (typically items, but also groups) based on
their properties. The result is a status represented by colors (ordinal):

* UNKNOWN (order 0): status could not be determined
* GREEN (order 1): everything OK
* YELLOW (order 2): ignorable warning
* ORANGE (order 3): warning
* RED (order 4): error
* BROWN (order 5): fubar


Built in KPIs
-------------

* scaling (warning is the scale label is exactly 0)
* health (examines the health label on items)
* condition (K8s condition true/false evaluation)
* custom (see below)

Custom KPIs
-----------

Custom KPIs can be configured in the landscape config using ranges and/or matches and applied to everything having labels.
In the example below a KPI "monthlyCosts" is defined using ranges on the label "costs" and the KPI "myEval" evaluates a
label "foo". Both ranges (inclusive lower and upper limits) and matchers are separated by semicolon.

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
        myEval:
          description: evaluate the label "foo"
          label: foo
          matches:
            GREEN: "OK;good;nice"
            RED: "BAD;err.*"
        health:
          description: can be overridden
