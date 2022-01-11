package de.bonndan.nivio.security;

import de.bonndan.nivio.api.LinkFactory;
import de.bonndan.nivio.config.NivioConfigProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.support.BindingAwareConcurrentModel;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LoginControllerUnitTest {

    private NivioConfigProperties properties;
    private BindingAwareConcurrentModel model;
    private LoginController loginController;

    @BeforeEach
    void setup() {

        LinkFactory linkFactory = mock(LinkFactory.class);

        properties = mock(NivioConfigProperties.class);
        when(properties.getLoginMode()).thenReturn("required");
        when(properties.getBrandingLogoUrl()).thenReturn("nivio.icons.example.svg");
        when(properties.getBrandingForeground()).thenReturn("#FFFFFF");
        when(properties.getBrandingBackground()).thenReturn("#FFFFFF");
        when(properties.getBrandingMessage()).thenReturn("foo");

        loginController = new LoginController(linkFactory, properties);
        model = new BindingAwareConcurrentModel();

    }

    @Test
    void showLoginPage() {

        // when
        model.addAttribute("brandingLogoUrl", properties.getBrandingLogoUrl());
        model.addAttribute("brandingForeground", properties.getBrandingForeground());
        model.addAttribute("brandingBackground", properties.getBrandingBackground());
        model.addAttribute("brandingMessage", properties.getBrandingMessage());

        // then
        assertThat(loginController.showLoginPage(model)).isEqualTo("login");
        assertThat(model.getAttribute("brandingLogoUrl")).isEqualTo("nivio.icons.example.svg");
        assertThat(model.getAttribute("brandingForeground")).isEqualTo("#FFFFFF");
        assertThat(model.getAttribute("brandingBackground")).isEqualTo("#FFFFFF");
        assertThat(model.getAttribute("brandingMessage")).isEqualTo("foo");

    }

}