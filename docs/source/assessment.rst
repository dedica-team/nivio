Data Assessment using KPIs
==========================

:abbr:`KPIs (Key Performance Indicators)` can be used to evaluate landscape components (typically items, but also groups) based on
their properties. The result is a status represented by colors (ordinal):

* UNKNOWN (order 0): status could not be determined
* GREEN (order 1): everything OK
* YELLOW (order 2): ignorable warning
* ORANGE (order 3): warning
* RED (order 4): error
* BROWN (order 5): fubar


Built in KPIs
-------------

Scaling
^^^^^^^
This KPI evaluates the scale label and tries to find bottlenecks where providers for many items are down or not scaled.

* red if 0 as provider for other items
* yellow if scaled to 0 without relations
* orange of scaled to 0 as data sink
* unknown if no label or not a number
* green if scaled higher than 1
* yellow if a bottleneck (more than 1 item depends on it)

Lifecycle
^^^^^^^^^
This KPI evaluates the lifecycle label for "official" values.

* PRODUCTION turns the KPI value to GREEN
* END_OF_LIFE turns it to ORANGE

Other
^^^^^

* health (examines the health label on items)
* condition (K8s condition true/false evaluation)

By default all shipped `KPIs (Key Performance Indicators)` are disabled. Set ``enabled`` to true in the config to disable them.

.. code-block:: yaml
   :linenos:

    identifier: kpi_example

    config:
      kpis:
        scaling:
          enabled: true


Custom KPIs
-----------

Custom KPIs can be configured in the landscape config using ranges and/or matchers (regular expressions) and applied to everything having labels.
In the example below a KPI ``monthlyCosts`` is defined, using ranges on the label ``costs``, and the KPI ``myEval`` evaluates a
label ``foo``.

* Both ranges (inclusive lower and upper limits) and matchers are separated by semicolon.
* The displayed message can be customized by a template. The placeholder for the value is '%s'.

.. code-block:: yaml
   :linenos:

    identifier: kpi_example
    name: Using KPIs for data assessment

    config:
      kpis:
        monthlyCosts:
          description: Evaluates the monthly maintenance costs
          label: costs
          messageTemplate: "Monthly costs: $%s"
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
