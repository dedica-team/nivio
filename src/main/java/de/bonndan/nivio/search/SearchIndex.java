package de.bonndan.nivio.search;

import de.bonndan.nivio.assessment.Assessment;
import org.apache.lucene.facet.FacetResult;
import org.springframework.lang.NonNull;

import java.net.URI;
import java.util.List;
import java.util.Set;

public interface SearchIndex {

    String WILDCARD = "*";
    String WHITESPACE = " ";
    String FACET_DELIMITER = ":";

    void indexForSearch(@NonNull final Set<SearchDocumentValueObject> components, @NonNull final Assessment assessment);

    @NonNull
    Set<URI> search(@NonNull String queryString);

    @NonNull
    List<FacetResult> facets();
}
