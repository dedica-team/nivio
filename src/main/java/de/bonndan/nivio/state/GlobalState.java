package de.bonndan.nivio.state;

import de.bonndan.nivio.landscape.FullyQualifiedIdentifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class GlobalState extends ConcurrentHashMap<FullyQualifiedIdentifier, ServiceState> {

}
