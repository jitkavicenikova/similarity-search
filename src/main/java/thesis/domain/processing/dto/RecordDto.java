package thesis.domain.processing.dto;

/**
 * Represents a record with an id and metadata.
 */
public class RecordDto {
    private Integer id;
    // metadata in JSON format
    private String metadata;

    public RecordDto() {
    }

    public RecordDto(Integer id) {
        this.id = id;
    }

    public RecordDto(Integer id, String metadata) {
        this.id = id;
        this.metadata = metadata;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}
