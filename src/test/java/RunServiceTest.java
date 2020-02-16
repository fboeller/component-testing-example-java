import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.*;
import org.mockserver.client.proxy.Times;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.model.HttpResponse;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

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
        mockServerClient.when(request()).respond(response().withStatusCode(200));
        var isSuccess = runService.executeRun(1);
        assertThat(isSuccess).isTrue();
        mockServerClient.verify(request().withMethod("POST").withPath("/item"), Times.once());
    }

    @Test
    @DisplayName("Multiple runs result successful requests")
    public void t2() {
        mockServerClient.when(request()).respond(response().withStatusCode(200));
        var isSuccess = runService.executeRun(5);
        assertThat(isSuccess).isTrue();
        mockServerClient.verify(request().withMethod("POST").withPath("/item"), Times.exactly(5));
    }

    @Test
    @DisplayName("A run fails on a server error")
    public void t3() {
        mockServerClient.when(request()).respond(response().withStatusCode(500));
        var isSuccess = runService.executeRun(1);
        assertThat(isSuccess).isFalse();
    }

}
