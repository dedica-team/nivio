package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ComponentDescription;
import de.bonndan.nivio.input.dto.GroupDescription;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ComponentDescriptionValuesTest {

    @Test
    void assignNotNull() {
        ComponentDescription component = new GroupDescription();

        ComponentDescription increment = new GroupDescription();
        increment.setName("name");
        increment.setDescription("desc");
        increment.setOwner("owner");
        increment.setContact("contact");

        //when
        ComponentDescriptionValues.assignNotNull(component, increment);

        //then
        assertThat(component.getName()).isEqualTo("name");
        assertThat(component.getDescription()).isEqualTo("desc");
        assertThat(component.getOwner()).isEqualTo("owner");
        assertThat(component.getContact()).isEqualTo("contact");
    }

    @Test
    void assignSafeNotNull() {
        ComponentDescription component = new GroupDescription();
        ComponentDescription increment = new GroupDescription();

        component.setName("name");
        component.setDescription("desc");
        component.setOwner("owner");
        component.setContact("contact");

        increment.setName("name2");
        increment.setDescription("desc2");
        increment.setOwner("owner2");
        increment.setContact("contact2");

        //when
        ComponentDescriptionValues.assignSafeNotNull(component, increment);

        //then
        assertThat(component.getName()).isEqualTo("name");
        assertThat(component.getDescription()).isEqualTo("desc");
        assertThat(component.getOwner()).isEqualTo("owner");
        assertThat(component.getContact()).isEqualTo("contact");
    }

}