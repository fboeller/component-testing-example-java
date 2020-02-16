import com.codahale.metrics.health.HealthCheck;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class App extends Application<AppConfiguration> {

    private static final int DB_PORT = 4202;
    private static final int EXTERNAL_SERVICE_PORT = 4203;

    private static final HttpUrl externalServiceUrl = new HttpUrl.Builder()
            .scheme("http").host("localhost").port(EXTERNAL_SERVICE_PORT)
            .build();
    private static final String databaseUrl = "jdbc:postgresql://localhost:" + DB_PORT + "/postgres";

    public static void main(String[] args) throws Exception {
        new App().run("server", "config.yml");
    }

    @Override
    public void run(AppConfiguration configuration, Environment environment) throws Exception {
        var jdbi = Database.initDatabase(databaseUrl);
        var runService = new RunService(externalServiceUrl, new OkHttpClient());
        var runResource = new RunResource(jdbi, runService);
        environment.jersey().register(runResource);
        registerHealthChecks(environment);
    }

    private static void registerHealthChecks(Environment environment) {
        environment.healthChecks().register("dummy", new HealthCheck() {
            @Override
            protected Result check() {
                return Result.healthy();
            }
        });
    }
}
