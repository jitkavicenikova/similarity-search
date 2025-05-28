package thesis.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thesis.data.model.Conversion;
import thesis.data.model.Unit;
import thesis.data.repository.MarkerRepository;
import thesis.data.repository.UnitRepository;
import thesis.exceptions.BadRequestException;
import thesis.exceptions.EntityInUseException;
import thesis.exceptions.EntityNotFoundException;

import java.util.ArrayList;

@Service
public class UnitService extends BaseEntityService<Unit> {
    private final UnitRepository unitRepository;
    private final MarkerRepository markerRepository;

    @Autowired
    public UnitService(UnitRepository unitRepository, MarkerRepository markerRepository) {
        super(unitRepository);
        this.unitRepository = unitRepository;
        this.markerRepository = markerRepository;
    }

    @Override
    public Unit getEntity(String name) {
        return unitRepository.findById(name)
                .orElseThrow(() -> new EntityNotFoundException("Unit with name '" + name + "' not found"));
    }

    @Override
    public void delete(String name) {
        if (!unitRepository.existsById(name)) {
            throw new EntityNotFoundException("Unit with name '" + name + "' not found");
        }

        if (isUnitUsedInMarkers(name)) {
            throw new EntityInUseException("Cannot delete unit with name '" + name + "' because it is used in markers");
        }

        if (isUnitUsedInConversions(name)) {
            throw new EntityInUseException("Cannot delete unit with name '" + name + "' because it is used in conversions");
        }

        unitRepository.deleteById(name);
    }

    public Unit addConversion(String unitName, Conversion conversion) {
        if (unitName.equals(conversion.getTargetUnitName())) {
            throw new BadRequestException("Source and target unit names must be different");
        }

        var unit = getEntity(unitName);
        if (unit.getConversions() == null) {
            unit.setConversions(new ArrayList<>());
        }

        if (unit.getConversions().stream().anyMatch(c -> c.getMarkerName().equals(conversion.getMarkerName()))) {
            throw new BadRequestException("Conversion with marker name '" + conversion.getMarkerName() + "' already exists");
        }

        unit.getConversions().add(conversion);
        return unitRepository.save(unit);
    }

    private Boolean isUnitUsedInMarkers(String unitName) {
        return markerRepository.existsByUnitName(unitName);
    }

    private Boolean isUnitUsedInConversions(String unitName) {
        var units = findAll();
        for (var unit : units) {
            if (unit.getConversions() != null &&
                    unit.getConversions().stream().anyMatch(c -> c.getTargetUnitName().equals(unitName))) {
                return true;
            }
        }

        return false;
    }
}
