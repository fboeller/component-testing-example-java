import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.util.UUID;

public class Database {

    public static Jdbi initDatabase(String jdbcUrl, String user, String password) throws Exception {
        var dataSource = createPostgresDataSource(jdbcUrl, user, password);
        migrateDatabase(dataSource);
        return configureJdbi(Jdbi.create(dataSource));
    }

    private static DataSource createPostgresDataSource(String jdbcUrl, String user, String password) {
        var dataSource = new PGSimpleDataSource();
        dataSource.setUser(user);
        dataSource.setPassword(password);
        dataSource.setURL(jdbcUrl);
        return dataSource;
    }

    public static void migrateDatabase(DataSource dataSource) throws Exception {
        try (var connection = dataSource.getConnection()) {
            var migrator = new Liquibase(
                    "migrations.xml",
                    new ClassLoaderResourceAccessor(),
                    new JdbcConnection(connection)
            );
            migrator.update("");
        }
    }

    public static Jdbi configureJdbi(Jdbi jdbi) {
        jdbi.installPlugin(new SqlObjectPlugin());
        jdbi.registerRowMapper(Run.class, (rs, ctx) -> new Run(
                rs.getObject("id", UUID.class),
                rs.getString("status")
        ));
        return jdbi;
    }
}
