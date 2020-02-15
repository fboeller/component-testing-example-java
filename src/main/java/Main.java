import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.jdbi.v3.core.Jdbi;

import static io.vertx.core.http.HttpMethod.GET;
import static io.vertx.core.http.HttpMethod.POST;

public class Main {

    public static void main(String[] args) throws Exception {
        var vertx = Vertx.vertx();
        var jdbi = Database.initDatabase();
        var runService = new RunService(HttpUrl.parse("http://localhost:4203"), new OkHttpClient());
        var router = configureRouter(Router.router(vertx), jdbi, runService);
        vertx.createHttpServer()
                .requestHandler(router)
                .listen(4201);
    }

    private static Router configureRouter(Router router, Jdbi jdbi, RunService runService) {
        router.route(POST, "/runs").handler(routingContext -> {
            var id = jdbi.withExtension(RunDAO.class, RunDAO::createRun);
            runService.executeRun();
            routingContext.response().end(id.toString());
        });
        router.route(GET, "/runs").handler(routingContext -> {
            var runs = jdbi.withExtension(RunDAO.class, RunDAO::selectRuns);
            routingContext.response().end(runs.toString());
        });
        return router;
    }
}
