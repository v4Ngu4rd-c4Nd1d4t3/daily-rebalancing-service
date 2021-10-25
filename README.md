# Be Code challenge rebalancing

## Starting the application

The application can be started via `mvn spring-boot:run`

It will not load customers or strategies during startup, those have to be activated via admin endpoints. To load the example configuration with curl use

```
curl -X PUT -G http://localhost:8080/api/admin/strategies -d file_path=strategy.csv
curl -X PUT -G http://localhost:8080/api/admin/customers -d file_path=customers.csv
```

## What is missing to make this production ready

1. Authentication and Authorization
1. Documentation
1. Test coverage
1. Persistence of customers pending for re-balancing
1. Circuit breakers
1. Better algorithm for strategy selection
