package thesis.data.model;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("record")
public class Record implements Identifiable {
    @Id
    private String id;
    @NotNull(message = "idRaw cannot be null")
    private Integer idRaw;
    // metadata in JSON format
    private String metadata;

    public Record(Integer idRaw, String metadata) {
        this.idRaw = idRaw;
        this.metadata = metadata;
    }

    public Record() {
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getIdRaw() {
        return idRaw;
    }

    public void setIdRaw(Integer idRaw) {
        this.idRaw = idRaw;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}
