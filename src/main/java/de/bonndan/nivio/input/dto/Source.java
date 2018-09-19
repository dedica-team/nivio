package de.bonndan.nivio.input.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a source file with its sections.
 */
public class Source {

    public List<ServiceDescription> ingress = new ArrayList<>();
    public List<ServiceDescription> services = new ArrayList<>();
    public List<ServiceDescription> infrastructure = new ArrayList<>();
}
