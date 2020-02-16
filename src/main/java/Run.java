import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.UUID;

public class Run {

    private final UUID id;
    private final String status;

    public Run(UUID id, String status) {
        this.id = id;
        this.status = status;
    }

    @JsonProperty("id")
    public UUID getId() {
        return id;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Run run = (Run) o;
        return Objects.equals(id, run.id) &&
                Objects.equals(status, run.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status);
    }

    @Override
    public String toString() {
        return "Run{id=" + id + ", status='" + status + '\'' + '}';
    }
}
