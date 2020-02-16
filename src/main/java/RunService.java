import okhttp3.*;

import java.io.IOException;

public class RunService {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final HttpUrl baseUrl;

    public RunService(HttpUrl baseUrl, OkHttpClient client) {
        this.client = client;
        this.baseUrl = baseUrl;
    }

    public boolean executeRun(int itemCount) {
        var allCallsSuccessful = true;
        for (int i=0; i < itemCount; i++) {
            try {
                var request = new Request.Builder()
                        .post(RequestBody.create("{}", JSON))
                        .url(baseUrl.resolve("item"))
                        .build();
                var response = client.newCall(request).execute();
                allCallsSuccessful = allCallsSuccessful && response.isSuccessful();
            } catch (IOException e) {
                return false;
            }
        }
        return allCallsSuccessful;
    }

}
