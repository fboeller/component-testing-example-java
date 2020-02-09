import java.util.Objects;
import java.util.UUID;

public class Warning {

    private final UUID runId;
    private final String message;

    public Warning(UUID runId, String message) {
        this.runId = runId;
        this.message = message;
    }

    public UUID getRunId() {
        return runId;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Warning warning = (Warning) o;
        return Objects.equals(runId, warning.runId) &&
                Objects.equals(message, warning.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(runId, message);
    }

    @Override
    public String toString() {
        return "Warning{" +
                "runId=" + runId +
                ", message='" + message + '\'' +
                '}';
    }
}
