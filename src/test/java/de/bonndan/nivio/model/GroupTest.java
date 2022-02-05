package de.bonndan.nivio.model;

import de.bonndan.nivio.GraphTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GroupTest {

    private Context context;
    private GraphTestSupport graph;

    @BeforeEach
    void setup() {
        graph = new GraphTestSupport();
        context = graph.context;
    }

    @Test
    void getParentFromAttached() {

        //when
        Context parent = graph.groupA.getParent();

        //then
        assertThat(parent).isNotNull().isSameAs(context);
    }

}