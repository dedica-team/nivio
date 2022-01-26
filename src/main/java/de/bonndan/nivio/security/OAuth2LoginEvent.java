package de.bonndan.nivio.security;

import org.springframework.context.ApplicationEvent;

public class OAuth2LoginEvent extends ApplicationEvent {
    public OAuth2LoginEvent(CustomOAuth2User customOAuth2User) {
        super(customOAuth2User);
    }

    @Override
    public CustomOAuth2User getSource() {
        return (CustomOAuth2User) source;
    }

}
