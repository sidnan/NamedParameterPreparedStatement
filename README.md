# NamedParameterPreparedStatement
Library to prepare and execute a SQL with parameter name instead of param index/position


## Can be 

useful when dealing with a lengthy SQL query with a lot of user passed parameters. 


## Scenario

Say, in a PreparedStatement, the user parameter is denoted as '?' and it is identified using the position (What is the position of the given parameter in the given set of many '?'s, it gets complicated when dealing with index numbers).

```
## Solution

If Hiberate is used, with the help of query builder (createSQLQuery) in org.hibernate.Session, one can mention the placeholder parameter name in the SQL and set the values based on the parameter name.
```


## Problem Statement

If the project does not use Hibernate for some reason like

* Legacy code using JDBC connection, PreparedStatement.

* There could be a case where big data is analyzed & stored in EntepriseDataWarehouse (EDW) and the current Persistence architecture does not allow to add another resource. In that case a new connection is established for the new resource. Say, using Hibernate persistence and Netezza EDW which is based on PostgreSQL. Even if you try using Hibernate PostgreSQLDialect, it does not work. So create an usual JDBC connection and work it. PreparedStatement is used in JDBC Connection; it gets complicated with a lot of passing parameters.


## Proposal

This library has a Class "NamedParameterPreparedStatement" which allows to create SQL with parameter names and handles those parameter names during value mapping and execution. This makes the code understandable, maintainable. This library inturn uses PreparedStatement to execute query.

Want to learn more? [See the wiki](https://github.com/sidnan/NamedParameterPreparedStatement/wiki/wiki)

