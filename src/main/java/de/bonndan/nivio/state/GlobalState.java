package de.bonndan.nivio.state;

import de.bonndan.nivio.landscape.FullyQualifiedIdentifier;

import java.util.concurrent.ConcurrentHashMap;

public class GlobalState extends ConcurrentHashMap<FullyQualifiedIdentifier, ServiceState> {

}
