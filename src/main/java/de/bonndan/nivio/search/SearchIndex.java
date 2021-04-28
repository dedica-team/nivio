package de.bonndan.nivio.search;

import de.bonndan.nivio.model.Component;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.Item;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.facet.taxonomy.TaxonomyWriter;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static de.bonndan.nivio.search.SearchDocumentFactory.*;

/**
 * A lucene based search index on all landscape items.
 */
public class SearchIndex {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchIndex.class);
    public static final String WILDCARD = "*";
    public static final String WHITESPACE = " ";
    public static final String FACET_DELIMITER = ":";

    private final Directory searchIndex;
    private final Directory taxoIndex;

    /**
     * Creates a new empty index.
     */
    public SearchIndex() {

        //init lucene
        searchIndex = new RAMDirectory();
        taxoIndex = new RAMDirectory();
    }

    /**
     * Creates a search index based in a snapshot of current items state (later modifications won't be shown).
     *
     * @return number of indexed items
     * TODO support other components than {@link Item}
     */
    public int indexForSearch(Set<? extends Component> index) {
        int indexed = 0;
        try {
            FacetsConfig config = SearchDocumentFactory.getConfig();
            TaxonomyWriter taxoWriter = new DirectoryTaxonomyWriter(taxoIndex, IndexWriterConfig.OpenMode.CREATE);
            IndexWriter writer = new IndexWriter(searchIndex, new IndexWriterConfig(new StandardAnalyzer()));
            writer.deleteAll();
            for (Component component : index) {

                if (component instanceof Item) {
                    Document doc = from((Item) component);
                    writer.addDocument(config.build(taxoWriter, doc));
                    indexed++;
                }
            }
            IOUtils.close(writer, taxoWriter);
        } catch (IOException e) {
            throw new RuntimeException("Failed to update search index", e);
        }

        return indexed;
    }

    /**
     * Searches using the given queryString and returns a set of {@link FullyQualifiedIdentifier}s that can be used to retrieve
     * items from the {@link ItemIndex}.
     *
     * @param queryString a lucene query string. Whitespaces are treated as "AND".
     * @return the {@link FullyQualifiedIdentifier}s of the matched documents
     */
    @NonNull
    public Set<FullyQualifiedIdentifier> search(@NonNull final String queryString) {
        try {
            return documentSearch(rewriteQuery(queryString)).stream()
                    .map(doc -> FullyQualifiedIdentifier.from(doc.get(LUCENE_FIELD_FQI)))
                    .collect(Collectors.toSet());
        } catch (IOException | ParseException e) {
            throw new RuntimeException(String.format("Failed to execute search for '%s'", queryString));
        }
    }

    /**
     * TODO there might be a away in lucene to rewrite the query terms, e.g. in {@link MultiFieldQueryParser}
     */
    private String rewriteQuery(final String query) {
        return Arrays.stream(query.split(WHITESPACE))
                .map(s -> {
                    if (StringUtils.isEmpty(s) || "or".equalsIgnoreCase(s) || "and".equalsIgnoreCase(s)) {
                        return s;
                    }
                    if (s.endsWith(FACET_DELIMITER)) {
                        return s + WILDCARD;
                    }
                    if (s.contains(FACET_DELIMITER)) {
                        return s;
                    }
                    return s + WILDCARD;
                })
                .collect(Collectors.joining(WHITESPACE));
    }

    /**
     * Returns the facets for the given query.
     *
     * @return top 10 facets
     */
    public List<FacetResult> facets() {
        try {
            DirectoryReader ireader = DirectoryReader.open(searchIndex);
            IndexSearcher searcher = new IndexSearcher(ireader);

            DirectoryTaxonomyReader taxoReader = new DirectoryTaxonomyReader(taxoIndex);
            FacetsCollector fc = new FacetsCollector();
            FacetsConfig config = getConfig();
            FacetsCollector.search(searcher, new MatchAllDocsQuery(), 10, fc);

            Facets facets = new FastTaxonomyFacetCounts(taxoReader, config, fc);
            ireader.close();
            return facets.getAllDims(10);
        } catch (IOException e) {
            LOGGER.warn("Unable to get the facets for the given query error: ", e);
        }

        return null;
    }

    private List<Document> documentSearch(String queryString) throws IOException, ParseException {

        DirectoryReader ireader = DirectoryReader.open(searchIndex);
        IndexSearcher isearcher = new IndexSearcher(ireader);
        // Parse a simple query that searches for "text":
        QueryParser parser = new MultiFieldQueryParser(new String[]{LUCENE_FIELD_IDENTIFIER, LUCENE_FIELD_NAME, LUCENE_FIELD_DESCRIPTION}, new StandardAnalyzer());
        parser.setAllowLeadingWildcard(true);
        parser.setSplitOnWhitespace(true);
        parser.setDefaultOperator(QueryParser.Operator.AND);
        Query query = parser.parse(queryString);
        ScoreDoc[] hits = isearcher.search(query, 10).scoreDocs;

        List<Document> documents = new ArrayList<>();
        // Iterate through the results:
        for (ScoreDoc hit : hits) {
            Document hitDoc = isearcher.doc(hit.doc);
            documents.add(hitDoc);
        }
        ireader.close();

        return documents;
    }
}
