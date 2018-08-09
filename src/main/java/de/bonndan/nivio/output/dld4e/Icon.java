package de.bonndan.nivio.output.dld4e;

import de.bonndan.nivio.landscape.Service;

public class Icon extends DiagramItem {

    private final Service service;

    public Icon (Service service) {
        this.service = service;
    }

    public Service getService() {
        return service;
    }
}
