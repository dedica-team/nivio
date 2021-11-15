package de.bonndan.nivio.assessment;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.GroupFactory;
import de.bonndan.nivio.model.Labeled;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AssessableGroupTest {

    @Test
    void getLabels() {
        AssessableGroup group = new AssessableGroup(new Group("foo", "bar"), new HashSet<>());
        assertThat(group).isInstanceOf(Labeled.class);
    }
}