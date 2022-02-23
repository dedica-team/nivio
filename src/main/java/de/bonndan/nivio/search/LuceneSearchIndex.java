package de.bonndan.nivio.search;

import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.model.Component;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
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
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static de.bonndan.nivio.search.SearchDocumentFactory.from;
import static de.bonndan.nivio.search.SearchDocumentFactory.getConfig;

/**
 * A lucene based search index on all landscape items.
 */
public class LuceneSearchIndex implements SearchIndex {

    private static final Logger LOGGER = LoggerFactory.getLogger(LuceneSearchIndex.class);
    private static final String[] MULTI_FIELD_QUERY_FIELDS = {
            SearchField.IDENTIFIER.getValue(),
            SearchField.LUCENE_FIELD_NAME.getValue(),
            SearchField.LUCENE_FIELD_DESCRIPTION.getValue(),
            SearchField.LUCENE_FIELD_GENERIC.getValue(),
    };

    private Directory searchIndexDir;
    private Directory taxoIndexDir;
    private final MultiFieldQueryParser parser;

    /**
     * Creates a new empty index.
     */
    public static LuceneSearchIndex createFor(@NonNull final String identifier) {
        try {
            var tmpdir = System.getProperty("java.io.tmpdir");
            var fsSafeIdentifier = StringUtils.trimTrailingCharacter(identifier.replaceAll("[^0-9a-fA-F]", "_"), File.separatorChar);
            var sd = new MMapDirectory(Path.of(tmpdir, "nivio-document-index", fsSafeIdentifier));
            var td = new MMapDirectory(Path.of(tmpdir, "nivio-facet-index", fsSafeIdentifier));
            return new LuceneSearchIndex(sd, td);
        } catch (IOException e) {
            throw new SearchIndexCreationException(String.format("Failed to create search index: %s", e.getMessage()), e);
        }
    }

    public static LuceneSearchIndex createVolatile() {
        return new LuceneSearchIndex(new RAMDirectory(), new RAMDirectory());
    }

    /**
     * Creates a new empty index with given directory implementations.
     */
    private LuceneSearchIndex(@NonNull final Directory searchIndexDir, @NonNull final Directory taxoIndexDir) {
        this.searchIndexDir = Objects.requireNonNull(searchIndexDir);
        this.taxoIndexDir = Objects.requireNonNull(taxoIndexDir);

        // Parse a simple query that searches for "text":
        parser = new MultiFieldQueryParser(MULTI_FIELD_QUERY_FIELDS, new StandardAnalyzer());
        parser.setAllowLeadingWildcard(true);
        parser.setSplitOnWhitespace(true);
        parser.setDefaultOperator(QueryParser.Operator.AND);
    }

    /**
     * Index a landscape.
     *
     * @param components components to index
     * @param assessment the current assessment (status are indexed, too)
     */
    @Override
    public void indexForSearch(@NonNull final Set<SearchDocumentValueObject> components, @NonNull final Assessment assessment) {
        LOGGER.debug("Indexing {} components for search.", Objects.requireNonNull(components).size());
        Objects.requireNonNull(assessment);

        /*
        if (this.searchIndexDir instanceof RAMDirectory)
            this.searchIndexDir = new RAMDirectory();

        if (this.taxoIndexDir instanceof RAMDirectory)
            this.taxoIndexDir = new RAMDirectory();

         */

        try {
            FacetsConfig config = SearchDocumentFactory.getConfig();
            TaxonomyWriter taxoWriter = new DirectoryTaxonomyWriter(taxoIndexDir, IndexWriterConfig.OpenMode.CREATE);
            IndexWriter writer = new IndexWriter(searchIndexDir, new IndexWriterConfig(new StandardAnalyzer()));
            writer.deleteAll();
            writer.commit();

            for (SearchDocumentValueObject searchDocumentValueObject : components) {
                URI fullyQualifiedIdentifier = searchDocumentValueObject.getFullyQualifiedIdentifier();
                Document doc = from(searchDocumentValueObject, assessment.getResults().get(fullyQualifiedIdentifier));
                writer.addDocument(config.build(taxoWriter, doc));
            }

            IOUtils.close(writer, taxoWriter);
        } catch (IOException e) {
            throw new SearchEngineException("Failed to update search index", e);
        }

    }

    /**
     * Searches using the given queryString and returns a set of {@link FullyQualifiedIdentifier}s
     *
     * @param queryString a lucene query string. Whitespaces are treated as "AND".
     * @return the {@link FullyQualifiedIdentifier}s of the matched documents
     */
    @Override
    @NonNull
    public Set<URI> search(@NonNull final String queryString) {
        try {
            DirectoryReader ireader = DirectoryReader.open(searchIndexDir);
            IndexSearcher isearcher = new IndexSearcher(ireader);

            Query query = parser.parse(Arrays.stream(queryString.split(WHITESPACE))
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
                    .collect(Collectors.joining(WHITESPACE)));
            ScoreDoc[] hits = isearcher.search(query, 10).scoreDocs;

            List<Document> documents = new ArrayList<>();
            // Iterate through the results:
            for (ScoreDoc hit : hits) {
                Document hitDoc = isearcher.doc(hit.doc);
                documents.add(hitDoc);
            }
            ireader.close();

            return documents.stream()
                    .map(doc -> {
                        try {
                            return new URI(doc.get(SearchField.LUCENE_FIELD_FQI.getValue()));
                        } catch (URISyntaxException e) {
                            LOGGER.error("Failed to generate URI from {}", doc, e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        } catch (IOException | ParseException e) {
            throw new SearchEngineException(String.format("Failed to execute search for '%s'", queryString), e);
        }
    }

    /**
     * Returns the facets for the given query.
     *
     * @return top 10 facets
     */
    @Override
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

        return Collections.emptyList();
    }

    @Override
    public <T extends Component> void remove(T component) {

    }
}
