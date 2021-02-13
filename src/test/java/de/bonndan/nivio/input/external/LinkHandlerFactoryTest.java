package de.bonndan.nivio.input.external;

import de.bonndan.nivio.input.external.github.GitHubRepoHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LinkHandlerFactoryTest {

    @Test
    void doesNotThrowExceptionHandlerConstructionFails() {
        AutowireCapableBeanFactory mock = mock(AutowireCapableBeanFactory.class);
        LinkHandlerFactory linkHandlerFactory = new LinkHandlerFactory(mock);
        when(mock.createBean(eq(GitHubRepoHandler.class))).thenThrow(new BeanCreationException("foo"));

        //when
        Optional<ExternalLinkHandler> resolver = linkHandlerFactory.getResolver(LinkHandlerFactory.GITHUB);
        assertThat(resolver).isEmpty();
    }
}