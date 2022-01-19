package de.bonndan.nivio.appuser;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;


@Entity(name = "AppUser")
@Table(
        name = "user",
        uniqueConstraints = {
                @UniqueConstraint(name = "appuser_email_unique",
                        columnNames = "email")
        }
)
public class AppUser implements UserDetails {

    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_sequence"
    )
    @Id
    @Column(
            name = "id",
            nullable = false
    )
    private Long id;

    @Column(
            name = "name",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String name;


    @Column(
            name = "alias",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String alias;

    @Column(
            name = "email",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String email;

    @Column(
            name = "avatar_url",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String avatarUrl;


    @Column(
            name = "role",
            nullable = false,
            columnDefinition = "TEXT"
    )
    @Enumerated(EnumType.STRING)
    private AppUserRole appUserRole;

    private Boolean locked;
    private Boolean enabled;

    public AppUser(String name, String alias, String email, String avatarUrl, AppUserRole appUserRole) {
        this.name = name;
        this.alias = alias;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.appUserRole = appUserRole;
    }

    public AppUser() {

    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(appUserRole.name());
        return Collections.singletonList(authority);
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return alias;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAlias() {
        return alias;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public AppUserRole getAppUserRole() {
        return appUserRole;
    }

    public Boolean getLocked() {
        return locked;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setAppUserRole(AppUserRole appUserRole) {
        this.appUserRole = appUserRole;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}