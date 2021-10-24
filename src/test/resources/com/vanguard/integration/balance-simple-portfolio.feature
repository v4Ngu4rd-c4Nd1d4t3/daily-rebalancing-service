Feature: Balance portfolios

  Scenario: Balance example portfolios
    When Financial Portfolio Service returns portfolios
      | 1 | 6700 | 1200 | 400 |
      | 2 | 1337 | 1337 | 1337 |
    And Financial Portfolio Service accepts all trades
    Then Return 200 when requesting to load customers from customers.csv
    And Return 200 when requesting to load strategies from strategy.csv
    Then Wait 15 seconds
    Then Financial Portfolio Service received trades
      | 1 | -6700 | -1200 | 7900 |
      | 2 | -936 | 2272 | -1337 |