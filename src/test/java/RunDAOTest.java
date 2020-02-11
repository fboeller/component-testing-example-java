import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PostgresTestcontainerExtension.class)
public class RunDAOTest {

    @RegisterExtension
    static PostgresTestcontainerExtension db = new PostgresTestcontainerExtension();

    @Test
    @DisplayName("No runs are selected when no runs exist")
    void t1() {
        db.getJdbi().useExtension(RunDAO.class, dao ->
            assertThat(dao.selectRuns()).isEmpty()
        );
    }

    @Test
    @DisplayName("A single run is selected when it is inserted")
    void t2() {
        db.getJdbi().useExtension(RunDAO.class, dao -> {
            var id = dao.createRun("myrun");
            assertThat(dao.selectRuns())
                    .containsExactly(new Run(id, "myrun", "RUNNING"));
        });
    }
}
