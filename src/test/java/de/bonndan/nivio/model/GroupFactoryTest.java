package de.bonndan.nivio.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GroupFactoryTest {


    @Test
    public void testMerge() {
        Group one = new Group("a");
        one.setColor("#123123");
        one.setDescription("a");
        one.setOwner("Joe");

        Group two = new Group("a");
        two.setOwner("Matt");
        two.setContact("mail");

        GroupFactory.merge(one, two);

        assertEquals("Joe", one.getOwner());
        assertEquals("a", one.getDescription());
        assertEquals("mail", one.getContact());
        assertEquals("#123123", one.getColor());
    }
}
