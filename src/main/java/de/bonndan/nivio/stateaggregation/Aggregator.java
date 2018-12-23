package de.bonndan.nivio.stateaggregation;

import com.jasongoodwin.monads.Try;
import de.bonndan.nivio.ProcessingErrorEvent;
import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.landscape.FullyQualifiedIdentifier;
import de.bonndan.nivio.landscape.LandscapeItem;
import de.bonndan.nivio.landscape.ServiceRepository;
import de.bonndan.nivio.landscape.StatusItem;
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

    private final ServiceRepository serviceRepo;

    private final ProviderFactory factory;

    private final ApplicationEventPublisher publisher;

    @Autowired
    public Aggregator(ServiceRepository serviceRepo, ProviderFactory factory, ApplicationEventPublisher publisher) {
        this.serviceRepo = serviceRepo;
        this.factory = factory;
        this.publisher = publisher;
    }

    public void fetch(LandscapeItem landscape) {

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
            serviceRepo.findByLandscapeAndGroupAndIdentifier(fqi.getLandscape(), fqi.getGroup(), fqi.getIdentifier())
                    .ifPresent(service -> service.setStatus(item));
        });
    }

    private List<Provider> getProviders(LandscapeItem landscape) {
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

    private void handleError(LandscapeItem landscape, Throwable throwable, String msg) {
        logger.error(msg, throwable);
        publisher.publishEvent(new ProcessingErrorEvent(this, ProcessingException.of(landscape, throwable)));
    }
}

