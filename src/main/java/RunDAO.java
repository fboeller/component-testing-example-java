import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.UUID;

public interface RunDAO {

    @SqlUpdate("INSERT INTO run (status) VALUES ('RUNNING')")
    @GetGeneratedKeys
    UUID createRun();

    @SqlQuery("SELECT * FROM run")
    List<Run> selectRuns();

}
