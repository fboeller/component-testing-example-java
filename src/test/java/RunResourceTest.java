import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Testcontainers
public class RunResourceTest {

    @Container
    private static PostgreSQLContainer container = new PostgreSQLContainer<>("postgres:11.5")
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("secret");

    private static Jdbi jdbi;
    private static RunResource runResource;

    @BeforeAll
    public static void setupDatabase() throws Exception {
        jdbi = Database.initDatabase(container.getJdbcUrl());
        var runService = mock(RunService.class);
        when(runService.executeRun(anyInt())).thenReturn(true);
        runResource = new RunResource(jdbi, runService);
    }

    @AfterEach
    public void cleanDatabase() {
        jdbi.useHandle(handle -> handle.execute("DELETE FROM run"));
    }

    @Test
    @DisplayName("Empty list is returned when asked for runs without having existing runs")
    public void t1() {
        assertThat(runResource.getRuns())
                .isEqualTo(Collections.emptyList());
    }

    @Test
    @DisplayName("A posted run has the status 'SUCCESS' under normal conditions")
    public void t2() {
        assertThat(runResource.postRun(3))
                .hasFieldOrPropertyWithValue("status", "SUCCESS");
    }


}
