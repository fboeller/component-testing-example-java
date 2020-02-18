import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class RunDAOTest {

    @Container
    private static PostgreSQLContainer container = new PostgreSQLContainer<>("postgres:11.5")
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("secret");

    private static Jdbi jdbi;

    @BeforeAll
    public static void setupDatabase() throws Exception {
        jdbi = Database.initDatabase(container.getJdbcUrl());
    }

    @AfterEach
    public void cleanDatabase() {
        jdbi.useHandle(handle -> handle.execute("TRUNCATE run"));
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
