import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class RunDAOTest {

    @Container
    private static PostgreSQLContainer container = new PostgreSQLContainer("postgres:11.5");

    @Test
    public void t1() {
        assertThat(container.isRunning())
                .isTrue();
    }

}
