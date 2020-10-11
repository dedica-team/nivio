package de.bonndan.nivio.output.icons;

import de.bonndan.nivio.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class LocalIconsTest {

    private LocalIcons localServer;
    private VendorIcons vendorIcons;

    @BeforeEach
    public void setup() {
        vendorIcons = mock(VendorIcons.class);
        localServer = new LocalIcons(vendorIcons);
    }

    @Test
    public void encodesBase64DataUrls() {
        String icon = localServer.getIconUrl(IconMapping.DEFAULT_ICON.getIcon(), false);
        assertThat(icon).isNotBlank();

        String payload = icon.replace(DataUrlHelper.DATA_IMAGE_SVG_XML_BASE_64, "");
        String decoded = new String(Base64.getDecoder().decode(payload));
        assertThat(decoded).contains("xml");
    }

    @Test
    public void returnsServiceAsDefault() {
        String icon = localServer.getIconUrl(IconMapping.DEFAULT_ICON.getIcon(), false);
        assertThat(icon).isNotBlank();
        assertThat(localServer.getIconUrl(new Item("test", "a"))).isEqualTo(icon);
    }

    @Test
    public void returnsServiceWithUnknownType() {
        Item item = new Item("test", "a");
        item.setType("asb");

        String icon = localServer.getIconUrl(IconMapping.DEFAULT_ICON.getIcon(), false);
        assertThat(localServer.getIconUrl(new Item("test", "a"))).isEqualTo(icon);

    }

    @Test
    public void returnsType() {
        Item item = new Item("test", "a");
        item.setType("account");

        String icon = localServer.getIconUrl("account", false);
        assertThat(localServer.getIconUrl(item)).isEqualTo(icon);
    }

    @Test
    public void returnsTypeIgnoreCase() {
        Item item = new Item("test", "a");
        item.setType("AccOunT");

        String icon = localServer.getIconUrl("account", false);
        assertThat(localServer.getIconUrl(item)).isEqualTo(icon);
    }

    @Test
    public void returnsIcon() {
        Item item = new Item("test", "a");
        item.setIcon("http://my.icon");
        String s = localServer.getIconUrl(item);
        assertEquals("http://my.icon", s);
    }

    @Test
    public void returnsVendorIcon() throws MalformedURLException {
        Item item = new Item("test", "a");
        item.setIcon("vendor://redis");

        URL url = new URL("http://foo.com/bar.png");
        when(vendorIcons.getUrl(eq("redis"))).thenReturn(Optional.of(url.toString()));

        //when
        String s = localServer.getIconUrl(item);
        verify(vendorIcons).getUrl(eq("redis"));
        assertEquals(url.toString(), s);
    }

}