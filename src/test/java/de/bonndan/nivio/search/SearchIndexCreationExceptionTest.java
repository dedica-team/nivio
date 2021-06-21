package de.bonndan.nivio.search;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class SearchIndexCreationExceptionTest {

    @Test
    void testException() {
        var searchIndexCreationException = new SearchIndexCreationException("test", new IOException("test"));
        assertThat(searchIndexCreationException.getClass()).isEqualTo(SearchIndexCreationException.class);
        assertThat(searchIndexCreationException.getMessage()).isEqualTo("test");
        assertThat(searchIndexCreationException.getCause()).isEqualToComparingFieldByField(new IOException("test"));
    }
}
