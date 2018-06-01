package de.bonndan.nivio.input;

import javax.persistence.*;

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
}
