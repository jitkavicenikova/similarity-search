package thesis.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thesis.data.model.Marker;
import thesis.data.repository.MarkerRepository;
import thesis.data.repository.ResultRepository;
import thesis.data.repository.TechnologyRepository;
import thesis.data.repository.UnitRepository;
import thesis.exceptions.EntityInUseException;
import thesis.exceptions.EntityNotFoundException;

@Service
public class MarkerService extends BaseEntityService<Marker> {
    private final MarkerRepository markerRepository;
    private final ResultRepository resultRepository;
    private final UnitRepository unitRepository;
    private final TechnologyRepository technologyRepository;

    @Autowired
    public MarkerService(MarkerRepository markerRepository, ResultRepository resultRepository,
                         UnitRepository unitRepository, TechnologyRepository technologyRepository) {
        super(markerRepository);
        this.markerRepository = markerRepository;
        this.resultRepository = resultRepository;
        this.unitRepository = unitRepository;
        this.technologyRepository = technologyRepository;
    }

    @Override
    public Marker getEntity(String name) {
        return markerRepository.findById(name)
                .orElseThrow(() -> new EntityNotFoundException("Marker with name '" + name + "' not found"));
    }

    @Override
    public void delete(String name) {
        if (!markerRepository.existsById(name)) {
            throw new EntityNotFoundException("Marker with name '" + name + "' not found");
        }

        if (isMarkerUsedInResults(name)) {
            throw new EntityInUseException(createErrorMessage("results", name));
        }

        if (isMarkerUsedInConversions(name)) {
            throw new EntityInUseException(createErrorMessage("conversions", name));
        }

        if (isMarkerUsedInTechnologyProperties(name)) {
            throw new EntityInUseException(createErrorMessage("technology properties", name));
        }

        if (isMarkerUsedInChildMarkers(name)) {
            throw new EntityInUseException(createErrorMessage("child markers", name));
        }

        markerRepository.deleteById(name);
    }

    private Boolean isMarkerUsedInChildMarkers(String markerName) {
        var markers = findAll();
        for (Marker marker : markers) {
            if (marker.getChildMarkerNames() != null &&
                    marker.getChildMarkerNames().contains(markerName)) {
                return true;
            }
        }

        return false;
    }

    private Boolean isMarkerUsedInConversions(String markerName) {
        var units = unitRepository.findAll();
        for (var unit : units) {
            if (unit.getConversions() != null &&
                    unit.getConversions().stream().anyMatch(c -> c.getMarkerName().equals(markerName))) {
                return true;
            }
        }
        return false;
    }

    private Boolean isMarkerUsedInTechnologyProperties(String markerName) {
        var technologies = technologyRepository.findAll();
        for (var technology : technologies) {
            if (technology.getProperties() != null &&
                    technology.getProperties().stream()
                            .anyMatch(d -> d.getMarkerName().equals(markerName))) {
                return true;
            }
        }

        return false;
    }

    private Boolean isMarkerUsedInResults(String markerName) {
        return resultRepository.existsByMarkerName(markerName);
    }

    private String createErrorMessage(String reason, String name) {
        return "Cannot delete marker with name '" + name + "' because it is used in " + reason;
    }
}
