
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
import java.util.HashMap;
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
public class LODsyndesisIERestClient {

    private HttpClient client;
    private HttpGet recognizedEntities;

    private static final String URL = "https://demos.isl.ics.forth.gr/LODsyndesisIE/rest-api";
    private String serviceName;

    private DecimalFormat df = new DecimalFormat(".##");

    /**
     * Used to open connection with client and LODsyndesis
     */
    public LODsyndesisIERestClient() {
        client = HttpClientBuilder.create().build();
        df.setRoundingMode(RoundingMode.DOWN);

    }

    /**
     * Used to receive all facts (triples) about a given entity.
     *
     * @param uri
     * @return quadruples of triples (facts) and their provenance (KB from which
     * they derived from)
     */
    
     public ArrayList<String> getEntities(String uri) {
        try {
            serviceName = "getEntities";
            recognizedEntities = new HttpGet(URL + "/" + serviceName + "?text=" + uri+"&ERtools=DBS_WAT&equivalentURIs=false&provenance=false");
            recognizedEntities.addHeader(ACCEPT, "text/tsv");
            recognizedEntities.addHeader(CONTENT_TYPE, "application/n-triples");
            System.out.println(recognizedEntities);
            ArrayList<ArrayList<String>> allTriples = getContent(recognizedEntities);
            //System.out.println(allTriples);
            ArrayList<String> results=new ArrayList<String>();
            for (ArrayList<String> triple : allTriples) {
                //retrieve the object of the triple i.e. the equivalent uri
                String entity="";
                for(String x:triple){
                     if(!x.startsWith("https://demos.isl.ics.forth.gr/"))
                        entity+=x+"\t";
                }
                //System.out.println(entity);
                if(entity!=null)
                results.add(entity);
            }
            return results;
        } catch (Exception ex) {
            Logger.getLogger(LODsyndesisRestClient.class.getName()).log(Level.SEVERE, null, ex);
            ArrayList<String> equivalent_uris = new ArrayList<>();
            equivalent_uris.add(uri);
            return equivalent_uris;
        }
    }
    
    // https://demos.isl.ics.forth.gr/LODsyndesis/rest-api/datasetDiscovery?dataset=http://collection.britishmuseum.org/&connections_number=5&subset_size=triads&measurement_type=Entities

   
   

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
                Logger.getLogger(LODsyndesisIERestClient.class.getName()).log(Level.WARNING, line);
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
            Logger.getLogger(LODsyndesisIERestClient.class.getName()).log(Level.SEVERE, null, ex);
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

    
     
   

   
    
    public static void main(String[] args) {
        LODsyndesisIERestClient chanel = new LODsyndesisIERestClient();
        boolean print = true;

   //Keyword to URI
     chanel.getEntities("Aristotle%20born%20in%20Stagira");
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
    
     
        
     /**
     * Used to search for a given entity and its same as entities.
     *
     * @param uris
     * @return Triples of sameAs entities
     */
 
    
    
}
