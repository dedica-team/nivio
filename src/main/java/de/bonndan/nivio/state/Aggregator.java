package de.bonndan.nivio.state;

import com.jasongoodwin.monads.Try;
import de.bonndan.nivio.ProcessingErrorEvent;
import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.landscape.FullyQualifiedIdentifier;
import de.bonndan.nivio.landscape.LandscapeInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class Aggregator {

    private final static Logger logger = LoggerFactory.getLogger(Aggregator.class);

    private final Map<FullyQualifiedIdentifier, ServiceState> state;

    private final ProviderFactory factory;

    private final ApplicationEventPublisher publisher;

    @Autowired
    public Aggregator(Map<FullyQualifiedIdentifier, ServiceState> state, ProviderFactory factory, ApplicationEventPublisher publisher) {
        this.state = state;
        this.factory = factory;
        this.publisher = publisher;
    }

    public void fetch(LandscapeInterface landscape) {

        getProviders(landscape).forEach(provider -> {
            try {
                applyUpdates(provider.getStates());
            } catch (Throwable throwable) {
                String msg = "Failed to getStates state using provider " + provider;
                handleError(landscape, throwable, msg);
            }
        });

    }

    private void applyUpdates(Map<FullyQualifiedIdentifier, ServiceState> updates) {
        updates.forEach((fqi, serviceState) -> {

            if (!state.containsKey(fqi)) {
                state.put(fqi, serviceState);
                return;
            }

            ServiceState current = state.get(fqi);
            if (!current.getLevel().equals(serviceState.getLevel())) {
                publisher.publishEvent(new ServiceStateChangeEvent(this, fqi, current, serviceState));
            }

            state.put(fqi, serviceState);
        });
    }

    private List<Provider> getProviders(LandscapeInterface landscape) {
        List<Provider> providers = new ArrayList<>();
        landscape.getStateProviders()
                .forEach(config ->
                        Try.ofFailable(() -> factory.createFor(landscape, config))
                                .onSuccess(providers::add)
                                .onFailure(throwable -> handleError(landscape, throwable, "Failed to create state provider"))
                                .toOptional()
                );

        return providers;
    }

    private void handleError(LandscapeInterface landscape, Throwable throwable, String msg) {
        logger.error(msg, throwable);
        publisher.publishEvent(new ProcessingErrorEvent(this, ProcessingException.of(landscape, throwable)));
    }
}

