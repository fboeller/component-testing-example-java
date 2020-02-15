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

    public void executeRun() {
        try {
            var request = new Request.Builder()
                    .post(RequestBody.create("{}", JSON))
                    .url(baseUrl.resolve("item"))
                    .build();
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO Handle
        }
    }

}
