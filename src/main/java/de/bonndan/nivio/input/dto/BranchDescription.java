package de.bonndan.nivio.input.dto;

import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BranchDescription {

    private List<String> items = new ArrayList<>();

    public BranchDescription() {

    }

    public BranchDescription(@NonNull final List<String> items) {
        this.items = Objects.requireNonNull(items);
    }


    public List<String> getItems() {
        return items;
    }

    public void setItems(@NonNull final List<String> items) {
        this.items = Objects.requireNonNull(items);
    }
}
