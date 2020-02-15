import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.model.HttpRequest;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.mockserver.model.HttpRequest.request;

@Testcontainers
public class RunServiceTest {

    @Container
    public static MockServerContainer mockServer = new MockServerContainer();

    private static RunService runService;
    private MockServerClient mockServerClient;

    @BeforeAll
    public static void beforeAll() {
        runService = new RunService(
                new HttpUrl.Builder()
                        .scheme("http")
                        .host(mockServer.getContainerIpAddress())
                        .port(mockServer.getServerPort())
                        .build(),
                new OkHttpClient()
        );
    }

    @BeforeEach
    public void beforeEach() {
        mockServerClient = new MockServerClient(mockServer.getContainerIpAddress(), mockServer.getServerPort());
    }

    @Test
    @DisplayName("A run results in a successful request")
    public void t1() {
        runService.executeRun();
        mockServerClient.verify(request().withMethod("POST").withPath("/item"));
    }

}
