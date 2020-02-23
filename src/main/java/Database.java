import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.util.UUID;

public class Database {

    public static Jdbi initDatabase(String jdbcUrl) throws Exception {
        var dataSource = createPostgresDataSource(jdbcUrl);
        migrateDatabase(dataSource);
        return configureJdbi(Jdbi.create(dataSource));
    }

    private static DataSource createPostgresDataSource(String jdbcUrl) {
        var dataSource = new PGSimpleDataSource();
        dataSource.setUser("postgres");
        dataSource.setPassword("secret");
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
