package de.bonndan.nivio.security;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * Implementation of {@link OAuth2User} which provides an alias, name, avatar url, external id, and idProvider
 */
public class CustomOAuth2User implements OAuth2User {

    private final String name;
    private final String avatarUrl;
    @NonNull
    private final String alias;
    private final String externalId;
    private final String idProvider;
    private final Map<String, Object> attributes;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomOAuth2User(@NonNull final String externalId,
                            @NonNull final String alias,
                            @Nullable final String name,
                            @NonNull final Map<String, Object> attributes,
                            @NonNull final Collection<? extends GrantedAuthority> authorities,
                            @Nullable final String avatarUrl,
                            @NonNull final String idProvider
    ) {
        this.externalId = Objects.requireNonNull(externalId, "id must not be null");
        this.alias = Objects.requireNonNull(alias, "alias must not be null");
        this.attributes = Objects.requireNonNull(attributes, "attributes must not be null");
        this.authorities = Objects.requireNonNull(authorities, "authorities must not be null");
        this.idProvider = Objects.requireNonNull(idProvider, "idp must not be null");
        this.name = name;
        this.avatarUrl = avatarUrl;

    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return name;
    }

    @Nullable
    public String getAvatarUrl() {
        return avatarUrl;
    }

    @NonNull
    public String getAlias() {
        return alias;
    }

    @NonNull
    public String getIdProvider() {
        return idProvider;
    }

    @NonNull
    public String getExternalId() {
        return externalId;
    }
}
