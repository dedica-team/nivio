package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class MagicLabelRelationsTest {

    public static final String IDENTIFIER = "anItemWithLabels";
    private ItemDescription item;
    private LandscapeDescription input;
    private MagicLabelRelations resolver;

    @BeforeEach
    public void setup() {
        item = getItemWithLabels();
        input = new LandscapeDescription();
        input.getItemDescriptions().add(item);

        resolver = new MagicLabelRelations(new ProcessLog(mock(Logger.class)));
    }

    @Test
    @DisplayName("key contains url in name and is url with hostname matching a name")
    public void findFromLabelUrlHostnameByName() {

        //given
        Item elastic = new Item();
        elastic.setIdentifier("elastic-server-as89");
        elastic.setName("elastic");

        LandscapeImpl landscape = getLandscape(elastic);

        //when
        resolver.process(input, landscape);

        //then
        assertEquals(1, item.getRelations().size());
        RelationItem<String> rel = item.getRelations().iterator().next();
        assertEquals("elastic-server-as89", rel.getTarget());
    }

    @Test
    @DisplayName("key contains url in name and is url with hostname matching an identifier")
    public void findFromLabelUrlHostnameByIdentifier() {
        //given
        Item elastic = new Item();
        elastic.setIdentifier("elastic");

        LandscapeImpl landscape = getLandscape(elastic);

        //when
        resolver.process(input, landscape);

        //then
        assertEquals(1, item.getRelations().size());
        RelationItem<String> rel = item.getRelations().iterator().next();
        assertEquals("elastic", rel.getTarget());
    }

    @Test
    @DisplayName("key has magic part but value is not a url")
    public void findFromLabelValueByIdentifier() {
        //given
        Item api = new Item();
        api.setIdentifier("api-foo");

        LandscapeImpl landscape = getLandscape(api);

        //when
        resolver.process(input, landscape);

        //then
        assertEquals(1, item.getRelations().size());
        RelationItem<String> rel = item.getRelations().iterator().next();
        assertEquals("api-foo", rel.getTarget());
    }

    @Test
    @DisplayName("key has magic part but value is not a url")
    public void findFromLabelValueByName() {
        //given
        Item api = new Item();
        api.setIdentifier("api.foo.123");
        api.setName("api-foo");

        LandscapeImpl landscape = getLandscape(api);

        //when
        resolver.process(input, landscape);

        //then
        assertEquals(1, item.getRelations().size());
        RelationItem<String> rel = item.getRelations().iterator().next();
        assertEquals("api.foo.123", rel.getTarget());
    }

    @Test
    @DisplayName("does nothing with more than one match")
    public void ifUncertainDoesNotLink() {
        //given
        Item api = new Item();
        api.setIdentifier("api.foo.123");
        api.setName("api-foo");
        LandscapeImpl landscape = getLandscape(api);

        Item api2 = new Item();
        api2.setIdentifier("api.foo.234");
        api2.setName("api-foo");
        landscape.addItem(api2);


        //when
        resolver.process(input, landscape);

        //then
        assertEquals(0, item.getRelations().size());

    }

    @Test
    @DisplayName("a part of the key matches an identifier")
    public void keyPartMatchesidentifier() {
        //given
        Item hihi = new Item();
        hihi.setIdentifier("baz");
        LandscapeImpl landscape = getLandscape(hihi);

        //when
        resolver.process(input, landscape);

        //then
        assertEquals(1, item.getRelations().size());
        RelationItem<String> rel = item.getRelations().iterator().next();
        assertEquals("baz", rel.getTarget());
    }

    @Test
    @DisplayName("does not link same service to itself")
    public void doesNotLinkSame() {
        //given
        Item hihi = new Item();
        hihi.setIdentifier(IDENTIFIER);

        input.getItemDescriptions().get(0).getLabels().put("BASE_URL", IDENTIFIER);
        LandscapeImpl landscape = getLandscape(hihi);

        //when
        resolver.process(input, landscape);

        //then
        assertEquals(0, item.getRelations().size());
    }

    @Test
    public void hasProviderRelation() {
        //given
        Item db = new Item();
        db.setIdentifier("x.y.z");
        LandscapeImpl landscape = getLandscape(db);

        //when
        resolver.process(input, landscape);

        //then
        assertEquals(1, item.getRelations().size());
        RelationItem<String> rel = item.getRelations().iterator().next();
        assertEquals("x.y.z", rel.getSource());
        assertEquals("x.y.z", rel.getSource());
        assertEquals(RelationType.PROVIDER, rel.getType());
    }


    private LandscapeImpl getLandscape(Item target) {
        LandscapeImpl landscape = new LandscapeImpl();
        landscape.addItem(target);
        return landscape;
    }

    private ItemDescription getItemWithLabels() {
        ItemDescription itemDescription = new ItemDescription();
        itemDescription.setIdentifier(IDENTIFIER);

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