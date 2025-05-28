package thesis.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thesis.data.model.Technology;
import thesis.data.model.TechnologyProperties;
import thesis.data.repository.ResultRepository;
import thesis.data.repository.TechnologyRepository;
import thesis.exceptions.BadRequestException;
import thesis.exceptions.EntityInUseException;
import thesis.exceptions.EntityNotFoundException;

import java.util.ArrayList;
import java.util.Set;

@Service
public class TechnologyService extends BaseEntityService<Technology> {
    private final TechnologyRepository technologyRepository;
    private final ResultRepository resultRepository;

    @Autowired
    public TechnologyService(TechnologyRepository technologyRepository, ResultRepository resultRepository) {
        super(technologyRepository);
        this.technologyRepository = technologyRepository;
        this.resultRepository = resultRepository;
    }

    @Override
    public Technology save(Technology technology) {
        if (!existsById(technology.getName())) {
            savePropertiesList(technology);
            return technologyRepository.save(technology);
        }

        return getEntity(technology.getName());
    }

    @Override
    public Technology getEntity(String name) {
        return technologyRepository.findById(name)
                .orElseThrow(() -> new EntityNotFoundException("Technology with name " + name + " not found"));
    }

    public Set<String> searchForTechnologyWithMinSensitivity(String markerName, Double minSensitivity) {
        return technologyRepository.searchForTechnologyWithMinSensitivity(markerName, minSensitivity);
    }

    public Set<String> searchForTechnologyWithMinSpecificity(String markerName, Double minSpecificity) {
        return technologyRepository.searchForTechnologyWithMinSpecificity(markerName, minSpecificity);
    }

    @Override
    public void delete(String name) {
        var technology = getEntity(name);

        if (isTechnologyUsedInResults(name)) {
            throw new EntityInUseException("Cannot delete technology with name " + name + " because it is used in results");
        }

        if (isTechnologyUsedInComparableWith(name)) {
            throw new EntityInUseException("Cannot delete technology with name " + name + " because it is used in comparableWith");
        }

        deletePropertiesForTechnology(technology);

        technologyRepository.deleteById(name);
    }

    public Technology addProperties(String technologyName, TechnologyProperties technologyProperties) {
        var technology = getEntity(technologyName);
        if (technology.getProperties() != null && technology.getProperties().stream().anyMatch(d -> d.getMarkerName().equals(technologyProperties.getMarkerName()))) {
            throw new BadRequestException("Properties for marker name " + technologyProperties.getMarkerName() + " already exist");
        }

        saveProperties(technologyProperties, technologyName);

        if (technology.getProperties() == null) {
            technology.setProperties(new ArrayList<>());
        }
        technology.getProperties().add(technologyProperties);

        return technologyRepository.save(technology);
    }

    private void savePropertiesList(Technology technology) {
        if (technology.getProperties() != null) {
            technology.getProperties().forEach(properties -> {
                saveProperties(properties, technology.getName());
            });
        }
    }

    public void saveProperties(TechnologyProperties properties, String technologyName) {
        if (properties.getIsPercentage() != null) {
            technologyRepository.saveIsPercentage(properties, technologyName);
        }
        if (properties.getDeviationRanges() != null) {
            technologyRepository.saveDeviationRanges(properties.getDeviationRanges(), technologyName, properties.getMarkerName());
        }

        if (properties.getSensitivity() != null) {
            technologyRepository.saveSensitivity(technologyName, properties.getMarkerName(), properties.getSensitivity());
        }
        if (properties.getSpecificity() != null) {
            technologyRepository.saveSpecificity(technologyName, properties.getMarkerName(), properties.getSpecificity());
        }
    }

    private void deletePropertiesForTechnology(Technology technology) {
        if (technology.getProperties() != null) {
            technology.getProperties().forEach(properties -> {
                deleteProperties(technology.getName(), properties);
            });
        }
    }

    private void deleteProperties(String technologyName, TechnologyProperties technologyProperties) {
        technologyRepository.deleteDeviation(technologyProperties.getMarkerName(), technologyName);
        technologyRepository.deleteSensitivity(technologyProperties.getMarkerName(), technologyName);
        technologyRepository.deleteSpecificity(technologyProperties.getMarkerName(), technologyName);
        if (technologyProperties.getDeviationRanges() != null) {
            technologyRepository.deleteDeviationRange(technologyProperties.getDeviationRanges(), technologyProperties.getMarkerName(), technologyName);
        }
    }

    private Boolean isTechnologyUsedInResults(String technologyName) {
        return resultRepository.existsByTechnologyName(technologyName);
    }

    private Boolean isTechnologyUsedInComparableWith(String name) {
        var technologies = findAll();
        for (var technology : technologies) {
            if (technology.getProperties() != null &&
                    technology.getProperties().stream()
                            .anyMatch(d -> d.getComparableWith() != null && d.getComparableWith().contains(name))) {
                return true;
            }
        }

        return false;
    }
}
