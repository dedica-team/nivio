package de.bonndan.nivio.search;

import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.model.Component;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface SearchIndex {

    String WILDCARD = "*";
    String WHITESPACE = " ";
    String FACET_DELIMITER = ":";

    void indexForSearch(@NonNull final Set<SearchDocumentValueObject> components, @NonNull final Assessment assessment);

    @NonNull
    Set<URI> search(@NonNull String queryString);

    @NonNull
    List<FacetResult> facets();

    <T extends Component> void remove(T component);
}
