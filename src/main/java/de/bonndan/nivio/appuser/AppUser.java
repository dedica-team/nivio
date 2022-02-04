package de.bonndan.nivio.appuser;

import javax.persistence.*;


@Entity(name = "AppUser")
@Table(
        name = "appUser",
        uniqueConstraints = {
                @UniqueConstraint(name = "emailUnique",
                        columnNames = "email"),
                @UniqueConstraint(name = "externalIdAndIdProviderUnique",
                        columnNames = {"externalId", "idProvider"})

        }
)
public class AppUser {

    @SequenceGenerator(
            name = "userSequence",
            sequenceName = "userSequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "userSequence"
    )
    @Id
    @Column(
            name = "id",
            nullable = false
    )
    private Long id;

    @Column(
            name = "externalId",
            nullable = false,
            columnDefinition = "VARCHAR"
    )
    private String externalId;

    @Column(
            name = "idProvider",
            nullable = false,
            columnDefinition = "VARCHAR"
    )
    private String idProvider;

    @Column(
            name = "name",
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
            columnDefinition = "VARCHAR"
    )
    private String email;

    @Column(
            name = "avatarUrl",
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

    @Column
    private Boolean locked;

    @Column
    private Boolean enabled;

    public Long getId() {
        return id;
    }

    public String getName() { return name; }

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

    public String getExternalId() {
        return externalId;
    }

    public String getIdProvider() {
        return idProvider;
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

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public void setIdProvider(String idProvider) { this.idProvider = idProvider; }

}
