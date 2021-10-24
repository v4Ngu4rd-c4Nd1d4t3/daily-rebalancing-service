Feature: Retrying failed batches

  Scenario: Retry only failed batches
    When Financial Portfolio Service returns portfolios
      | 1 | 6700 | 1200 | 400 |
      | 3 | 1337 | 1337 | 1337 |
    And Financial Portfolio Service accepts all trades
    Then Return 200 when requesting to load customers from customer-many.csv
    And Return 200 when requesting to load strategies from strategy.csv
    Then Wait 5 seconds
    Then Financial Portfolio Service received trades
      | 3 | -1337 | -1337 | 2674 |
    When Financial Portfolio Service returns portfolios
      | 1 | 6700 | 1200 | 400 |
      | 2 | 1337 | 1337 | 1337 |
    Then Wait 5 seconds
    Then Financial Portfolio Service received trades
      | 1 | -6700 | -1200 | 7900 |
      | 2 | -936 | 2272 | -1337 |