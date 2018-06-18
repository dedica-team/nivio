package de.bonndan.nivio.landscape;

import java.io.Serializable;
import java.util.Objects;

public class DataFlowId implements Serializable {

    private String source_identifier;
    private String target_identifier;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataFlowId that = (DataFlowId) o;
        return Objects.equals(source_identifier, that.source_identifier) &&
                Objects.equals(target_identifier, that.target_identifier);
    }

    @Override
    public int hashCode() {

        return Objects.hash(source_identifier, target_identifier);
    }
}
