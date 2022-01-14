package de.bonndan.nivio.security;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * Implementation of {@link OAuth2User} which provides an id, alias and avatar url
 */
public class CustomOAuth2User implements OAuth2User {

    private final String id;
    private final String alias;
    private final String name;
    private final String avatarUrl;
    private final Map<String, Object> attributes;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomOAuth2User(@NonNull final String id,
                            @NonNull final String alias,
                            @NonNull final String name,
                            @NonNull final Map<String, Object> attributes,
                            @NonNull final Collection<? extends GrantedAuthority> authorities,
                            @Nullable final String avatarUrl
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.alias = Objects.requireNonNull(alias, "alias must not be null");
        this.attributes = Objects.requireNonNull(attributes, "attributes must not be null");
        this.authorities = Objects.requireNonNull(authorities, "authorities must not be null");
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
    public String getId() {
        return id;
    }

    @NonNull
    public String getAlias() {
        return alias;
    }
}