package de.bonndan.nivio.search;

import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.model.Component;
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
    public void indexForSearch(@NonNull final Set<SearchDocumentValueObject> components, Assessment assessment) {

    }

    @NonNull
    @Override
    public Set<URI> search(@NonNull String queryString) {
        return Collections.emptySet();
    }

    @Override
    public List<FacetResult> facets() {
        return Collections.emptyList();
    }

    @Override
    public <T extends Component> void remove(T component) {

    }
}
