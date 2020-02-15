import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.*;
import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class RunDAOTest {

    @Container
    private static PostgreSQLContainer container = new PostgreSQLContainer<>("postgres:11.5")
            .withTmpFs(Map.of("/var/lib/postgresql/data", "rw"));

    private static Jdbi jdbi;

    @BeforeAll
    public static void setupDatabase() throws Exception {
        var dataSource = createPostgresDataSource(container);
        migrateDatabase(dataSource);
        jdbi = Main.configureJdbi(Jdbi.create(dataSource));
    }

    @AfterEach
    public void cleanDatabase() {
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

    @Test
    @DisplayName("No runs are selected when no runs exist")
    void t1() {
        jdbi.useExtension(RunDAO.class, dao ->
            assertThat(dao.selectRuns()).isEmpty()
        );
    }

    @Test
    @DisplayName("A single run is selected when it is inserted")
    void t2() {
        jdbi.useExtension(RunDAO.class, dao -> {
            var id = dao.createRun();
            assertThat(dao.selectRuns())
                    .containsExactly(new Run(id, "RUNNING"));
        });
    }
}
