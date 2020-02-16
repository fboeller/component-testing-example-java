import com.codahale.metrics.health.HealthCheck;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class App extends Application<App.AppConfiguration> {

    public static class AppConfiguration extends Configuration {
    }

    private static final HttpUrl externalServiceUrl = HttpUrl.parse("http://localhost:4203");
    private static final String databaseUrl = "jdbc:postgresql://localhost:4202/postgres";

    public static void main(String[] args) throws Exception {
        new App().run("server", "config.yml");
    }

    @Override
    public void run(AppConfiguration configuration, Environment environment) throws Exception {
        var jdbi = Database.initDatabase(databaseUrl);
        var runService = new RunService(externalServiceUrl, new OkHttpClient());
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