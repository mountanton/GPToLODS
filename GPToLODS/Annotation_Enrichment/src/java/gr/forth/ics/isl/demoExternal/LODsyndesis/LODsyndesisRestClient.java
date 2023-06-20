
/*  This code belongs to the Semantic Access and Retrieval (SAR) group of the
 *  Information Systems Laboratory (ISL) of the
 *  Institute of Computer Science (ICS) of the
 *  Foundation for Research and Technology - Hellas (FORTH)
 *  Nobody is allowed to use, copy, distribute, or modify this work.
 *  It is published for reasons of research results reproducibility.
 *  (c) 2020 Semantic Access and Retrieval group, All rights reserved
 */
package gr.forth.ics.isl.demoExternal.LODsyndesis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author Sgo
 *
 * This Class is used to send requests and receive requests from LODsyndesis
 * rest API. It supports Object Coreference, Fact Validation, All Facts for An
 * Entity and LODsyndesis keyword search services.
 */
public class LODsyndesisRestClient {

    private HttpClient client;
    private HttpGet objectCoreference;
    private HttpGet entitiesDD;
    private HttpGet allFacts;
    private HttpGet allDatasets;
    private HttpGet datasetDiscovery;
    private HttpGet factChecking;
    private HttpGet keywordEntity;
    private HttpGet entityCardinality;
    private static final String URL = "https://demos.isl.ics.forth.gr/lodsyndesis/rest-api";
    private String serviceName;

    private DecimalFormat df = new DecimalFormat(".##");

    /**
     * Used to open connection with client and LODsyndesis
     */
    public LODsyndesisRestClient() {
        client = HttpClientBuilder.create().build();
        df.setRoundingMode(RoundingMode.DOWN);

    }

    /**
     * Used to search for a given entity and its same as entities.
     *
     * @param uri
     * @return Triples of sameAs entities
     */
    public ArrayList<String> getEquivalentEntity(String uri) {
        try {
            serviceName = "objectCoreference";
            objectCoreference = new HttpGet(URL + "/" + serviceName + "?uri=" + uri);
            objectCoreference.addHeader(ACCEPT, "text/plain");
            objectCoreference.addHeader(CONTENT_TYPE, "application/n-triples");
            System.out.println(objectCoreference);
            ArrayList<ArrayList<String>> allTriples = getContent(objectCoreference);
            ArrayList<String> equivalent_uris = new ArrayList<>();
            for (ArrayList<String> triple : allTriples) {
                //retrieve the object of the triple i.e. the equivalent uri
                if(triple!=null && triple.size()>2 && triple.get(0).contains(uri))
                equivalent_uris.add(triple.get(2));
            }
            return equivalent_uris;
        } catch (Exception ex) {
            Logger.getLogger(LODsyndesisRestClient.class.getName()).log(Level.SEVERE, null, ex);
            ArrayList<String> equivalent_uris = new ArrayList<>();
            equivalent_uris.add(uri);
            return equivalent_uris;
        }
    }

    /**
     * Used for the evaluation to match triples.
     *
     * @param uri
     * @return Triples of sameAs entities
     */
    public ArrayList<String> getEquivalentEntityEvaluation(String uri) {
        try {
            serviceName = "objectCoreference";
            objectCoreference = new HttpGet(URL + "/" + serviceName + "?uri=" + uri);
            objectCoreference.addHeader(ACCEPT, "text/plain");
            objectCoreference.addHeader(CONTENT_TYPE, "application/n-triples");

            ArrayList<ArrayList<String>> allTriples = getContent(objectCoreference);
            ArrayList<String> equivalent_uris = new ArrayList<>();
            for (ArrayList<String> triple : allTriples) {

                if (triple.size() != 3) {
                    return new ArrayList<>(Arrays.asList(uri));
                }
                //retrieve the object of the triple i.e. the equivalent uri
                //remove the first and last character since the uris are enclosed in <...>
                equivalent_uris.add(triple.get(2).substring(1, triple.get(2).length() - 1));
            }

            return equivalent_uris;
        } catch (Exception ex) {
            Logger.getLogger(LODsyndesisRestClient.class.getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<>(Arrays.asList(uri));
        }
    }

    /**
     * Used to receive all facts (triples) about a given entity.
     *
     * @param uri
     * @return quadruples of triples (facts) and their provenance (KB from which
     * they derived from)
     */
    public ArrayList<ArrayList<String>> getAllFacts(String uri) {
        try {
            serviceName = "allFacts";
            allFacts = new HttpGet(URL + "/" + serviceName + "?uri=" + uri);
            allFacts.addHeader(ACCEPT, "application/n-quads");
            allFacts.addHeader(CONTENT_TYPE, "application/n-quads");

            ArrayList<ArrayList<String>> allQuads = getContent(allFacts);

            return allQuads;
        } catch (IOException ex) {
            Logger.getLogger(LODsyndesisRestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    // https://demos.isl.ics.forth.gr/LODsyndesis/rest-api/datasetDiscovery?dataset=http://collection.britishmuseum.org/&connections_number=5&subset_size=triads&measurement_type=Entities

    public ArrayList<String> datasetDiscoveryRequest(String dataset, int connections,String subsetSize,String mType) {
        try {
            serviceName = "datasetDiscovery";
            String choices="?dataset="+dataset+"&connections_number="+connections+
                    "&subset_size="+subsetSize+"&measurement_type="+mType;
            datasetDiscovery = new HttpGet(URL + "/" + serviceName + choices);
            datasetDiscovery.addHeader(ACCEPT, "application/json");
            datasetDiscovery.addHeader(CONTENT_TYPE, "application/json");

            ArrayList<String> allQuads = getJsonContentDatasetDiscovery(datasetDiscovery);
            //ArrayList<ArrayList<String>> allQuads = getContent(datasetDiscovery);
            return allQuads;
        } catch (IOException ex) {
            Logger.getLogger(LODsyndesisRestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Used to execute the request, receive the response in JSON format and
     * produce an interpretable structure with it.
     *
     * @param request
     * @return An interpretable structure that contains current service
     * response.
     * @throws IOException
     */
    private ArrayList<String> getJsonContentDatasetDiscovery(HttpGet request) throws IOException {

        try {
            HttpResponse response = client.execute(request);

            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            ArrayList<String> result = new ArrayList<>();
            String line = rd.readLine();
            // If there is an error, return an empty arrayList
            if (line.startsWith("<!DOCTYPE")) {
                Logger.getLogger(LODsyndesisRestClient.class.getName()).log(Level.WARNING, line);
                return new ArrayList<>();
            }

            JSONObject jsonObject = new JSONObject("{candidates: " + line + "}");
            JSONArray candidates = jsonObject.getJSONArray(("candidates"));

            for (int i = 0; i < candidates.length(); i++) {
                JSONObject uri = candidates.getJSONObject(i);
                String datasets = uri.getString("datasets").replaceAll("\"", "").replace("[", "").replace("]", "");
                result.add(datasets + "\t" + uri.getString("commonResources"));
            }

            return result;
        } catch (JSONException ex) {
            Logger.getLogger(LODsyndesisRestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Used to check for a given fact, i.e triples match a given
     * subject-predicate tuple.
     *
     * @param uri
     * @param fact
     * @return quadruples of triples (facts) and their provenance (KB from which
     * they derived from)
     */
    public ArrayList<ArrayList<String>> checkFact(String uri, String fact) {
        try {
            serviceName = "factChecking";
            String URLEncodedFact = getURLEncodedFact(fact);
            factChecking = new HttpGet(URL + "/" + serviceName + "?uri=" + uri + "&fact=" + URLEncodedFact);
            factChecking.addHeader(ACCEPT, "application/n-quads");
            factChecking.addHeader(CONTENT_TYPE, "application/n-quads");

            ArrayList<ArrayList<String>> allQuads = getContent(factChecking);

            return allQuads;
        } catch (IOException ex) {
            Logger.getLogger(LODsyndesisRestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Used to check for a given fact, i.e triples match a given
     * subject-predicate tuple, where the fact is treated as correct if the
     * number of given words it contains exceed the given thershold.
     *
     * @param uri
     * @param fact
     * @param thres
     * @return quadruples of triples (facts) and their provenance (KB from which
     * they derived from)
     */
    public ArrayList<ArrayList<String>> checkFact(String uri, String fact, double thres) {
        try {
            serviceName = "factChecking";
            String URLEncodedFact = getURLEncodedFact(fact);
            factChecking = new HttpGet(URL + "/" + serviceName + "?uri=" + uri + "&fact=" + URLEncodedFact + "&threshold=" + df.format(thres));
            factChecking.addHeader(ACCEPT, "application/n-quads");
            factChecking.addHeader(CONTENT_TYPE, "application/n-quads");

            ArrayList<ArrayList<String>> allQuads = getContent(factChecking);

            return allQuads;
        } catch (IOException ex) {
            Logger.getLogger(LODsyndesisRestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Used to check for a given fact, i.e triples match a given
     * subject-predicate tuple, where the fact is treated as correct if the
     * number of given words it contains exceed the given thershold.
     *
     * @param uri
     * @param fact
     * @param thres
     * @return quadruples of triples (facts) and their provenance (KB from which
     * they derived from)
     */
    public ArrayList<ArrayList<String>> checkFactAsJSON(String uri, String fact, double thres) {
        try {
            serviceName = "factChecking";
            String URLEncodedFact = getURLEncodedFact(fact);
            factChecking = new HttpGet(URL + "/" + serviceName + "?uri=" + uri + "&fact=" + URLEncodedFact + "&threshold=" + df.format(thres));
            factChecking.addHeader(ACCEPT, "application/json");
            factChecking.addHeader(CONTENT_TYPE, "application/json");
            System.out.println(factChecking);
            ArrayList<ArrayList<String>> allQuads = getContent(factChecking);

            return allQuads;
        } catch (IOException ex) {
            Logger.getLogger(LODsyndesisRestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public ArrayList<ArrayList<String>> getCardinalityAsJSON(String uri) {
        try {
            serviceName = "entityCardinality";
            entityCardinality = new HttpGet(URL + "/" + serviceName + "?entity=" + uri);
            entityCardinality.addHeader(ACCEPT, "application/json");
            entityCardinality.addHeader(CONTENT_TYPE, "application/json");
            System.out.println(entityCardinality);
            ArrayList<ArrayList<String>> allQuads = getContent(entityCardinality);

            return allQuads;
        } catch (IOException ex) {
            Logger.getLogger(LODsyndesisRestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Used to match candidate entities to a given URI. Finds all URIs, whose
     * suffix starts with that Keyword
     *
     * @param keyword
     * @return ArrayList<String> candidateEntities
     */
    public ArrayList<String> getEntityFromKeyWord(String keyword) {

        try {
            serviceName = "keywordEntity";
            keywordEntity = new HttpGet(URL + "/" + serviceName + "?keyword=" + keyword.trim().replaceAll(" ", "_"));
            keywordEntity.addHeader(ACCEPT, "application/json");
            keywordEntity.addHeader(CONTENT_TYPE, "application/json");
            // System.out.println(keywordEntity);
            ArrayList<String> candidateEntities = getJsonContent(keywordEntity);

            return candidateEntities;
        } catch (IOException ex) {
            Logger.getLogger(LODsyndesisRestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Used to execute the request, receive the response in JSON format and
     * produce an interpretable structure with it.
     *
     * @param request
     * @return An interpretable structure that contains current service
     * response.
     * @throws IOException
     */
    private ArrayList<String> getJsonContent(HttpGet request) throws IOException {

        try {
            HttpResponse response = client.execute(request);

            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            ArrayList<String> result = new ArrayList<>();
            String line = rd.readLine();
            // If there is an error, return an empty arrayList
            if (line.startsWith("<!DOCTYPE")) {
                Logger.getLogger(LODsyndesisRestClient.class.getName()).log(Level.WARNING, line);
                return new ArrayList<>();
            }

            JSONObject jsonObject = new JSONObject("{candidates: " + line + "}");
            JSONArray candidates = jsonObject.getJSONArray(("candidates"));

            for (int i = 0; i < candidates.length(); i++) {
                JSONObject uri = candidates.getJSONObject(i);
                result.add(uri.getString("uri"));
            }

            return result;
        } catch (JSONException ex) {
            Logger.getLogger(LODsyndesisRestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Used to transform a fact (predicate into URL valid substring). i.e.
     * replaces white spaces with %20.
     *
     * @param fact
     * @return return fact as a valid URL substring.
     */
    private String getURLEncodedFact(String fact) {
        String URLEncodedFact = "";
        String[] factSplited = fact.split(" ");
        int cnt = 0;
        for (String subFact : factSplited) {
            cnt++;
            if (cnt == factSplited.length) {
                URLEncodedFact += subFact;
            } else {
                URLEncodedFact += subFact + "%20";
            }
        }
        return URLEncodedFact;
    }

    /**
     * Used to execute the request, receive the response in n-quads or n-triples
     * format and produce an interpretable structure with it.
     *
     * @param request
     * @return An interpretable structure that contains current service
     * response.
     * @throws IOException
     */
    private ArrayList<ArrayList<String>> getContent(HttpGet request) throws IOException {

        HttpResponse response = client.execute(request);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        String line = "";

        while ((line = rd.readLine()) != null) {
            String[] lineSplited = line.split("\\s+");
            ArrayList<String> lineSplitedClean = new ArrayList<>();
            for (String lineUnit : lineSplited) {
                if (lineUnit.equals(".")) {
                    continue;
                } else {
                    lineSplitedClean.add(lineUnit);
                }
            }
            result.add(lineSplitedClean);
        }
        return result;
    }

    public ArrayList<String> objectCoreference(String URI, boolean print) {
        System.out.println("Find all the equivalent URIs of " + URI);
        ArrayList<String> results = this.getEquivalentEntity(URI);
        if (print == true) {
            for (String res : results) {
                System.out.println(res);
            }
            System.out.println("=======================\n");
        }
        return results;
    }

    public ArrayList<String> keywordToEntity(String keyword, boolean print) {
        System.out.println("Find all the URIs starting with the keyword " + keyword);
        ArrayList<String> results = this.getEntityFromKeyWord(keyword);
        if (print == true) {
            for (String res : results) {
                System.out.println(res);
            }
            System.out.println("=======================\n");
        }
        return results;
    }

    public ArrayList<String> allFacts(String entity, boolean print) {
        System.out.println("Find all the facts for the entity " + entity);
        ArrayList<ArrayList<String>> results2 = this.getAllFacts(entity);
        //results2 = chanel.checkFact(entity, fact, threshold);
        ArrayList<String> results = new ArrayList<String>();
        for (ArrayList<String> array : results2) {
            String quad = "";
            for (String str : array) {
                quad += str + " ";
            }
            results.add(quad);
        }
        if (print == true) {
            for (String str : results) {
                System.out.println(str);
            }
            System.out.println("=======================\n");

        }

        return results;
    }

     public ArrayList<String> checkAFact(String entity, String fact, double threshold, boolean print) {
        System.out.println("Find all the facts for the entity " + entity);
        ArrayList<ArrayList<String>> results2 = this.checkFact(entity, fact, threshold);
      
        ArrayList<String> results = new ArrayList<String>();
        String quad = "";
        int i=0;
        for (ArrayList<String> array : results2) {
            
            for (String str : array) {
                quad += str + " ";
            }
            if(i%2==1){
                results.add(quad);
                quad="";
            }
            i++;
        }
        if (print == true) {
            for (String str : results) {
                System.out.println(str);
            }
            System.out.println("=======================\n");

        }

        return results;
    }
     
    public ArrayList<String> datasetDiscovery(String dataset,int connections,String subsetSize,String mtype,boolean print){
     System.out.println("Dataset Discovery");
     ArrayList<String> results=this.datasetDiscoveryRequest(dataset, connections, subsetSize, mtype);

      if(print ==true){
          for(String str:results){
              System.out.println(str);
          }
         System.out.println("=======================\n");

      }
      return results;
        
    }

    
    public TreeMap<String, String> relatedFacts(String URI, String words, double thres){
        ArrayList<String> results=this.checkAFact(URI,words, thres,false);
        TreeMap<String,String> map=new TreeMap<>();
        for(String x:results){
            String [] split=x.split(" ");
            if(!split[1].equals("<"+URI+">")){
                if(!map.containsKey(split[1])){
                    
                    map.put(split[1],split[2]+"\t"+split[4]);
                }
                else{
                    String y=map.get(split[1])+"\n"+split[2]+"\t"+split[4];
                      map.put(split[1],y);
                    
                }
            }
            else if(!split[3].equals("<"+URI+">")){
                if(!map.containsKey(split[3])){
                    
                    map.put(split[3],split[2]+"\t"+split[4]);
                }
                else{
                    String y=map.get(split[3])+"\n"+split[2]+"\t"+split[4];
                      map.put(split[3],y);
                    
                }
            }
            
        }
        for(String x:map.keySet()){
            System.out.println(x);
            System.out.println(map.get(x));
            System.out.println("\n");
        }
        return map;
    }
    
    
    public static void main(String[] args) {
        LODsyndesisRestClient chanel = new LODsyndesisRestClient();
        boolean print = true;

   //Keyword to URI
       String keyword = "Aristotle";
        //chanel.keywordToEntity(keyword, print);

        // Object Coreference
        String URI = "http://dbpedia.org/resource/The_Lord_of_the_Rings_(film_series)";
        String words="The_Return_Of_The_King /Christopher_Lee /Orlando_Bloom /Elijah_Wood /New_Zealand Wingnut_Films";
        
        String URI2="http://dbpedia.org/resource/New_Zealand";
        String words2="http://dbpedia.org/resource/Zorba_the_Greek Peace_Award http://dbpedia.org/resource/Christ_Recrucified http://dbpedia.org/resource/English_language"+
                " http://dbpedia.org/resource/Iliad http://dbpedia.org/resource/Martinengo http://dbpedia.org/resource/The_Last_Temptation_of_Christ http://dbpedia.org/resource/Nobel_Prize http://dbpedia.org/resource/Heraklion http://dbpedia.org/resource/Zorba_the_Greek";
        //chanel.allDatasets(URI, print);
        String[] split=words2.split(" ");
        double thres=1.0/(double) split.length;
        System.out.println(thres);
        //chanel.relatedFacts(URI2, words2, thres);
        String uris="http://dbpedia.org/resource/Aristotle%20http://dbpedia.org/resource/Socrates";
        String ebdd=chanel.datasetDiscoveryEntities(uris,5,10,"coverage");
        System.out.println(ebdd);
      //  System.out.println(res.get(0).size());
     //   System.out.println(res.get(0).get(0).replace("}","}\n"));
        
        //All Values for an entity
       // String entity1 = "http://dbpedia.org/resource/Naxos";
      //  chanel.allFacts(entity1, print);
        
        
        // Check values for a fact
//        String entity2 = "http://dbpedia.org/resource/Aristotle";
//        String fact = "birth place";
//        double threshold = 1.0;
//        chanel.checkAFact(entity2, fact, threshold, print);
//
//        // Dataset Discovery
//        String dataset="http://collection.britishmuseum.org/";
//        int connections=10;
//        String subsetSize="triads";
//        String mType="Entities";
//        chanel.datasetDiscovery(dataset, connections, subsetSize, mType, print);

    }
    
    public ArrayList<ArrayList<String>> getAllDatasets(String uri) {
        try {
            serviceName = "objectCoreference";
            allDatasets= new HttpGet(URL + "/" + serviceName + "?uri=" + uri+"&provenance=true");
            allDatasets.addHeader(ACCEPT, "text/plain");
            allDatasets.addHeader(CONTENT_TYPE, "application/n-triples");

            ArrayList<ArrayList<String>> allQuads = getContent(allDatasets);

            return allQuads;
        } catch (IOException ex) {
            Logger.getLogger(LODsyndesisRestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public ArrayList<String> allDatasets(String entity, boolean print) {
        System.out.println("Find all the datasets for the entity " + entity);
            ArrayList<ArrayList<String>> results2 = this.getAllDatasets(entity);
        //results2 = chanel.checkFact(entity, fact, threshold);
        ArrayList<String> results = new ArrayList<String>();
        for (ArrayList<String> array : results2) {
            String tr = "";
            for (String str : array) {
                tr += str + " ";
            }
            if(!results.contains(tr))
             results.add(tr);
        }
        if (print == true) {
            for (String str : results) {
                System.out.println(str);
            }
            System.out.println("=======================\n");

        }

        return results;
    }
    
    /**
     * Used to check for a given fact, i.e triples match a given
     * subject-predicate tuple, where the fact is treated as correct if the
     * number of given words it contains exceed the given thershold.
     *
     * @param uri
     * @param fact
     * @param thres
     * @return quadruples of triples (facts) and their provenance (KB from which
     * they derived from)
     */
    public ArrayList<ArrayList<String>> provenanceAsJSON(String uri) {
        try {
            serviceName = "objectCoreference";
            allDatasets= new HttpGet(URL + "/" + serviceName + "?uri=" + uri+"&provenance=true");
            
            allDatasets.addHeader(ACCEPT, "application/json");
            allDatasets.addHeader(CONTENT_TYPE, "application/json");
         
            ArrayList<ArrayList<String>> allQuads = getContent(allDatasets);
                       return allQuads;
        } catch (IOException ex) {
            Logger.getLogger(LODsyndesisRestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public ArrayList<ArrayList<String>> objectCoreferenceAsJSON(String uri) {
        try {
            serviceName = "objectCoreference";
            objectCoreference= new HttpGet(URL + "/" + serviceName + "?uri=" + uri);
            
            objectCoreference.addHeader(ACCEPT, "application/json");
            objectCoreference.addHeader(CONTENT_TYPE, "application/json");
            
            ArrayList<ArrayList<String>> allQuads = getContent(objectCoreference);

            return allQuads;
        } catch (IOException ex) {
            Logger.getLogger(LODsyndesisRestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    
        public ArrayList<ArrayList<String>> allFactsAsJSON(String uri) {
        try {
            serviceName = "allFacts";
            allFacts = new HttpGet(URL + "/" + serviceName + "?uri=" + uri);
            allFacts.addHeader(ACCEPT, "application/json");
            allFacts.addHeader(CONTENT_TYPE, "application/json");
            
            ArrayList<ArrayList<String>> allQuads = getContent(allFacts);

            return allQuads;
        } catch (IOException ex) {
            Logger.getLogger(LODsyndesisRestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

        
     /**
     * Used to search for a given entity and its same as entities.
     *
     * @param uris
     * @return Triples of sameAs entities
     */
    public String datasetDiscoveryEntities(String uris,int subset,int topK, String mtype) {
        try {
           // String urlNew="http://localhost:8083/LODsyndesis/rest-api";
            serviceName = "entityBasedDatasetDiscovery";
            entitiesDD = new HttpGet(URL + "/" + serviceName + "?entities=" + uris+"&subsetSize="+subset+"&topK="+topK+"&measurementType="+mtype);
            entitiesDD.addHeader(ACCEPT, "text/csv");
            entitiesDD.addHeader(CONTENT_TYPE, "text/csv");
            //System.out.println(objectCoreference);
            ArrayList<ArrayList<String>> allTriples = getContent(entitiesDD);
            String output="";
            for (ArrayList<String> triple : allTriples) {
                for(int i=0;i<triple.size();i++){
                    output+=triple.get(i);
                    if(i+1==triple.size()){
                        output+="\n";
                    }
                    else{
                        output+=",";
                    }
                }
               // System.out.println(triple);
                //retrieve the object of the triple i.e. the equivalent uri
                //equivalent_uris.add(triple.get(0));
            }
            
            return output;
        } catch (Exception ex) {
            Logger.getLogger(LODsyndesisRestClient.class.getName()).log(Level.SEVERE, null, ex);
            ArrayList<String> equivalent_uris = new ArrayList<>();
            //equivalent_uris.add(uri);
            return "";
        }
    }
    
    
}
