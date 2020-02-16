import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.jdbi.v3.core.Jdbi;

import static io.vertx.core.http.HttpMethod.GET;
import static io.vertx.core.http.HttpMethod.POST;

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
        var router = configureRouter(Router.router(vertx), jdbi, runService);
        vertx.createHttpServer()
                .requestHandler(router)
                .listen(APP_PORT);
    }

    private static Router configureRouter(Router router, Jdbi jdbi, RunService runService) {
        router.route().failureHandler(routingContext -> routingContext.failure().printStackTrace());
        router.route(POST, "/runs").handler(routingContext -> {
            var itemCount = Integer.parseInt(routingContext.request().getParam("item_count"));
            var id = jdbi.withExtension(RunDAO.class, RunDAO::createRun);
            var isSuccess = runService.executeRun(itemCount);
            var run = jdbi.withExtension(RunDAO.class, dao -> dao.changeStatus(id, isSuccess ? "SUCCESS" : "FAILED"));
            routingContext.response().end(JsonObject.mapFrom(run).encodePrettily());
        });
        router.route(GET, "/runs").handler(routingContext -> {
            var runs = jdbi.withExtension(RunDAO.class, RunDAO::selectRuns);
            routingContext.response().end(new JsonArray(runs).encodePrettily());
        });
        return router;
    }
}
