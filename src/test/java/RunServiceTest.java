import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.*;
import org.mockserver.client.proxy.Times;
import org.mockserver.client.server.MockServerClient;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.mockserver.model.HttpRequest.request;

@Testcontainers
public class RunServiceTest {

    @Container
    public static MockServerContainer mockServer = new MockServerContainer();

    private static RunService runService;
    private static MockServerClient mockServerClient;

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
        mockServerClient = new MockServerClient(mockServer.getContainerIpAddress(), mockServer.getServerPort());
    }

    @AfterEach
    public void afterEach() {
        mockServerClient.reset();
    }

    @Test
    @DisplayName("A run results in a successful request")
    public void t1() {
        runService.executeRun(1);
        mockServerClient.verify(request().withMethod("POST").withPath("/item"), Times.once());
    }

    @Test
    @DisplayName("Multiple runs result successful requests")
    public void t2() {
        runService.executeRun(5);
        mockServerClient.verify(request().withMethod("POST").withPath("/item"), Times.exactly(5));
    }

}
