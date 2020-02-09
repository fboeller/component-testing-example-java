import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import java.util.UUID;

public class Main {

    public static void main(String[] args) {

    }

    public static Jdbi configureJdbi(Jdbi jdbi) {
        jdbi.installPlugin(new SqlObjectPlugin());
        jdbi.registerRowMapper(Run.class, (rs, ctx) -> new Run(
                rs.getObject("id", UUID.class),
                rs.getString("name"),
                rs.getString("status")
        ));
        jdbi.registerRowMapper(Warning.class, (rs, ctx) -> new Warning(
                rs.getObject("run_id", UUID.class),
                rs.getString("message")
        ));
        return jdbi;
    }
}
