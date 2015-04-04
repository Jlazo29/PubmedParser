package ingestion;

import parsers.*;
import org.apache.log4j.BasicConfigurator;

import org.apache.solr.common.SolrInputDocument;
import parsers.PMCParser;
import parsers.PMC.Article;
import parsers.MedlineParser;
import parsers.medline.MedlineCitation;
import parsers.medline.MedlineCitationSet;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.*;

public class  SolrUtils {
    // TODO: automate this, and make it portable
    public static final String SAMPLE_FILE = "/home/jlazo/Projects/solr-pubmed/entrez-parsing/src/main/resources/samples/medsamp2014.xml";
    public static final String SAMPLE_FILE_2 = "samples/pubmed_sample_2009.xml";
    public static final String MEDLINE = "/home/jorge/Projects/Medline";
    public static Integer count;
    public static SolrClient client;
    public static Collection<SolrInputDocument> collection;
    public static PMCParser pmcParser;
    public static MedlineParser medlineParser;

    public static boolean parsePMC(Path path) throws Exception{
        Article article = pmcParser.unmarshallPath(path);
        String articleType = article.getArticleType();
        if (articleType.equals("case-report") || articleType.equals("research-article")){
            collection.add(pmcParser.mapToSolrInputDocument(article));
            count++;
            return true;
        }
        System.out.println("This file: " + path.getFileName() + " is not a research article or a case-report, it is a " + article.getArticleType());
        return false;
    }

    public static boolean parseMedline(Path path) throws Exception{
        MedlineCitationSet set = medlineParser.unmarshallPath(path);
        Collection<SolrInputDocument> data = medlineParser.mapToSolrInputDocumentCollection(set);
        count += data.size();
        collection.addAll(medlineParser.mapToSolrInputDocumentCollection(set));
        return true;
    }


    public static void main(String[] args) throws Exception {
        File rootDir = new File("/home/jlazo/Projects/Medsamp/A-B/");

        collection = new ArrayList<>();
        long startTime = System.nanoTime();
        count = 0;
        BasicConfigurator.configure();
        client = new SolrClient(false);
        pmcParser = new PMCParser();
//        client.deleteRecords() //!!!!CARE!!!!

        Path startingPath = rootDir.toPath();
        FileTraverser traverser = new FileTraverser();
        Files.walkFileTree(startingPath, traverser);

        long parseTime = System.nanoTime();
        String time = String.valueOf((parseTime - startTime )/ 1000000000.0 );
        System.out.println(System.getProperty("line.separator") + "Took " + time + " seconds to Parse " + count + " Files!" + System.getProperty("line.separator"));

        client.update(collection);

        long indexTime = System.nanoTime();
        String time2 = String.valueOf((indexTime - parseTime )/ 1000000000.0 );

        System.out.println(System.getProperty("line.separator") + "Took " + time2 + " seconds to index " + count + " Files!" + System.getProperty("line.separator"));
        System.out.println("TOTAL TIME TAKEN TO PARSE AND INDEX: " + ((System.nanoTime() - startTime) /1000000000.0) + " SECONDS" );

    }

 }
