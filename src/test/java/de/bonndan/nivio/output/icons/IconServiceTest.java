package de.bonndan.nivio.output.icons;

import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class IconServiceTest {

    private LocalIcons localIcons;
    private ExternalIcons externalIcons;
    private IconService iconService;

    @BeforeEach
    public void setup() {
        externalIcons = mock(ExternalIcons.class);
        localIcons = new LocalIcons();
        iconService = new IconService(localIcons, externalIcons);
    }


    @Test
    public void returnsServiceWithUnknownType() {
        Item item = getTestItem("test", "a");
        item.setLabel(Label.type, "asb");

        String icon = localIcons.getIconUrl(IconMapping.DEFAULT_ICON.getIcon()).orElseThrow();
        assertThat(iconService.getIconUrl(getTestItem("test", "a"))).isEqualTo(icon);

    }

    @Test
    public void returnsType() {
        Item item = getTestItem("test", "a");
        item.setLabel(Label.type, "account");

        String icon = localIcons.getIconUrl("account").orElseThrow();
        assertThat(iconService.getIconUrl(item)).isEqualTo(icon);
    }

    @Test
    public void returnsCustomIcon() {
        Item item = getTestItem("test", "a");
        item.setLabel(Label.icon, "http://my.icon");

        assertThat(iconService.getIconUrl(item)).isEqualTo("http://my.icon");
    }

    @Test
    public void returnsVendorIcon() throws MalformedURLException {
        Item item = getTestItem("test", "a");
        item.setLabel(Label.icon, "vendor://redis");

        URL url = new URL("http://foo.com/bar.png");
        when(externalIcons.getUrl(eq("redis"))).thenReturn(Optional.of(url.toString()));

        //when
        String s = iconService.getIconUrl(item);
        verify(externalIcons).getUrl(eq("redis"));
        assertEquals(url.toString(), s);
    }

    @Test
    public void getFillUrl() throws MalformedURLException {
        when(externalIcons.getUrl(any(URL.class))).thenReturn(Optional.empty());

        Optional<String> fillUrl = iconService.getExternalUrl(new URL("http://my.icon"));
        assertThat(fillUrl).isEmpty();
        verify(externalIcons).getUrl(any(URL.class));
    }

}