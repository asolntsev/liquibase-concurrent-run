# liquibase-concurrent-run
Reproduce LiquiBase with concurrent run on different databases

## Usage
Just run from command line:
> ./gradlew test

## Expected result
Tests should fail with
```java
liquibase.exception.LiquibaseException: java.lang.RuntimeException: Cannot end scope ngzbciavdj when currently at scope ixxybekiqj
	at app//liquibase.Liquibase.runInScope(Liquibase.java:2409)
	at app//liquibase.Liquibase.update(Liquibase.java:211)
	at app//liquibase.Liquibase.update(Liquibase.java:197)
```

## Explanation
This project run 2 PARALLEL tests DatabaseMigrations1Test and DatabaseMigrations2Test.
Each of them runs LiquiBase (but on different databases). 

Expected result: all tests should pass because they run LiquiBase on different databases.
Actual result: tests fail with "Cannot end scope xxx when currently at scope yyy".

Root cause: Class SingletonScopeManager is singleton. You can create many instances of `LiquiBase`, but they all
will still use the same instance of SingletonScopeManager. :(
