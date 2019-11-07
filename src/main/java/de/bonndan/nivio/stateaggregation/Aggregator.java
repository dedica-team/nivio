package de.bonndan.nivio.stateaggregation;

import com.jasongoodwin.monads.Try;
import de.bonndan.nivio.ProcessingErrorEvent;
import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.model.*;
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

    private final LandscapeRepository landscapeRepository;

    private final ProviderFactory factory;

    private final ApplicationEventPublisher publisher;

    @Autowired
    public Aggregator(LandscapeRepository landscapeRepository,
                      ProviderFactory factory,
                      ApplicationEventPublisher publisher
    ) {
        this.landscapeRepository = landscapeRepository;
        this.factory = factory;
        this.publisher = publisher;
    }

    public void fetch(Landscape landscape) {

        getProviders(landscape).forEach(provider -> {
            try {
                applyUpdates(provider.getStates());
            } catch (Throwable throwable) {
                String msg = "Failed to getStates state using provider " + provider;
                handleError(landscape, throwable, msg);
            }
        });

    }

    private void applyUpdates(Map<FullyQualifiedIdentifier, StatusItem> updates) {
        updates.forEach((fqi, item) -> {
            landscapeRepository.findDistinctByIdentifier(fqi.getLandscape())
                    .ifPresentOrElse(landscape -> {
                                ServiceItems.find(fqi, landscape.getItems()).ifPresent(serviceItem -> serviceItem.setStatus(item));
                            },
                            () -> {
                            }
                    );
        });
    }

    private List<Provider> getProviders(Landscape landscape) {
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

    private void handleError(Landscape landscape, Throwable throwable, String msg) {
        logger.error(msg, throwable);
        publisher.publishEvent(new ProcessingErrorEvent(this, ProcessingException.of(landscape, throwable)));
    }
}

