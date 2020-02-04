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

        LandscapeImpl landscape = getLandscapeWith(elastic);

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

        LandscapeImpl landscape = getLandscapeWith(elastic);

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

        LandscapeImpl landscape = getLandscapeWith(api);

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

        LandscapeImpl landscape = getLandscapeWith(api);

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
        LandscapeImpl landscape = getLandscapeWith(api);

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
    @DisplayName("does nothing if the label value is a known identifier")
    public void ifValueIsIdentiferDoesNotLink() {
        //given
        Item apiFoo = new Item();
        apiFoo.setIdentifier("api-foo"); //this should match
        LandscapeImpl landscape = getLandscapeWith(apiFoo);

        Item foo = new Item();
        foo.setIdentifier("foo"); //part of the label "FOO_API_URL", should not match
        landscape.addItem(foo);

        Item api = new Item();
        api.setIdentifier("api"); //part of the label "FOO_API_URL", should not match
        landscape.addItem(api);


        //when
        resolver.process(input, landscape);

        //then
        assertEquals(2, item.getRelations().size()); // to api because of ABC_URL, to api-foo because of FOO_API_URL

        boolean hasApiFoo = item.getRelations().stream().anyMatch(rel -> "api-foo".equals(rel.getTarget()));
        assertTrue(hasApiFoo);
    }

    @Test
    @DisplayName("a part of the key matches an identifier")
    public void keyPartMatchesidentifier() {
        //given
        Item hihi = new Item();
        hihi.setIdentifier("baz");
        LandscapeImpl landscape = getLandscapeWith(hihi);

        //when
        resolver.process(input, landscape);

        //then
        assertEquals(1, item.getRelations().size());
        RelationItem<String> rel = item.getRelations().iterator().next();
        assertEquals("baz", rel.getTarget());
    }

    @Test
    @DisplayName("label blacklist is used")
    public void blacklistPreventsRelations() {
        //given
        Item hihi = new Item();
        hihi.setIdentifier("baz");
        LandscapeImpl landscape = getLandscapeWith(hihi);
        landscape.getConfig().getLabelBlacklist().add(".*COMPOSITION.*");

        //when
        resolver.process(input, landscape);

        //then
        assertEquals(0, item.getRelations().size());
    }

    @Test
    @DisplayName("does not link same service to itself")
    public void doesNotLinkSame() {
        //given
        Item hihi = new Item();
        hihi.setIdentifier(IDENTIFIER);

        item.getLabels().put("BASE_URL", IDENTIFIER);
        LandscapeImpl landscape = getLandscapeWith(hihi);

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
        LandscapeImpl landscape = getLandscapeWith(db);

        //when
        resolver.process(input, landscape);

        //then
        assertEquals(1, item.getRelations().size());
        RelationItem<String> rel = item.getRelations().iterator().next();
        assertEquals("x.y.z", rel.getSource());
        assertEquals("x.y.z", rel.getSource());
        assertEquals(RelationType.PROVIDER, rel.getType());
    }


    private LandscapeImpl getLandscapeWith(Item target) {
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