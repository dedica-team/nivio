package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.output.LocalServer;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IndexerIntegrationTest {

    @Autowired
    LandscapeRepository landscapeRepository;

    @Autowired
    ItemDescriptionFormatFactory formatFactory;

    @Autowired
    LandscapeDescriptionFactory landscapeDescriptionFactory;

    @Autowired
    LocalServer localServer;

    @Mock
    ApplicationEventPublisher applicationEventPublisher;

    private LandscapeImpl index() {
        return index("/src/test/resources/example/example_env.yml");
    }

    private LandscapeImpl index(String path) {
        File file = new File(getRootPath() + path);
        LandscapeDescription landscapeDescription = landscapeDescriptionFactory.fromYaml(file);

        Indexer indexer = new Indexer(landscapeRepository, formatFactory, applicationEventPublisher, localServer);

        ProcessLog processLog = indexer.reIndex(landscapeDescription);
        return (LandscapeImpl) processLog.getLandscape();
    }

    @Test //first pass
    public void testIndexing() {
        LandscapeImpl landscape = index();

        Assertions.assertNotNull(landscape);
        assertEquals("mail@acme.org", landscape.getContact());
        assertTrue(landscape.getDescription().contains("demonstrate"));
        Assertions.assertNotNull(landscape.getItems());
        assertEquals(8, landscape.getItems().all().size());
        Item blog = landscape.getItems().pick("blog-server", null);
        Assertions.assertNotNull(blog);
        assertEquals(3, blog.getProvidedBy().size());

        Optional<Item> first = blog.getProvidedBy().stream().filter(i -> i.getIdentifier().equals("wordpress-web")).findFirst();
        Item webserver = first.orElseThrow();

        Assertions.assertNotNull(webserver);
        assertEquals(1, webserver.getRelations(RelationType.PROVIDER).size());

        Relation push = (Relation) blog.getRelations().stream()
                .filter(d -> "push".equals(d.getDescription()))
                .findFirst()
                .orElse(null);

        Assertions.assertNotNull(push);

        assertEquals("push", push.getDescription());
        assertEquals("json", push.getFormat());
        assertEquals(blog.getIdentifier(), push.getSource().getIdentifier());
        assertEquals("nivio:example/dashboard/kpi-dashboard", push.getTarget().getFullyQualifiedIdentifier().toString());

        Set<InterfaceItem> interfaces = blog.getInterfaces();
        assertEquals(3, interfaces.size());
        InterfaceItem i = blog.getInterfaces().stream()
                .filter(d -> d.getDescription().equals("posts"))
                .findFirst()
                .orElseThrow();
        assertEquals("form", i.getFormat());
        assertEquals("http://acme.io/create", i.getUrl().toString());
    }

    @Test //second pass
    public void testReIndexing() {
        LandscapeImpl landscape = index();

        Assertions.assertNotNull(landscape);
        assertEquals("mail@acme.org", landscape.getContact());
        Assertions.assertNotNull(landscape.getItems());
        assertEquals(8, landscape.getItems().all().size());
        Item blog = landscape.getItems().pick("blog-server", null);
        Assertions.assertNotNull(blog);
        assertEquals(3, blog.getProvidedBy().size());

        LandscapeItems items = new LandscapeItems();
        items.setItems(blog.getProvidedBy());
        Item webserver = items.pick("wordpress-web", null);
        Assertions.assertNotNull(webserver);
        assertEquals(1, webserver.getRelations(RelationType.PROVIDER).size());

        Relation push = (Relation) blog.getRelations().stream()
                .filter(d -> "push".equals(d.getDescription()))
                .findFirst()
                .orElse(null);

        Assertions.assertNotNull(push);

        assertEquals("push", push.getDescription());
        assertEquals("json", push.getFormat());
        assertEquals("nivio:example/content/blog-server", push.getSource().getFullyQualifiedIdentifier().toString());
        assertEquals("nivio:example/dashboard/kpi-dashboard", push.getTarget().getFullyQualifiedIdentifier().toString());

        Set<InterfaceItem> interfaces = blog.getInterfaces();
        assertEquals(3, interfaces.size());
        InterfaceItem i = blog.getInterfaces().stream()
                .filter(d -> d.getDescription().equals("posts"))
                .findFirst()
                .orElseThrow();
        assertEquals("form", i.getFormat());
    }

    /**
     * wordpress-web updates must not create new services
     */
    @Test
    public void testIncrementalUpdate() {
        LandscapeImpl landscape = index();
        Item blog = landscape.getItems().pick("blog-server", null);
        int before = landscape.getItems().all().size();

        LandscapeDescription landscapeDescription = new LandscapeDescription();
        landscapeDescription.setIdentifier(landscape.getIdentifier());
        landscapeDescription.setIsPartial(true);

        ItemDescription newItem = new ItemDescription();
        newItem.setIdentifier(blog.getIdentifier());
        newItem.setGroup("completelyNewGroup");
        landscapeDescription.getItemDescriptions().add(newItem);

        ItemDescription exsistingWordPress = new ItemDescription();
        exsistingWordPress.setIdentifier("wordpress-web");
        exsistingWordPress.setName("Other name");
        landscapeDescription.getItemDescriptions().add(exsistingWordPress);

        Indexer indexer = new Indexer(landscapeRepository, formatFactory, applicationEventPublisher, localServer);

        //created
        landscape = (LandscapeImpl) indexer.reIndex(landscapeDescription).getLandscape();
        blog = (Item) landscape.getItems().pick("blog-server", "completelyNewGroup");
        assertEquals("completelyNewGroup", blog.getGroup());
        assertEquals(before + 1, landscape.getItems().all().size());

        //updated
        Item wordpress = (Item) landscape.getItems().pick("wordpress-web", "content");
        assertEquals("Other name", wordpress.getName());
        assertEquals("content", wordpress.getGroup());


    }

    /**
     * Ensures that same names in different landscapes do not collide
     */
    @Test
    public void testNameConflictDifferentLandscapes() {
        LandscapeImpl landscape1 = index("/src/test/resources/example/example_env.yml");
        LandscapeImpl landscape2 = index("/src/test/resources/example/example_other.yml");

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
    public void testDataflow() {
        LandscapeImpl landscape1 = index("/src/test/resources/example/example_dataflow.yml");

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
    public void environmentTemplatesApplied() {
        LandscapeImpl landscape = index("/src/test/resources/example/example_templates.yml");

        LandscapeItem web = landscape.getItems().pick("web", null);
        Assert.assertNotNull(web);
        assertEquals("web", web.getIdentifier());
        assertEquals("webservice", web.getType());
    }

    @Test
    public void readGroups() {
        LandscapeImpl landscape1 = index("/src/test/resources/example/example_env.yml");
        Map<String, GroupItem> groups = landscape1.getGroups();
        assertTrue(groups.containsKey("content"));
        Group content = (Group) groups.get("content");
        assertFalse(content.getItems().isEmpty());
        assertEquals(3, content.getItems().size());

        assertTrue(groups.containsKey("ingress"));
        Group ingress = (Group) groups.get("ingress");
        assertFalse(ingress.getItems().isEmpty());
        assertEquals(1, ingress.getItems().size());
    }

    @Test
    public void readGroupsContains() {
        LandscapeImpl landscape1 = index("/src/test/resources/example/example_groups.yml");
        Group a = (Group) landscape1.getGroups().get("groupA");
        LandscapeItems items = new LandscapeItems();
        items.setItems(a.getItems());
        assertNotNull(items.pick("blog-server", null));
        assertNotNull(items.pick("crappy_dockername-234234", null));
    }

    @Test
    public void labelRelations() {
        LandscapeImpl landscape = index("/src/test/resources/example/example_label_relations.yml");
        assertEquals(2, landscape.getGroups().size()); //common group is present by default
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
