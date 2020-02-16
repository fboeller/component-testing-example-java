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
        var container = new PostgreSQLContainer<>("postgres:11.5")
                .withDatabaseName("postgres")
                .withUsername("postgres")
                .withPassword("secret");
        container.start();
        jdbi = Database.initDatabase(container.getJdbcUrl());
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        jdbi.useHandle(handle -> handle.execute("DELETE FROM run"));
    }
}
