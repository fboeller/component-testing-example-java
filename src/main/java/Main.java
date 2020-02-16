import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class Main {

    private static final int APP_PORT = 4201;
    private static final int DB_PORT = 4202;
    private static final int EXTERNAL_SERVICE_PORT = 4203;

    private static final HttpUrl externalServiceUrl = new HttpUrl.Builder()
            .scheme("http").host("localhost").port(EXTERNAL_SERVICE_PORT)
            .build();
    private static final String databaseUrl = "jdbc:postgresql://localhost:" + DB_PORT + "/postgres";

    public static void main(String[] args) throws Exception {
        var vertx = Vertx.vertx();
        var jdbi = Database.initDatabase(databaseUrl);
        var runService = new RunService(externalServiceUrl, new OkHttpClient());
        var runResource = new RunResource(jdbi, runService);
        var router = runResource.configureRouter(Router.router(vertx));
        vertx.createHttpServer()
                .requestHandler(router)
                .listen(APP_PORT);
    }
}
