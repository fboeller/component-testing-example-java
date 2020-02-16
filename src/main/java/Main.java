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

    public static void main(String[] args) throws Exception {
        var vertx = Vertx.vertx();
        var jdbi = Database.initDatabase("jdbc:postgresql://localhost:" + DB_PORT + "/postgres");
        var externalServiceUrl = new HttpUrl.Builder()
                .scheme("http").host("localhost").port(EXTERNAL_SERVICE_PORT).build();
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
            routingContext.response().end(
                    new JsonObject().put("id", id.toString()).put("success", isSuccess).toString()
            );
        });
        router.route(GET, "/runs").handler(routingContext -> {
            var runs = jdbi.withExtension(RunDAO.class, RunDAO::selectRuns);
            routingContext.response().end(new JsonArray(runs).toString());
        });
        return router;
    }
}
