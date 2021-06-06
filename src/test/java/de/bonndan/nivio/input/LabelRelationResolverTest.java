package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LabelRelationResolverTest {

    private LabelRelationResolver resolver;
    private HintFactory hintFactory;

    @BeforeEach
    public void setup() {
        hintFactory = mock(HintFactory.class);
        resolver = new LabelRelationResolver(new ProcessLog(mock(Logger.class), "test"), hintFactory);
    }

    @Test
    @DisplayName("label blacklist is used")
    public void blacklistPreventsRelations() {
        //given
        LandscapeDescription landscape = new LandscapeDescription("identifier");
        landscape.getConfig().getLabelBlacklist().add(".*COMPOSITION.*");

        ItemDescription hihi = new ItemDescription( "bar");
        hihi.setLabel("BAZ_COMPOSITION_URL", "http://baz-composition-service:80");

        ItemDescription target = new ItemDescription( "baz");
        target.setAddress("http://baz-composition-service:80");

        landscape.setItems(List.of(hihi, target));

        //when
        resolver.resolve(landscape);

        //then
        assertEquals(0, hihi.getRelations().size());
    }

    @Test
    @DisplayName("label blacklist is used case insensitive")
    public void blacklistPreventsRelationsCaseInsensitive() {
        //given
        LandscapeDescription landscape = new LandscapeDescription("identifier");
        landscape.getConfig().getLabelBlacklist().add(".*COMPOSITION.*");

        ItemDescription hihi = new ItemDescription( "bar");
        hihi.setLabel("BAZ_composition_URL", "http://baz-composition-service:80");

        ItemDescription target = new ItemDescription( "baz");
        target.setAddress("http://baz-composition-service:80");

        landscape.setItems(List.of(hihi, target));

        //when
        resolver.resolve(landscape);

        //then
        assertEquals(0, hihi.getRelations().size());
    }

    @Test
    void ignoresLinks() {

        //given
        ItemDescription db = new ItemDescription("x.y.z");
        db.setLabel(LabelToFieldResolver.LINK_LABEL_PREFIX + "foo", "http://foo.bar.baz");
        LandscapeDescription landscape = new LandscapeDescription("identifier");
        landscape.setItems(List.of(db));

        //when
        resolver.resolve(landscape);

        //then
        assertThat(landscape.getItemDescriptions().all().size()).isEqualTo(1);
        verify(hintFactory, never()).createForLabel(eq(landscape), any(), any());
    }


    private ItemDescription getItemWithLabels() {
        ItemDescription itemDescription = new ItemDescription();
        itemDescription.setIdentifier("foo");

        itemDescription.getLabels().putAll(
                Map.of(
                        "ACTUATOR_PORT", "8081",
                        "APP_VERSION", "develop@abc123ef",
                        "ABC_URL", "https://test.com/abc/api/v1",
                        "DB_SERVER_DATABASE", "abc_dev1",
                        "DB_SERVER_HOST", "x.y.z:3024",
                        "DB_SERVER_MIGRATION_PASSWORD", "daniel",
                        "DB_SERVER_MIGRATION_USER", "user",
                        "DB_SERVER_PASSWORD", "",
                        "DB_SERVER_USER", "user",
                        "DEPLOYMENT_ENVIRONMENT", "dev"
                )
        );

        itemDescription.getLabels().putAll(
                Map.of(
                        "DEPLOYMENT_URL", "https://hihi/huhu ",
                        "ELASTIC_URL", "https://elastic:9200",
                        "KEYCLOAK_CLIENT_ID", "clientID",
                        "KEYCLOAK_CLIENT_SECRET", "xxxyyyzzz",
                        "KEYCLOAK_ISSUER_URI", "https://keycloak.com/auth/realms/harrr/protocol/openid-connect/token",
                        "BAZ_COMPOSITION_URL", "http://baz-composition-service:80",
                        "SERVER_PORT", "80",
                        "FOO_API_PASSWORD", "somesecret",
                        "FOO_API_URL", "api-foo",
                        "FOO_API_USER", "user"
                )
        );

        itemDescription.getLabels().putAll(
                Map.of(
                        "STACK", "abc",
                        "STAGE", "dev"
                )
        );

        return itemDescription;
    }
}