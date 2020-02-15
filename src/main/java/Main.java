import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import java.util.UUID;

import static io.vertx.core.http.HttpMethod.POST;

public class Main {

    public static void main(String[] args) {
        var vertx = Vertx.vertx();
        var router = Router.router(vertx);
        router.route(POST, "/run").handler(routingContext -> {
            var response = routingContext.response();
            response.end("Hello world!");
        });
        vertx.createHttpServer().requestHandler(router).listen(4242);
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
