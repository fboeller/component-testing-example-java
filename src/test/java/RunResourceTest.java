import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RunResourceTest {

    @RegisterExtension
    public static PostgresTestcontainerExtension db = new PostgresTestcontainerExtension();

    private static RunResource runResource;

    @BeforeAll
    public static void setupDatabase() {
        var runService = mock(RunService.class);
        when(runService.executeRun(anyInt())).thenReturn(true);
        runResource = new RunResource(db.getJdbi(), runService);
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
