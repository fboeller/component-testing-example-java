import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;

public class PostgresTestcontainerExtension implements BeforeAllCallback, AfterEachCallback {

    private Jdbi jdbi;

    public Jdbi getJdbi() {
        return jdbi;
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        var container = new PostgreSQLContainer<>("postgres:11.5");
        container.start();
        var dataSource = createPostgresDataSource(container);
        migrateDatabase(dataSource);
        jdbi = Main.configureJdbi(Jdbi.create(dataSource));
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        jdbi.useHandle(handle -> handle.execute("DELETE FROM run"));
    }

    private static DataSource createPostgresDataSource(PostgreSQLContainer container) {
        var dataSource = new PGSimpleDataSource();
        dataSource.setDatabaseName(container.getDatabaseName());
        dataSource.setUser(container.getUsername());
        dataSource.setPassword(container.getPassword());
        dataSource.setURL(container.getJdbcUrl());
        return dataSource;
    }

    private static void migrateDatabase(DataSource dataSource) throws Exception {
        try (var connection = dataSource.getConnection()) {
            var migrator = new Liquibase(
                    "migrations.xml",
                    new ClassLoaderResourceAccessor(),
                    new JdbcConnection(connection)
            );
            migrator.update("");
        }
    }
}
