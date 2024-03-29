package de.bonndan.nivio.input;

import de.bonndan.nivio.IntegrationTestSupport;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.search.ItemIndex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEvent;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class IndexerIntegrationTest {

    private IntegrationTestSupport integrationTestSupport;

    @BeforeEach
    void setup() {
         integrationTestSupport = new IntegrationTestSupport();
    }

    private Landscape index() {
        return index("/src/test/resources/example/example_env.yml");
    }

    private Landscape index(String path) {
        File file = new File(getRootPath() + path);
        return integrationTestSupport.getFirstIndexedLandscape(file);
    }

    @Test
    void onIndexEvent() {
        Indexer indexer = integrationTestSupport.getIndexer();
        LandscapeDescription test = new LandscapeDescription("test");
        SeedConfiguration configuration = new SeedConfiguration("test");
        IndexEvent event = new IndexEvent(List.of(test), configuration, "");

        //when
        indexer.onIndexEvent(event);

        //then
        ArgumentCaptor<ApplicationEvent> captor = ArgumentCaptor.forClass(ApplicationEvent.class);
        verify(integrationTestSupport.getEventPublisher(), times(2)).publishEvent(captor.capture());
        ProcessingFinishedEvent value = (ProcessingFinishedEvent) captor.getAllValues().get(0);
        assertThat(value.getInput()).isEqualTo(test);

        SeedConfigurationProcessedEvent value2 = (SeedConfigurationProcessedEvent) captor.getAllValues().get(1);
        assertThat(value2.getSource()).isEqualTo(configuration);
    }

    @Test
    void testIndexing() {
        Landscape landscape = index();

        Assertions.assertNotNull(landscape);
        assertEquals("mail@acme.org", landscape.getContact());
        assertTrue(landscape.getDescription().contains("demonstrate"));
        Assertions.assertNotNull(landscape.getItems());
        assertEquals(18, landscape.getItems().all().size());
        Item blog = landscape.getItems().pick("blog-server", null);
        Assertions.assertNotNull(blog);
        assertEquals(3, RelationType.PROVIDER.filter(blog.getRelations()).stream()
                .filter(relationItem1 -> relationItem1.getTarget().equals(blog))
                .map(Relation::getSource)
                .collect(Collectors.toUnmodifiableSet()).size());

        Optional<Item> first = RelationType.PROVIDER.filter(blog.getRelations()).stream()
                .filter(relationItem -> relationItem.getTarget().equals(blog))
                .map(Relation::getSource)
                .collect(Collectors.toUnmodifiableSet()).stream().filter(i -> i.getIdentifier().equals("wordpress-web")).findFirst();
        Item webserver = first.orElseThrow();

        Assertions.assertNotNull(webserver);
        assertEquals(1, RelationType.PROVIDER.filter(webserver.getRelations()).size());

        Relation push = blog.getRelations().stream()
                .filter(d -> "hourly push KPI data".equals(d.getDescription()))
                .findFirst()
                .orElse(null);

        Assertions.assertNotNull(push);

        assertEquals("hourly push KPI data", push.getDescription());
        assertEquals("json", push.getFormat());
        assertEquals(blog.getIdentifier(), push.getSource().getIdentifier());
        assertEquals("nivio:example/dashboard/kpi-dashboard", push.getTarget().getFullyQualifiedIdentifier().toString());

        Set<ServiceInterface> interfaces = blog.getInterfaces();
        assertEquals(3, interfaces.size());
        ServiceInterface i = blog.getInterfaces().stream()
                .filter(d -> d.getDescription().equals("posts"))
                .findFirst()
                .orElseThrow();
        assertEquals("form", i.getFormat());
        assertEquals("http://acme.io/create", i.getUrl().toString());
    }

    @Test //second pass
    void testReIndexing() {
        Landscape landscape = index();

        Assertions.assertNotNull(landscape);
        assertEquals("mail@acme.org", landscape.getContact());
        Assertions.assertNotNull(landscape.getItems());
        assertEquals(18, landscape.getItems().all().size());
        Item blog = landscape.getItems().pick("blog-server", null);
        Assertions.assertNotNull(blog);
        assertEquals(3, RelationType.PROVIDER.filter(blog.getRelations()).stream()
                .filter(relationItem1 -> relationItem1.getTarget().equals(blog))
                .map(Relation::getSource)
                .collect(Collectors.toUnmodifiableSet()).size());

        ArrayList<Item> landscapeItems = new ArrayList<>(RelationType.PROVIDER.filter(blog.getRelations()).stream()
                .filter(relationItem -> relationItem.getTarget().equals(blog))
                .map(Relation::getSource)
                .collect(Collectors.toUnmodifiableSet()));
        ItemIndex<Item> itemIndex = new ItemIndex<>(Item.class);
        itemIndex.setItems(new HashSet<>(landscapeItems));
        Item webserver = itemIndex.pick("wordpress-web", null);
        Assertions.assertNotNull(webserver);
        assertEquals(1, RelationType.PROVIDER.filter(webserver.getRelations()).size());

        Relation push = blog.getRelations().stream()
                .filter(d -> "hourly push KPI data".equals(d.getDescription()))
                .findFirst()
                .orElse(null);

        Assertions.assertNotNull(push);

        assertEquals("hourly push KPI data", push.getDescription());
        assertEquals("json", push.getFormat());
        assertEquals("nivio:example/content/blog-server", push.getSource().getFullyQualifiedIdentifier().toString());
        assertEquals("nivio:example/dashboard/kpi-dashboard", push.getTarget().getFullyQualifiedIdentifier().toString());

        Set<ServiceInterface> interfaces = blog.getInterfaces();
        assertEquals(3, interfaces.size());
        ServiceInterface i = blog.getInterfaces().stream()
                .filter(d -> d.getDescription().equals("posts"))
                .findFirst()
                .orElseThrow();
        assertEquals("form", i.getFormat());
    }

    /**
     * wordpress-web updates must not create new services
     */
    @Test
    void testIncrementalUpdate() {
        Landscape landscape = index();
        Item blog = landscape.getItems().pick("blog-server", null);
        int before = landscape.getItems().all().size();

        LandscapeDescription landscapeDescription = new LandscapeDescription(
                landscape.getIdentifier(), landscape.getName(), null
        );
        landscapeDescription.setIsPartial(true);

        ItemDescription newItem = new ItemDescription();
        newItem.setIdentifier(blog.getIdentifier());
        newItem.setGroup("completelyNewGroup");
        landscapeDescription.getItemDescriptions().add(newItem);

        ItemDescription exsistingWordPress = new ItemDescription();
        exsistingWordPress.setIdentifier("wordpress-web");
        exsistingWordPress.setName("Other name");
        landscapeDescription.getItemDescriptions().add(exsistingWordPress);


        //created
        integrationTestSupport.getIndexer().index(landscapeDescription);
        landscape = integrationTestSupport.getLandscapeRepository().findDistinctByIdentifier(landscapeDescription.getIdentifier()).orElseThrow();
        blog = landscape.getItems().pick("blog-server", "completelyNewGroup");
        assertEquals("completelyNewGroup", blog.getGroup());
        assertEquals(before + 1, landscape.getItems().all().size());

        //updated
        Item wordpress = landscape.getItems().pick("wordpress-web", "content");
        assertEquals("Other name", wordpress.getName());
        assertEquals("content", wordpress.getGroup());

        //testing changelog
        ArgumentCaptor<ProcessingFinishedEvent> captor = ArgumentCaptor.forClass(ProcessingFinishedEvent.class);
        verify(integrationTestSupport.getEventPublisher(), times(2)).publishEvent(captor.capture());
        ProcessingFinishedEvent value = captor.getAllValues().get(1);
        assertThat(value).isNotNull();
        ProcessingChangelog changelog = value.getChangelog();
        assertThat(changelog).isNotNull();
        assertThat(changelog.getChanges()).hasSize(3);
        assertThat(changelog.getChanges()).containsKey("nivio:example/content/wordpress-web");
    }

    /**
     * Ensures that same names in different landscapes do not collide
     */
    @Test
    void testNameConflictDifferentLandscapes() {
        Landscape landscape1 = index("/src/test/resources/example/example_env.yml");
        Landscape landscape2 = index("/src/test/resources/example/example_other.yml");

        Assertions.assertNotNull(landscape1);
        assertEquals("mail@acme.org", landscape1.getContact());
        Assertions.assertNotNull(landscape1.getItems());
        Item blog1 = landscape1.getItems().pick("blog-server", null);
        Assertions.assertNotNull(blog1);
        assertEquals("blog", blog1.getLabel(Label.shortname));

        Assertions.assertNotNull(landscape2);
        assertEquals("nivio:other", landscape2.getIdentifier());
        assertEquals("mail@other.org", landscape2.getContact());
        Assertions.assertNotNull(landscape2.getItems());
        Item blog2 = landscape2.getItems().pick("blog-server", null);
        Assertions.assertNotNull(blog2);
        assertEquals("blog1", blog2.getLabel(Label.shortname));
    }

    /**
     * Ensures that same names in different landscapes do not collide
     */
    @Test
    void testDataflow() {
        Landscape landscape1 = index("/src/test/resources/example/example_dataflow.yml");

        Assertions.assertNotNull(landscape1);
        Assertions.assertNotNull(landscape1.getItems());
        Item blog1 = landscape1.getItems().pick("blog-server", "content1");
        Assertions.assertNotNull(blog1);
        Item blog2 = landscape1.getItems().pick("blog-server", "content2");
        Assertions.assertNotNull(blog2);
        assertEquals("Demo Blog", blog1.getName());
        assertEquals(
                FullyQualifiedIdentifier.build("nivio:dataflowtest", "content1", "blog-server").toString(),
                blog1.toString()
        );

        assertNotNull(blog1.getRelations());
        assertEquals(2, blog1.getRelations().size());
    }

    @Test
    void environmentTemplatesApplied() {
        Landscape landscape = index("/src/test/resources/example/example_templates.yml");

        Item web = landscape.getItems().pick("web", null);
        assertNotNull(web);
        assertEquals("web", web.getIdentifier());
        assertEquals("webservice", web.getType());
    }

    @Test
    void readGroups() {
        Landscape landscape1 = index("/src/test/resources/example/example_env.yml");
        Map<String, Group> groups = landscape1.getGroups();
        assertTrue(groups.containsKey("content"));
        Group content = groups.get("content");
        assertThat(content.getItems()).isNotEmpty();
        assertEquals(3, content.getItems().size());

        assertTrue(groups.containsKey("ingress"));
        Group ingress = groups.get("ingress");
        assertFalse(ingress.getItems().isEmpty());
        assertEquals(1, ingress.getItems().size());
    }

    @Test
    void readGroupsContains() {
        Landscape landscape1 = index("/src/test/resources/example/example_groups.yml");
        Group a = landscape1.getGroups().get("groupA");
        ItemIndex<Item> index = new ItemIndex<>(Item.class);
        index.setItems(new HashSet<>(landscape1.getItems().retrieve(a.getItems())));

        assertNotNull(index.pick("blog-server", null));
        assertNotNull(index.pick("crappy_dockername-234234", null));
    }

    @Test
    void masksSecrets() {
        Landscape landscape1 = index("/src/test/resources/example/example_secret.yml");
        Optional<Item> abc = landscape1.getItems().find("abc", null);
        assertThat(abc).isNotEmpty();
        Item item = abc.get();
        assertThat(item.getLabel("key")).isEqualTo(SecureLabelsResolver.MASK);
        assertThat(item.getLabel("password")).isEqualTo(SecureLabelsResolver.MASK);
        assertThat(item.getLabel("foo_url")).isEqualTo("https://*@foobar.com");
    }

    @Test
    void labelRelations() {
        Landscape landscape = index("/src/test/resources/example/example_label_relations.yml");
        assertEquals(3, landscape.getGroups().size()); //common group is present by default
        assertEquals(2, landscape.getItems().all().size());

        Item foo = landscape.getItems().all().iterator().next();
        assertEquals("foo", foo.getIdentifier());
        assertEquals(1, foo.getRelations().size());
    }

    private String getRootPath() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }
}
