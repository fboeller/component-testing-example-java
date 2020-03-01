import com.codahale.metrics.health.HealthCheck;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import retrofit2.Retrofit;

public class App extends Application<App.AppConfiguration> {

    public static class AppConfiguration extends Configuration {
    }

    private static final String databaseUrl = "jdbc:postgresql://localhost:4202/postgres";
    private static final String externalServiceUrl = "http://localhost:4203";

    public static void main(String[] args) throws Exception {
        new App().run("server", "config.yml");
    }

    @Override
    public void run(AppConfiguration configuration, Environment environment) throws Exception {
        var jdbi = Database.initDatabase(databaseUrl, "postgres", "secret");
        var externalService = ExternalService.create(externalServiceUrl);
        var runService = new RunService(externalService);
        var runResource = new RunResource(jdbi, runService);
        environment.jersey().register(runResource);
        environment.healthChecks().register("dummy", new HealthCheck() {
            @Override
            protected Result check() {
                return Result.healthy();
            }
        });
    }
}
