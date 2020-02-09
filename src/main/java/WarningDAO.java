import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.UUID;

public interface WarningDAO {

    @SqlUpdate("INSERT INTO warning (run_id, message) VALUES (?, ?)")
    void createWarning(UUID runId, String message);

    @SqlQuery("SELECT * FROM warning WHERE run_id = ?")
    List<Warning> selectWarnings(UUID runId);
}
