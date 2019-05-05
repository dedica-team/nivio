package de.bonndan.nivio.output;

import de.bonndan.nivio.landscape.Service;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IconsTest {

    @Test
    public void returnsServiceAsDefault() {
        assertTrue(iconUrlContains("service", Icons.getIcon(new Service())));
    }

    @Test
    public void returnsServiceWithUnknownType() {
        Service service = new Service();
        service.setType("asb");
        assertTrue(iconUrlContains("service", Icons.getIcon(service)));
    }

    @Test
    public void returnsType() {
        Service service = new Service();
        service.setType("firewall");
        assertTrue(iconUrlContains("firewall", Icons.getIcon(service)));
    }

    @Test
    public void returnsTypeIgnoreCase() {
        Service service = new Service();
        service.setType("FireWall");
        assertTrue(iconUrlContains("firewall", Icons.getIcon(service)));
    }

    @Test
    public void returnsIcon() {
        Service service = new Service();
        service.setIcon("http://my.icon");
        assertEquals("http://my.icon", Icons.getIcon(service).getUrl().toString());
    }

    @Test
    public void isCustom() {
        Service service = new Service();
        service.setIcon("http://my.icon");
        assertTrue(Icons.getIcon(service).isCustom());
    }

    private boolean iconUrlContains(String part, Icon icon) {
        return icon.getUrl().toString().contains(part);
    }
}