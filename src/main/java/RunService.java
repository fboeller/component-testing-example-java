import java.io.IOException;

public class RunService {

    private final ExternalService externalService;

    public RunService(ExternalService externalService) {
        this.externalService = externalService;
    }

    public boolean executeRun(int itemCount) {
        var allCallsSuccessful = true;
        for (int i = 0; i < itemCount; i++) {
            try {
                var response = externalService.postItem().execute();
                allCallsSuccessful = allCallsSuccessful && response.isSuccessful();
            } catch (IOException e) {
                return false;
            }
        }
        return allCallsSuccessful;
    }

}
