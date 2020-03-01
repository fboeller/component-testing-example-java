import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Testcontainers
public class RunResourceTest {

    @Container
    private static PostgreSQLContainer container = new PostgreSQLContainer<>("postgres:11.5");

    private static Jdbi jdbi;
    private static RunResource runResource;

    @BeforeAll
    public static void beforeAll() throws Exception {
        jdbi = Database.initDatabase(container.getJdbcUrl(), container.getUsername(), container.getPassword());
        var runService = mock(RunService.class);
        when(runService.executeRun(anyInt())).thenReturn(true);
        runResource = new RunResource(jdbi, runService);
    }

    @AfterEach
    public void cleanDatabase() {
        jdbi.useHandle(handle -> handle.execute("TRUNCATE run"));
    }

    @Test
    @DisplayName("Empty list is returned when asked for runs without having existing runs")
    public void t1() {
        assertThat(runResource.getRuns())
                .isEmpty();
    }
}
