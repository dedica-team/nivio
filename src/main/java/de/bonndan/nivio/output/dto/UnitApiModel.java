package de.bonndan.nivio.output.dto;

import de.bonndan.nivio.model.Unit;

import java.util.List;
import java.util.stream.Collectors;

public class UnitApiModel extends ComponentApiModel {

    private final List<ContextApiModel> contexts;

    public UnitApiModel(Unit unit) {
        super(unit);
        contexts = unit.getChildren().stream()
                .map(ContextApiModel::new)
                .collect(Collectors.toList());
    }

    public List<ContextApiModel> getContexts() {
        return contexts;
    }
}
