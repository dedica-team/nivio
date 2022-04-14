package de.bonndan.nivio.search;

import de.bonndan.nivio.assessment.Assessment;
import org.apache.lucene.facet.FacetResult;
import org.springframework.lang.NonNull;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * no-op impl for testing
 */
public class NullSearchIndex implements SearchIndex {

    @Override
    public void indexForSearch(@NonNull final Set<SearchDocumentValueObject> components,
                               @NonNull final Assessment assessment
    ) {
        //no-op
    }

    @NonNull
    @Override
    public Set<URI> search(@NonNull String queryString) {
        return Collections.emptySet();
    }

    @NonNull
    @Override
    public List<FacetResult> facets() {
        return Collections.emptyList();
    }
}
