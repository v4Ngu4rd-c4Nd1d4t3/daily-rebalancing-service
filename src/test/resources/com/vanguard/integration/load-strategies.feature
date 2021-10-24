Feature: Load strategies via admin endpoint

  Scenario: Return 406 if strategy file does not exists
    Then Return 406 when requesting to load strategies from does-not-exist.csv

  Scenario: Return 200 when strategy file is loaded successfully
    Then Return 200 when requesting to load strategies from strategy-single.csv

  Scenario: Return 500 when strategy file is malformed
    Then Return 500 when requesting to load strategies from strategy-malformed.csv