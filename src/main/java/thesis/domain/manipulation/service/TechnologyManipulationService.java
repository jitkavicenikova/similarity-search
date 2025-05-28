package thesis.domain.manipulation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thesis.data.model.Technology;
import thesis.data.model.TechnologyProperties;
import thesis.data.service.TechnologyService;
import thesis.data.validation.database.TechnologyDatabaseValidator;
import thesis.data.validation.database.TechnologyPropertiesDatabaseValidator;

@Service
public class TechnologyManipulationService extends BaseEntityManipulationService<Technology> {
    private final TechnologyService technologyService;
    private final TechnologyPropertiesDatabaseValidator propertiesDatabaseValidator;

    @Autowired
    public TechnologyManipulationService(TechnologyService technologyService, TechnologyDatabaseValidator technologyDatabaseValidator,
                                         TechnologyPropertiesDatabaseValidator propertiesDatabaseValidator) {
        super(technologyService, technologyDatabaseValidator);
        this.technologyService = technologyService;
        this.propertiesDatabaseValidator = propertiesDatabaseValidator;
    }

    public Technology addProperties(String technologyName, TechnologyProperties technologyProperties) {
        propertiesDatabaseValidator.runValidation(technologyProperties);

        return technologyService.addProperties(technologyName, technologyProperties);
    }
}
