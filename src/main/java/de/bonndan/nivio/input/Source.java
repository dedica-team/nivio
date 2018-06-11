package de.bonndan.nivio.input;

import javax.persistence.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

@Entity
@Table(name = "sources")
public class Source {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    private String url;

    @ManyToOne
    private Environment environment;

    public Source() {
    }

    public Source(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFullUrl() {
        try {
            URL url = new URL(getUrl());
            return url.toURI().toString();
        } catch (MalformedURLException | URISyntaxException e) {
            File file = new File(environment.getPath());
            return file.getParent() + "/" + getUrl();
        }
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
