Feature: Load customers via admin endpoint

  Scenario: Return 406 if customer file does not exists
    Then Return 406 when requesting to load customers from does-not-exist.csv

  Scenario: Return 200 when customer file is loaded successfully
    Then Return 200 when requesting to load customers from customer-single.csv

  Scenario: Return 500 when customer file is malformed
    Then Return 500 when requesting to load customers from customer-malformed.csv