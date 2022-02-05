package de.bonndan.nivio.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class LandscapeTest {


    @Test
    void addSelfToIndex() {

        //given
        var index = mock(Index.class);

        //when
        Landscape build = LandscapeFactory.createForTesting("test", "test").withIndex(index).build();

        //then
        verify(index).addOrReplace(build);
        assertThat(build.isAttached()).isTrue();
    }

}