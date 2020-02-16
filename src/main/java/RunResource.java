import org.jdbi.v3.core.Jdbi;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/runs")
@Produces(MediaType.APPLICATION_JSON)
public class RunResource {

    private final Jdbi jdbi;
    private final RunService runService;

    public RunResource(Jdbi jdbi, RunService runService) {
        this.jdbi = jdbi;
        this.runService = runService;
    }

    @POST
    public Run postRun(@QueryParam("item_count") int itemCount) {
        var id = jdbi.withExtension(RunDAO.class, RunDAO::createRun);
        var isSuccess = runService.executeRun(itemCount);
        return jdbi.withExtension(RunDAO.class, dao -> dao.changeStatus(id, isSuccess ? "SUCCESS" : "FAILED"));
    }

    @GET
    public List<Run> getRuns() {
        return jdbi.withExtension(RunDAO.class, RunDAO::selectRuns);
    }

}
