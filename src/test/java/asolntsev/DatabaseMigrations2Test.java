package asolntsev;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseMigrations2Test {
  private static final String DEFAULT_DB_NAME = "test2";
  private static final PostgreSQLContainer<?> dbContainer = new PostgreSQLContainer<>("postgres:11.2");

  @BeforeAll
  static void beforeAll() {
    dbContainer.withDatabaseName(DEFAULT_DB_NAME).withReuse(false).start();
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
  void runMigration(int i) throws SQLException, LiquibaseException {
    try (Connection connection = connect()) {
      migrateChanges(connection);
    }
  }

  private static void migrateChanges(Connection connection) throws LiquibaseException {
    Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
    try (Liquibase liquibase = new Liquibase("changelog.xml", new ClassLoaderResourceAccessor(), database)) {
      liquibase.update(new Contexts(), new LabelExpression());
    }
  }

  private static Connection connect() throws SQLException {
    DriverManager.registerDriver(new org.postgresql.Driver());
    return DriverManager.getConnection(dbContainer.getJdbcUrl(), dbContainer.getUsername(), dbContainer.getPassword());
  }
}
