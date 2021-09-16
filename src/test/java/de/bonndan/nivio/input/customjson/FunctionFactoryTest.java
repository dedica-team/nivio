package de.bonndan.nivio.input.customjson;

import com.jayway.jsonpath.InvalidPathException;
import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.ProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.regex.PatternSyntaxException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class FunctionFactoryTest {

    private FunctionFactory functionFactory;

    @BeforeEach
    void setUp() {
        FileFetcher fileFetcher = mock(FileFetcher.class);
        functionFactory = new FunctionFactory(fileFetcher);
    }

    @Test
    void handlesIOError() {
        assertThrows(IllegalArgumentException.class, () -> functionFactory.asFunctions("\"", null));
    }

    @Test
    void handlesPipesInRegexes() {
        List<Function<String, String>> functions = functionFactory.asFunctions("find \"([a-z|])\"|fetch", null);
        assertThat(functions).hasSize(2);
    }

    @Test
    void complexRegex() {
        String pipedSteps = "find \"https:\\/\\/[\\w.\\-_]*\\/([\\w.\\-_]*)\\/.*\"";
        List<Function<String, String>> functions = functionFactory.asFunctions(pipedSteps, null);
        assertThat(functions).hasSize(1);
        Function<String, String> find = functions.get(0);
        String path = find.apply("https://host.ignored.com/this.is-important/othercrap");
        assertThat(path).isEqualTo("this.is-important");
    }


    @Test
    void failsEarlyOnJsonPath() {
        assertThrows(InvalidPathException.class, () -> functionFactory.asFunctions(".abc.", null));
    }

    @Test
    void failsEarlyOnRegex() {
        assertThrows(PatternSyntaxException.class, () -> functionFactory.asFunctions("find \"([a-z)\"", null));
    }

    @Test
    void worksWithRegex() {
        List<Function<String, String>> functions = functionFactory.asFunctions("find \"(^[a-z]*)\"", null);

        assertThat(functions).hasSize(1);
        Function<String, String> find = functions.get(0);
        String abc09 = find.apply("abc09");
        assertThat(abc09).isEqualTo("abc");
    }

    @Test
    void worksWithPath() {
        List<Function<String, String>> functions = functionFactory.asFunctions("$.id", null);

        assertThat(functions).hasSize(1);
        Function<String, String> find = functions.get(0);

        String s = find.apply("{\"id\": 1}");
        assertThat(s).isEqualTo("1");
    }

    @Test
    void worksWithPathNotFound() {
        List<Function<String, String>> functions = functionFactory.asFunctions("$.id", null);

        assertThat(functions).hasSize(1);
        Function<String, String> find = functions.get(0);

        String s = find.apply("{\"foo\": 1}");
        assertThat(s).isEqualTo("");
    }
}
