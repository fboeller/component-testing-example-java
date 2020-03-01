import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.model.HttpError.error;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.verify.VerificationTimes.exactly;
import static org.mockserver.verify.VerificationTimes.once;

@Testcontainers
public class RunServiceTest {

    @Container
    public static MockServerContainer mockServer = new MockServerContainer("5.9.0");

    private static RunService runService;
    private static MockServerClient mockServerClient;

    @BeforeAll
    public static void beforeAll() {
        var externalService = ExternalService.create(mockServer.getEndpoint());
        runService = new RunService(externalService);
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
        mockServerClient.verify(request().withMethod("POST").withPath("/item"), once());
    }

    @Test
    @DisplayName("Multiple runs result successful requests")
    public void t2() {
        mockServerClient.when(request()).respond(response().withStatusCode(200));
        var isSuccess = runService.executeRun(5);
        assertThat(isSuccess).isTrue();
        mockServerClient.verify(request().withMethod("POST").withPath("/item"), exactly(5));
    }

    @Test
    @DisplayName("A run fails on a server error")
    public void t3() {
        mockServerClient.when(request()).respond(response().withStatusCode(500));
        var isSuccess = runService.executeRun(1);
        assertThat(isSuccess).isFalse();
    }

    @Test
    @DisplayName("A run fails on a connection error")
    public void t4() {
        mockServerClient.when(request()).error(error().withDropConnection(true));
        var isSuccess = runService.executeRun(2);
        assertThat(isSuccess).isFalse();
    }

}
