package thesis.domain.manipulation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thesis.data.model.Marker;
import thesis.data.service.MarkerService;
import thesis.data.validation.database.MarkerDatabaseValidator;

@Service
public class MarkerManipulationService extends BaseEntityManipulationService<Marker> {
    @Autowired
    public MarkerManipulationService(MarkerService markerService, MarkerDatabaseValidator markerDatabaseValidator) {
        super(markerService, markerDatabaseValidator);
    }
}
