package de.bonndan.nivio.state;

import com.jasongoodwin.monads.Try;
import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.landscape.FullyQualifiedIdentifier;
import de.bonndan.nivio.landscape.LandscapeInterface;
import de.bonndan.nivio.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class Aggregator {

    private final static Logger logger = LoggerFactory.getLogger(Aggregator.class);

    private final Map<FullyQualifiedIdentifier, ServiceState> state;

    private final ProviderFactory factory;

    private final NotificationService notificationService;

    @Autowired
    public Aggregator(Map<FullyQualifiedIdentifier, ServiceState> state, ProviderFactory factory, NotificationService notificationService) {
        this.state = state;
        this.notificationService = notificationService;
        this.factory = factory;
    }

    public void fetch(LandscapeInterface landscape) {

        getProviders(landscape).forEach(provider -> {
            try {
                provider.apply(state);
            } catch (Throwable throwable) {
                String msg = "Failed to apply state using provider " + provider;
                handleError(landscape, throwable, msg);
            }
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
        notificationService.sendError(ProcessingException.of(landscape, throwable), msg);
    }
}

