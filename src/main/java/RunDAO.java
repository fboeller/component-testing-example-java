import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.UUID;

public interface RunDAO {

    @SqlUpdate("INSERT INTO run (status) VALUES ('RUNNING')")
    @GetGeneratedKeys
    UUID createRun();

    @SqlUpdate("UPDATE run SET status=:status WHERE id=:id")
    @GetGeneratedKeys
    Run changeStatus(@Bind("id") UUID runId, @Bind("status") String status);

    @SqlQuery("SELECT * FROM run")
    List<Run> selectRuns();

}
