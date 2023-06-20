/*
 *  This code belongs to the Semantic Access and Retrieval (SAR) group of the 
 *  Information Systems Laboratory (ISL) of the 
 *  Institute of Computer Science (ICS) of the  
 *  Foundation for Research and Technology - Hellas (FORTH)
 *  Nobody is allowed to use, copy, distribute, or modify this work.
 *  It is published for reasons of research results reproducibility.
 *  (c) 2020 Semantic Access and Retrieval group, All rights reserved
 */
package gr.forth.ics.isl.demoExternal.main;

import edu.mit.jwi.IDictionary;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import gr.forth.ics.isl.demoExternal.LODsyndesis.LODSyndesisChanel;
import gr.forth.ics.isl.demoExternal.core.EntitiesDetection;
import gr.forth.ics.isl.demoExternal.core.SPARQLQuery;

import gr.forth.ics.isl.nlp.externalTools.Spotlight;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import gr.forth.ics.isl.servlet.InformationExtraction;
import java.awt.Image;
import java.util.HashSet;
import javax.imageio.ImageIO;

/**
 * This is the main class for the execution of the QA process. The constructor
 * of the class is responsible to initialize all the required resources and
 * tools. Using the created instance, the user can use the methods
 * "getAnswerAsJson", to submit a question in Natural Language and retrieve an
 * answer in JSON format.
 *
 * @author Michalis
 */
public class ExternalKnowledgeDemoMain {

    //Core Nlp pipeline instance (for question analysis)
    public static StanfordCoreNLP split_pipeline;
    //Core Nlp pipeline instance (for answer extraction)
    public static StanfordCoreNLP lemma_pipeline;
    //Core Nlp pipeline instance (for entities detection)
    public static StanfordCoreNLP entityMentions_pipeline;
    public static StanfordCoreNLP compounds_pipeline;
    public static IDictionary wordnet_dict;
    public static EntitiesDetection entities_detection = new EntitiesDetection();
    //DBPedia Spotlight instance
    public static Spotlight spotlight;
    public static ArrayList<String> wordnetResources = new ArrayList<>();

    public HashSet<String> entityURIsForEvaluation = new HashSet<String>();

    // LODSyndesisChanel instance, for accessing the provided rest api.
    public static LODSyndesisChanel chanel;

    public ExternalKnowledgeDemoMain() {
        try {
            initializeToolsAndResources("");
            //System.out.println("inside");
        } catch (IOException ex) {
            Logger.getLogger(ExternalKnowledgeDemoMain.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Function for the initialization of the required resources. i.e. all the
     * coreNLP properties, stop-words, spotlight, lodsyndesis chanel etc.
     *
     * @param wordnetPath
     * @throws MalformedURLException
     * @throws IOException
     */
    public static void initializeToolsAndResources(String wordnetPath) throws MalformedURLException, IOException {

        //Logger.getLogger(ExternalKnowledgeDemoMain.class.getName()).log(Level.INFO, "...Generating stop-words lists...");
//        StringUtils.generateStopListsFromExternalSource(filePath_en, filePath_gr);
        Properties split_props = new Properties();
        //Properties without lemmatization
        split_props.put("annotators", "tokenize, ssplit, pos");
        split_props.put("tokenize.language", "en");
        split_pipeline = new StanfordCoreNLP(split_props);

        Properties lemma_props = new Properties();
        lemma_props.put("annotators", "tokenize, ssplit, pos, lemma");
        lemma_props.put("tokenize.language", "en");
        lemma_pipeline = new StanfordCoreNLP(lemma_props);

        Properties entityMentions_props = new Properties();
        entityMentions_props.put("annotators", "tokenize, ssplit, truecase, pos, lemma,  ner, entitymentions");
        entityMentions_props.put("tokenize.language", "en");
        entityMentions_props.put("truecase.overwriteText", "true");
        entityMentions_pipeline = new StanfordCoreNLP(entityMentions_props);

        Properties compound_props = new Properties();
        compound_props.put("annotators", "tokenize, ssplit, truecase, pos, parse");
        compound_props.put("tokenize.language", "en");
        compound_props.put("truecase.overwriteText", "true");
        compounds_pipeline = new StanfordCoreNLP(compound_props);

        spotlight = new Spotlight();

        chanel = new LODSyndesisChanel();
    }

    /**
     * Function for submitting a question in Natural Language and retrieve an
     * answer in JSON format. This function executes the whole QA pipeline, to
     * analyze the input question and retrieve an answer. The JSON contains
     * information like: question type, question entities, answer triple,
     * provenance, confidence score etc.
     *
     * This function is being exploited for the online demo, which takes an
     * additional input "format" with value "plain" or "triple".
     *
     * @param query
     * @param format
     * @return
     */
    /**
     * Function for submitting a question in Natural Language and retrieve an
     * answer in JSON format. This function executes the whole QA pipeline, to
     * analyze the input question and retrieve an answer. The JSON contains
     * information like: question type, question entities, answer triple,
     * provenance, confidence score etc.
     *
     * @param query
     * @param toolType
     * @return
     */
    public HashMap<String, String> getEntities(String query, String toolType) {
        String ip = "";
        // ==== Entities Detection Step ====

        // String NEtool = "both";
        //String NEtool = "scnlp";
        String NEtool = "both";
        if (toolType.equals("Stanford CoreNLP")) {
            NEtool = "scnlp";
        } else if (toolType.equals("DBpedia Spotlight")) {
            NEtool = "dbpedia";
        }
        // identify NamedEntities in the question using SCNLP and Spotlight
        entities_detection.identifyNamedEntities(query, NEtool);

        HashMap<String, String> entity_URI = entities_detection.extractEntitiesWithUris(query, NEtool);
        return entity_URI;
    }
    static HashMap<String, String> URIs = new HashMap<>();
    HashMap<String, String> entityURIs = new HashMap<>();

    static ArrayList<String> unsortedURIs = new ArrayList<String>();

    public HashMap<String, String> getURIs() {
        return URIs;
    }

    public ArrayList<String> getUnsortedURIs() {
        return unsortedURIs;
    }

    public HashMap<String, String> getEntityURIs() {
        return entityURIs;
    }

    public String dbpediaQuery(String query, String endpoint) throws UnsupportedEncodingException, MalformedURLException, IOException {
        // //System.out.println(query);
        String sparqlQueryURL = endpoint + "?query=" + URLEncoder.encode(query, "utf8");
        URL url = new URL(sparqlQueryURL);
        URLConnection con = url.openConnection();
        String xml_content = "application/sparql-results+xml";
        con.setRequestProperty("ACCEPT", xml_content);

        /* In case the Endpoint asks for username and password */
        InputStream is = con.getInputStream();
        InputStreamReader isr = new InputStreamReader(is, "utf8");
        BufferedReader in = new BufferedReader(isr);

        String input;
        String resultsString = "";
        while ((input = in.readLine()) != null) {
            ////System.out.println(input);
            String[] split = input.split("<uri>");
            if (split.length < 2) {
                continue;
            }

            String[] split2 = split[1].split("</uri>");
            if (split2.length > 1 && !split2[0].trim().equals("")) {
                resultsString = resultsString + split2[0] + "\n";
            }
        }

        in.close();
        isr.close();
        is.close();

        return resultsString;

    }

  

    public String getImage(String uri) {
        try {
            long startTime = System.currentTimeMillis();

            String query = "select distinct ?s where {<" + uri + "> <http://dbpedia.org/ontology/thumbnail> ?s}"; // http://dbpedia.org/resource/Category:FIFA_100
            String service = "https://dbpedia.org/sparql";
            String res = this.dbpediaQuery(query, service);
//            if (this.testImage(res) == false) {
//
//                query = "select ?o where {<" + uri + "> <http://xmlns.com/foaf/0.1/depiction> ?o}";
//                String res2 = this.dbpediaQuery(query, service);
//                for (String newImg : res2.split("\n")) {
//                    res = newImg;
//                    if (this.testImage(res) == true) {
//                        break;
//                    }
//                }
//            }
            //long estimatedTime = System.currentTimeMillis() - startTime;
            // System.out.println(uri + " " + (double) estimatedTime / (1000));
            return res;
        } catch (MalformedURLException ex) {
            Logger.getLogger(ExternalKnowledgeDemoMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExternalKnowledgeDemoMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public void entityStatistics(ArrayList<String> entity_URI) {
        SPARQLQuery sq = new SPARQLQuery();
        double avgDatasets = 0, avgURIs = 0, avgDB = 0, avgWK = 0, avgDBWiki = 0, avgTriples = 0, avgCommonFacts = 0, avgCommonFactsDBWK = 0;
        HashSet<String> allDatasets = new HashSet<String>();
        int count = 0;
        for (String ent : entity_URI) {
            String code = this.getEntityCode(ent, sq);
            if (!code.contains("EID")) {
                continue;
            }
            count++;
            int urisNum = this.getEntityURIsNumber(code, sq);
            int datasetsNum = this.getDatasetsNumber(code, sq);
            int triplesNum = 0;
            int dbpedia = 0, wikidata = 0;
            int commonFacts = 0, commonFactsDBWK = 0;
            int wikidataDBpedia = 0;
            String dCount = this.getDirectCount(code, sq);
            String[] split = dCount.split("DC");
            //System.out.println(dCount);
            if (split.length == 2) {
                for (String dst : split[0].split(",")) {
                    allDatasets.add(dst);
                }

                String[] split2 = split[1].split("\\$");
                for (String prov : split2) {
                    // System.out.println(prov);
                    String[] split3 = prov.split("\t");
                    if (split3[0].contains("288")) {
                        dbpedia += Integer.parseInt(split3[1]);
                        wikidataDBpedia += Integer.parseInt(split3[1]);
                    } else if (split3[0].contains("289")) {
                        wikidata += Integer.parseInt(split3[1]);
                        wikidataDBpedia += Integer.parseInt(split3[1]);
                    }

                    if (split3[0].split(",").length > 1) {
                        commonFacts += Integer.parseInt(split3[1]);
                        if (split3[0].contains("288,289")) {
                            commonFactsDBWK += Integer.parseInt(split3[1]);
                        }
                    }

                    triplesNum += Integer.parseInt(split3[1]);
                }

            }
            avgDB += dbpedia;
            avgDBWiki += wikidataDBpedia;
            avgURIs += urisNum;
            avgDatasets += datasetsNum;
            avgTriples += triplesNum;
            avgCommonFacts += commonFacts;
            avgCommonFactsDBWK += commonFactsDBWK;
            avgWK += wikidata;
            System.out.println(ent + "\t" + urisNum + "\t" + datasetsNum + "\t" + dbpedia + "\t" + wikidata + "\t" + wikidataDBpedia + "\t" + triplesNum + "\t" + commonFactsDBWK + "\t" + commonFacts);
        }

        avgDB = avgDB / (double) +count;
        avgWK = avgWK / (double) +count;
        avgDBWiki = avgDBWiki / (double) +count;
        avgDatasets = avgDatasets / (double) +count;
        avgURIs = avgURIs / (double) +count;
        avgTriples = avgTriples / (double) +count;
        avgCommonFacts = avgCommonFacts / (double) +count;
        avgCommonFactsDBWK = avgCommonFactsDBWK / (double) +count;

        System.out.println("===========");
        System.out.println("Entities:\t" + entity_URI.size());
        System.out.println("Datasets with at least one Recognized Entity:\t" + allDatasets.size());
        System.out.println("Average URIs:\t" + avgURIs);
        System.out.println("Average Datasets:\t" + avgDatasets);
        System.out.println("DBpedia Average Triples:\t" + avgDB);
        System.out.println("Wikidata Average Triples:\t" + avgWK);

        System.out.println("DBpedia Wikidata Triples:\t" + avgDBWiki);
        System.out.println("LODsyndesis Average Triples:\t" + avgTriples);
        System.out.println("Average Common Facts of DB WK:\t" + avgCommonFactsDBWK);
        System.out.println("Average Common Facts:\t" + avgCommonFacts);
        System.out.println("===========");
    }

    public int getDatasetsNumber(String code, SPARQLQuery sq) {
        String query = "select  distinct ?SID where { <" + code + "> <http://www.ics.forth.gr/isl/provenance> ?SID}";
        String[][] queryResults = sq.runSPARQLQuery(query, false, true, "\t");

        if ((queryResults.length > 1 && queryResults[1][0] != null)) {
            return queryResults[1][0].split(",").length;
        }

        return 0;

    }

    public String getDirectCount(String code, SPARQLQuery sq) {
        String query = "select  distinct ?SID where { <" + code + "> <http://www.ics.forth.gr/isl/directCount> ?SID}";
        String[][] queryResults = sq.runSPARQLQuery(query, false, true, "%");

        if ((queryResults.length > 1 && queryResults[1][0] != null)) {
            return queryResults[1][0];
        }

        return "";

    }

    /**
     * the URI of an entity in LODsyndesis
     *
     * @param entity the code of an entity in LODsyndesis
     * @param sparql a sparql instance
     * @return the URI of an entity in LODsyndesis
     */
    public String getEntityCode(String entity, SPARQLQuery sparql) {
        String query = "select distinct ?code where { <" + entity + "> <http://www.ics.forth.gr/isl/identifier> ?code}";
        String[][] queryResults = sparql.runSPARQLQuery(query, false, true, "\t");
        if ((queryResults.length > 1 && queryResults[1][0] != null)) {
            String code = queryResults[1][0];
            return code;
        }
        return "";
    }

    public int getEntityURIsNumber(String code, SPARQLQuery sq) {
        String query = "select count distinct ?SID where { ?SID <http://www.ics.forth.gr/isl/identifier> <" + code + ">}";
        String[][] queryResults = sq.runSPARQLQuery(query, false, true, "\t");

        if ((queryResults.length > 1 && queryResults[1][0] != null)) {
            String cnt = queryResults[1][0];
            return Integer.parseInt(cnt);

        }

        return 0;

    }

    public String[][] getResults(HashMap<String, String> entity_URI, boolean img) {
        URIs.clear();
        unsortedURIs.clear();
        int i = 1;
        String uris = "";
        List<String> sortedKeys = new ArrayList<String>(entity_URI.keySet());
        List<String> removeKeys = new ArrayList<String>();
        for (String k : sortedKeys) {
            for (String p : sortedKeys) {
                if (!p.equals(k) && p.contains(k) && p.contains(" ")) {
                    removeKeys.add(k);
                }
            }
        }
        sortedKeys.removeAll(removeKeys);
        this.entityURIsForEvaluation.clear();
        for (String k : sortedKeys) {
            this.entityURIsForEvaluation.add(entity_URI.get(k));
            unsortedURIs.add(entity_URI.get(k));
        }

        // this.entityStatistics(unsortedURIs);
        System.out.println(unsortedURIs);
        Collections.sort(sortedKeys);
        String[][] results = new String[sortedKeys.size() + 2][5];
        results[0][0] = "Entity";
        results[0][1] = "Find Provenance";
        results[0][2] = "Download URIs";
        results[0][3] = "Download All Triples";
        results[0][4] = "Find the Top-K Datasets";

//        for (String k : sortedKeys) {
//            System.out.println(k + "\t" + entity_URI.get(k));
//        }
        for (String k : sortedKeys) {
            String image = "";
            if (img == true) {
                if (InformationExtraction.images.containsKey(entity_URI.get(k))) {
                    image = InformationExtraction.images.get(entity_URI.get(k));
                    //  System.out.println("cache");
                } else {
                    image = this.getImage(entity_URI.get(k));
                    if (InformationExtraction.images.size() >= 30000) {
                        InformationExtraction.images.clear();
                    }
                    InformationExtraction.images.put(entity_URI.get(k), image);
                }
            }
            results[i][0] = "";
            if (!image.equals("")) {
                results[i][0] = "<img src=" + image + " width='75' height='75' title='" + k + "' ><br>";
            }
            String text = k.replace("_", " ") + "\thttps://demos.isl.ics.forth.gr/lodsyndesis/RunQuery?URI=" + entity_URI.get(k).replace("http://", "").replace("/", "$").replace("#", "@") + "&queryType=triples";

            results[i][0] += text;
            results[i][1] = "[<a style=\"color:black\" href=\"https://demos.isl.ics.forth.gr/lodsyndesis/rest-api/objectCoreference?uri=" + entity_URI.get(k) + "&provenance=true\">RDF</a>,<a style=\"color:black\" href=\"https://demos.isl.ics.forth.gr/lodsyndesis/RunQuery?URI=" + entity_URI.get(k).replace("http://", "").replace("/", "$").replace("#", "@") + "&queryType=DatDom\">HTML</a>,<a style=\"color:black\" href=\"InformationExtraction?URIs=" + entity_URI.get(k).replace(":", "%3A").replace("/", "%2F").replace("/", "%3F") + "&export=provenance.json&single=true\">JSON</a>]";
            results[i][2] = "[<a style=\"color:black\" href=\"https://demos.isl.ics.forth.gr/lodsyndesis/rest-api/objectCoreference?uri=" + entity_URI.get(k) + "\">RDF</a>,<a style=\"color:black\" href=\"https://demos.isl.ics.forth.gr/lodsyndesis/RunQuery?URI=" + entity_URI.get(k).replace("http://", "").replace("/", "$").replace("#", "@") + "&queryType=EquivalentURIs\">HTML</a>,<a style=\"color:black\" href=\"InformationExtraction?URIs=" + entity_URI.get(k).replace(":", "%3A").replace("/", "%2F").replace("/", "%3F") + "&export=objectCoreference.json&single=true\">JSON</a>]";
            results[i][3] = "[<a style=\"color:black\" href=\"https://demos.isl.ics.forth.gr/lodsyndesis/rest-api/allFacts?uri=" + entity_URI.get(k) + "\">RDF</a>,<a style=\"color:black\" href=\"https://demos.isl.ics.forth.gr/lodsyndesis/RunQuery?URI=" + entity_URI.get(k).replace("http://", "").replace("/", "$").replace("#", "@") + "&queryType=triples\">HTML</a>,<a style=\"color:black\" href=\"InformationExtraction?URIs=" + entity_URI.get(k).replace(":", "%3A").replace("/", "%2F").replace("/", "%3F") + "&export=allFacts.json&single=true\">JSON</a>]";
            results[i][4] = "[<a style=\"color:black\" href=\"https://demos.isl.ics.forth.gr/lodsyndesis/rest-api/entityBasedDatasetDiscovery?entities=" + entity_URI.get(k) + "&subsetSize=3&topK=10&measurementType=coverage\">CSV</a>,<a style=\"color:black\" href=\"https://demos.isl.ics.forth.gr/lodsyndesis/RunQueryCov?URI=" + entity_URI.get(k) + "&mt=coverage&qt=3&lt=10\">HTML</a>]";

            URIs.put(entity_URI.get(k), text);
            entityURIs.put(k.replace("_", " "), entity_URI.get(k));
            i++;
            uris += entity_URI.get(k).replace(":", "%3A").replace("/", "%2F").replace("/", "%3F") + "%0A";//"https://demos.isl.ics.forth.gr/lodsyndesis/RunQueryEntities?URIs=http%3A%2F%2Fdbpedia.org%2Fresource%2FAristotle%0Ahttp%3A%2F%2Fdbpedia.org%2Fresource%2FSocrates%0Ahttp%3A%2F%2Fdbpedia.org%2Fresource%2FAthens&mt=coverage&qt=1&lt=10";

        }
        results[sortedKeys.size() + 1][0] = "<b>All Entities</b>";
        results[sortedKeys.size() + 1][1] = "<a style=\"color:black\" href=\"InformationExtraction?text=" + uris.substring(0, uris.length() - 3) + "&export=allDatasetsAllEntities.nt\">RDF</a>";//&nbsp;&nbsp;<a href=\"InformationExtraction?URIs="+uris.substring(0,uris.length()-3)+"&export=allDatasetsAllEntities.json\"><img src=\"images/json2.jpg\" alt=\"JSON\" height=\"40\" width=\"40\"></a>";
        results[sortedKeys.size() + 1][2] = "<a style=\"color:black\" href=\"InformationExtraction?text=" + uris.substring(0, uris.length() - 3) + "&export=objectCoreferenceAllEntities.nt\">RDF</a>";//&nbsp;&nbsp;<a><img src=\"images/json2.jpg\" alt=\"JSON\" height=\"40\" width=\"40\"></a>";
        results[sortedKeys.size() + 1][3] = "<a style=\"color:black\" href=\"InformationExtraction?text=" + uris.substring(0, uris.length() - 3) + "&export=allFactsAllEntities.nq\">RDF<a>";//<img src=\"images/json2.jpg\" alt=\"JSON\" height=\"40\" width=\"40\"></a>";
        results[sortedKeys.size() + 1][4]
                = //"[<a style=\"color:black\" href=\"https://demos.isl.ics.forth.gr/lodsyndesis/rest-api/entityBasedDatasetDiscovery?entities=" + uris.substring(0, uris.length() - 3)  + "&subsetSize=3&topK=10&measurementType=coverage\">CSV</a>,"+
                "<a style=\"color:black\" href=\"https://demos.isl.ics.forth.gr/lodsyndesis/RunQueryEntities?URIs=" + uris.substring(0, uris.length() - 3) + "&mt=coverage&qt=1&lt=10\">HTML</a>";

        //System.out.println(results.length);
        return results;

    }

    public HashSet<String> getEntityURIsForEvaluation() {
        return entityURIsForEvaluation;
    }
}
