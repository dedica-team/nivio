package de.bonndan.nivio.landscape;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.net.URL;

@Entity
public class ServiceInterface implements InterfaceItem {

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    @ManyToOne
    @JsonBackReference
    private Service service;

    private String description;
    private String format;
    private URL url;

    public ServiceInterface() {

    }

    public ServiceInterface(InterfaceItem interfaceItem) {
        this.description = interfaceItem.getDescription();
        this.format = interfaceItem.getFormat();
        this.url = interfaceItem.getUrl();
    }

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }
}
