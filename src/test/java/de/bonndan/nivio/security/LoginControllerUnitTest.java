package de.bonndan.nivio.security;

import de.bonndan.nivio.api.LinkFactory;
import de.bonndan.nivio.config.NivioConfigProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LoginControllerUnitTest {

    private LinkFactory linkFactory;
    private NivioConfigProperties properties;
    private Model model;

    @BeforeEach
    void setup() {

        linkFactory = mock(LinkFactory.class);
        properties = mock(NivioConfigProperties.class);
        model = mock(Model.class);
        when(properties.getLoginMode()).thenReturn("required");

    }

    @Test
    void showLoginPage() {

        // given
        LoginController loginController = new LoginController(linkFactory, properties);

        // when
        when(model.getAttribute("brandingLogoUrl")).thenReturn("nivio.icons.example.svg");
        when(model.getAttribute("brandingForeground")).thenReturn("#FFFFFF");
        when(model.getAttribute("brandingBackground")).thenReturn("#FFFFFF");
        when(model.getAttribute("brandingMessage")).thenReturn("foo");

        // then
        assertThat(loginController.showLoginPage(model)).isEqualTo("login");
        assertThat(Objects.requireNonNull(model.getAttribute("brandingLogoUrl"))).isEqualTo("nivio.icons.example.svg");
        assertThat(Objects.requireNonNull(model.getAttribute("brandingForeground"))).isEqualTo("#FFFFFF");
        assertThat(Objects.requireNonNull(model.getAttribute("brandingBackground"))).isEqualTo("#FFFFFF");
        assertThat(Objects.requireNonNull(model.getAttribute("brandingMessage"))).isEqualTo("foo");

    }

}