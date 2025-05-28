package thesis.domain.manipulation.service;

import thesis.data.model.Identifiable;

public class TestEntity implements Identifiable {
    private final String id;

    public TestEntity(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }
}