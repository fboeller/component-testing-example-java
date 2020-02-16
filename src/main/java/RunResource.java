import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.jdbi.v3.core.Jdbi;

import static io.vertx.core.http.HttpMethod.GET;
import static io.vertx.core.http.HttpMethod.POST;

public class RunResource {

    private final Jdbi jdbi;
    private final RunService runService;

    public RunResource(Jdbi jdbi, RunService runService) {
        this.jdbi = jdbi;
        this.runService = runService;
    }

    public Router configureRouter(Router router) {
        router.route().failureHandler(routingContext -> routingContext.failure().printStackTrace());
        router.route(POST, "/runs").handler(this::postRun);
        router.route(GET, "/runs").handler(this::getRuns);
        return router;
    }

    public void postRun(RoutingContext routingContext) {
        var itemCount = Integer.parseInt(routingContext.request().getParam("item_count"));
        var id = jdbi.withExtension(RunDAO.class, RunDAO::createRun);
        var isSuccess = runService.executeRun(itemCount);
        var run = jdbi.withExtension(RunDAO.class, dao -> dao.changeStatus(id, isSuccess ? "SUCCESS" : "FAILED"));
        routingContext.response().end(JsonObject.mapFrom(run).encodePrettily());
    }

    public void getRuns(RoutingContext routingContext) {
        var runs = jdbi.withExtension(RunDAO.class, RunDAO::selectRuns);
        routingContext.response().end(new JsonArray(runs).encodePrettily());
    }

}
