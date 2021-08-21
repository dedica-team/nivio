package de.bonndan.nivio.search;

import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Landscape;
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
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
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

    private final Directory searchIndexDir;
    private final Directory taxoIndexDir;

    /**
     * Creates a new empty index.
     */
    public SearchIndex(@NonNull final String identifier) {

        //init lucene
        try {
            var tmpdir = System.getProperty("java.io.tmpdir");
            var fsSafeIdentifier = StringUtils.trimTrailingCharacter(identifier.replaceAll("[^0-9a-fA-F]", "_"), File.separatorChar);
            searchIndexDir = new MMapDirectory(Path.of(tmpdir, "nivio-document-index", fsSafeIdentifier));
            taxoIndexDir = new MMapDirectory(Path.of(tmpdir, "nivio-facet-index", fsSafeIdentifier));
        } catch (IOException e) {
            LOGGER.error(String.format("Failed to create search index: %s", e.getMessage()));
            throw new SearchIndexCreationException("Failed to create search index.", e);
        }
    }

    /**
     * Index a landscape.
     *
     * @param landscape  the landscape to index
     * @param assessment the current assessment (status are indexed, too)
     */
    public void indexForSearch(@NonNull final Landscape landscape, @NonNull final Assessment assessment) {
        Set<Item> items = Objects.requireNonNull(landscape).getItems().all();
        indexItems(items, Objects.requireNonNull(assessment).getResults());
    }

    /**
     * Creates a search index based in a snapshot of current items state (later modifications won't be shown).
     */
    private void indexItems(Set<Item> items, Map<String, List<StatusValue>> assessments) {
        try {
            FacetsConfig config = SearchDocumentFactory.getConfig();
            TaxonomyWriter taxoWriter = new DirectoryTaxonomyWriter(taxoIndexDir, IndexWriterConfig.OpenMode.CREATE);
            IndexWriter writer = new IndexWriter(searchIndexDir, new IndexWriterConfig(new StandardAnalyzer()));
            writer.deleteAll();
            for (Item item : items) {
                writer.addDocument(config.build(taxoWriter, from(item, assessments.get(item.getFullyQualifiedIdentifier().toString()))));
            }
            IOUtils.close(writer, taxoWriter);
        } catch (IOException e) {
            throw new SearchEngineException("Failed to update search index", e);
        }

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
            throw new SearchEngineException(String.format("Failed to execute search for '%s'", queryString));
        }
    }

    /**
     * TODO there might be a away in lucene to rewrite the query terms, e.g. in {@link MultiFieldQueryParser}
     */
    private String rewriteQuery(final String query) {
        return Arrays.stream(query.split(WHITESPACE))
                .map(s -> {
                    if (!StringUtils.hasLength(s) || "or".equalsIgnoreCase(s) || "and".equalsIgnoreCase(s)) {
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
            DirectoryReader ireader = DirectoryReader.open(searchIndexDir);
            IndexSearcher searcher = new IndexSearcher(ireader);

            DirectoryTaxonomyReader taxoReader = new DirectoryTaxonomyReader(taxoIndexDir);
            FacetsCollector fc = new FacetsCollector();
            FacetsConfig config = getConfig();
            FacetsCollector.search(searcher, new MatchAllDocsQuery(), 10, fc);

            Facets facets = new FastTaxonomyFacetCounts(taxoReader, config, fc);
            ireader.close();
            return facets.getAllDims(10);
        } catch (IOException e) {
            LOGGER.warn("Unable to get the facets for the given query error: ", e);
        }

        return new ArrayList<>();
    }

    private List<Document> documentSearch(String queryString) throws IOException, ParseException {

        DirectoryReader ireader = DirectoryReader.open(searchIndexDir);
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
