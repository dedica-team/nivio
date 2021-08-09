package de.bonndan.nivio.output.map;

import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeFactory;
import de.bonndan.nivio.output.map.svg.SVGDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RenderingRepositoryTest {

    private Landscape landscape;
    private RenderingRepository renderingRepository;

    @BeforeEach
    public void setup() {
        landscape = LandscapeFactory.createForTesting("foo", "bar").build();
        renderingRepository = new RenderingRepository();
    }

    @Test
    void saveAndGet() {
        //given
        SVGDocument foo = mock(SVGDocument.class);
        renderingRepository.save("foo", landscape, foo, false);

        //when
        Optional<Object> first = renderingRepository.get("foo", landscape, false);

        //then
        assertThat(first).isPresent().get().isEqualTo(foo);
    }

}