package de.bonndan.nivio.input.linked;

import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.model.Labeled;
import de.bonndan.nivio.model.Link;

import java.util.concurrent.CompletableFuture;

public class GitHubProjectHandler implements ExternalLinkHandler {

    private final HttpService httpService;

    public GitHubProjectHandler(HttpService httpService) {
        this.httpService = httpService;
    }

    @Override
    public CompletableFuture<String> resolveAndApplyData(Link link, Labeled component) {
        return CompletableFuture.completedFuture("OK");
    }
}
