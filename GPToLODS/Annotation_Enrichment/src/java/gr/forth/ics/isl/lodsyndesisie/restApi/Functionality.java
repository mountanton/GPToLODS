
/*  This code belongs to the Semantic Access and Retrieval (SAR) group of the
 *  Information Systems Laboratory (ISL) of the
 *  Institute of Computer Science (ICS) of the
 *  Foundation for Research and Technology - Hellas (FORTH)
 *  Nobody is allowed to use, copy, distribute, or modify this work.
 *  It is published for reasons of research results reproducibility.
 *  (c) 2020 Semantic Access and Retrieval group, All rights reserved
 */
package gr.forth.ics.isl.lodsyndesisie.restApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import gr.forth.ics.isl.demoExternal.main.ExternalKnowledgeDemoMain;
import gr.forth.ics.isl.demoExternal.LODsyndesis.LODsyndesisRestClient;
import gr.forth.ics.isl.demoExternal.main.EntityRecognitionProcess;
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
import java.util.HashMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jettison.json.JSONException;

/**
 *
 * @author micha
 */
public class Functionality {

    public static ExternalKnowledgeDemoMain ekdm = new ExternalKnowledgeDemoMain();

   
   

    public String[][] getEntities(String input, String toolType, boolean equiv, boolean prov) throws IOException, JSONException {
        EntityRecognitionProcess erp=new EntityRecognitionProcess();
        String[][] results2 = erp.runWithTool(input, toolType,false);
        HashMap<String, String> entities = new HashMap<String, String>();
        int j = 1;
        int cnt = 2;
        if (equiv == true) {
            cnt++;
        }
        if (prov == true) {
            cnt++;
        }
        String[][] entitiesToURIs = new String[results2.length - 1][cnt + 1];
        entitiesToURIs[0][0]="Entity";
        entitiesToURIs[0][1]="DBpediaURI";
        entitiesToURIs[0][2]="LODsyndesisURI";
        if(equiv==true){
              entitiesToURIs[0][3]="EquivalentURIs";
        }
        if(prov==true){
              entitiesToURIs[0][cnt]="Provenance";

        }


        if (results2 != null) {
            for (int i = 1; i < results2.length - 1; i++) {
                String entity = "", href = "", before = "";
                if (results2[i][0].contains("<br>")) {
                    String[] splitBR = results2[i][0].split("<br>");
                    entity = splitBR[1].split("\t")[0];
                     href = splitBR[1].split("\t")[1];
                    // before = splitBR[0] + "<br>";
                } else {
                    entity = results2[i][0].split("\t")[0];
                       href = results2[i][0].split("\t")[1];
                }
                entitiesToURIs[j][0] = entity;
                entitiesToURIs[j][1] = ekdm.getEntityURIs().get(entity);
                 entitiesToURIs[j][2]=href;
                if (equiv == true) {
                    entitiesToURIs[j][3] = "";
                    LODsyndesisRestClient rest = new LODsyndesisRestClient();
                    ArrayList<String> resTemp = rest.objectCoreference(ekdm.getEntityURIs().get(entity), false);
                    for (String x : resTemp) {
                        String uri = x.replace("<", "").replace(">", "");
                        if (!uri.equals(ekdm.getEntityURIs().get(entity))) {
                            entitiesToURIs[j][3] += uri + ",";
                        }
                    }
                    if(entitiesToURIs[j][3].length()>1)
                        entitiesToURIs[j][3] = entitiesToURIs[j][3].substring(0, entitiesToURIs[j][3].length() - 1);
                    
                }
                if (prov == true) {
                    entitiesToURIs[j][cnt] = "";
                    LODsyndesisRestClient rest = new LODsyndesisRestClient();
                    ArrayList<String> resTemp = rest.allDatasets(ekdm.getEntityURIs().get(entity), false);
                    for (String x : resTemp) {
                        if(!x.contains("dbpedia.org/resource"))
                            continue;
                        String uri = x.replace("<", "").replace(">", "");
                        if (!uri.equals(ekdm.getEntityURIs().get(entity))) {
                            String dataset = uri.split(" ")[2];
                            if (!dataset.contains("dbpedia.org/resource")) {
                                entitiesToURIs[j][cnt] += uri.split(" ")[2] + ",";
                            }
                        }
                    }
                    if(entitiesToURIs[j][cnt].length()>0)
                    entitiesToURIs[j][cnt] = entitiesToURIs[j][cnt].substring(0, entitiesToURIs[j][cnt].length() - 1);
                }

                j++;
                // results2[i][0] = before + "<a href=\"" + href + "\">" + results2[i][0] + "</a>";
            }
        }
        return entitiesToURIs;
    }

    public String dbpediaQuery(String query, String endpoint) throws UnsupportedEncodingException, MalformedURLException, IOException {
        // //////System.out.println(query);
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
            //////System.out.println(input);
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

    
    
    public String getType(String URI) throws UnsupportedEncodingException, MalformedURLException, IOException {
        ArrayList<String> inputURIs = new ArrayList<String>();
        String query1, query2;// = @"select distinct ?s where {?s a <" + URI + ">}";

        query1 = "select distinct ?s where {<" + URI + "> a ?s .filter(regex(?s,'schema.org'))}";

        query2 = "select distinct ?s where {<" + URI + "> a ?s1 . ?s1 <http://www.w3.org/2002/07/owl#equivalentClass> ?s .filter(regex(?s,'schema.org'))}";

        String service = "https://dbpedia.org/sparql";
        String input = this.dbpediaQuery(query1, service);
        for (String k : input.split("\n")) {
            if (!k.equals("")) {
                inputURIs.add(k);
            }
        }
        if (inputURIs.isEmpty()) {
            input = this.dbpediaQuery(query2, service);
            for (String k : input.split("\n")) {
                if (!k.equals("")) {
                    inputURIs.add(k);
                }
            }
        }
        if (inputURIs.isEmpty()) {
            return "Thing";
        } else {
            //////System.out.println(inputURIs);
            String ret = inputURIs.get(0).replace("http://schema.org/", "");
            return ret;
        }
        //return inputURIs;
    }
    
    public String returnFirstURI(String input,String[][] results2) throws MalformedURLException, IOException {
          HashMap<String, String> entities = new HashMap<String, String>();
                if (results2 != null) {
                    for (int i = 1; i < results2.length - 1; i++) {
                        String entity = "", href = "", before = "";
                        if (results2[i][0].contains("<br>")) {
                            String[] splitBR = results2[i][0].split("<br>");
                            entity = splitBR[1].split("\t")[0];
                            href = splitBR[1].split("\t")[1];
                            before = splitBR[0] + "<br>";
                        } else {
                            entity = results2[i][0].split("\t")[0];
                            href = results2[i][0].split("\t")[1];
                        }
                        String entity2 = Character.toUpperCase(entity.charAt(0)) + entity.substring(1);
                        // entities.put(entity, href);
                        //   entities.put(entity2, href);
                        results2[i][0] = entity2;
                        if (entity2.contains(" ")) {
                            String[] split = entity2.split(" ");
                            String newStr = "";
                            for (String k : split) {
                                newStr += Character.toUpperCase(k.charAt(0)) + k.substring(1) + " ";
                            }
                            newStr = newStr.substring(0, newStr.length() - 1);
                            entities.put(newStr, href);
                            results2[i][0] = newStr;
                        } else {
                            entities.put(entity2, href);
                        }
                        results2[i][0] = before + "<a href=\"" + href + "\">" + results2[i][0] + "</a>";
                    }
                }
                String text = "";
                input = input.replace(",", "!comma");
                input = input.replace(".", "!dot");
                input = input.replace(")", "!par");
                for (String str : entities.keySet()) {
                    if (!str.contains(".")) {
                        input = input.replaceAll("(?i)" + str + " ", "<a href=\"Link:" + str + "\"><b>" + str + "</b></a> ");
                        input = input.replaceAll("(?i)" + str + "!comma", "<a href=\"Link:" + str + "\"><b>" + str + "</b></a>,");
                        input = input.replaceAll("(?i)" + str + "!dot", "<a href=\"Link:" + str + "\"><b>" + str + "</b></a>.");
                        input = input.replaceAll("(?i)" + str + "!par", "<a href=\"Link:" + str + "\"><b>" + str + "</b></a>)");
                        input = input.replace("Link:" + str, entities.get(str));
                    }
                }
                input = input.replace("!comma", ",");
                input = input.replace("!dot", ".");
                input = input.replace("!par", ")");

                for (String str : entities.keySet()) {
                    if (str.contains(".")) {
                        input = input.replaceAll("(?i)" + str, "<a href=\"Link:" + str + "\"><b>" + str + "</b></a>");
                        input = input.replace("Link:" + str, entities.get(str));
                    }
                }
        for (String k : input.split("<a")) {
            if (k.contains("</a>")) {
                int i = 0;
                for (String p : k.split("</a>")) {
                    if (i == 0) {
                        String about = "http://dbpedia.org/resource/" + p.split("queryType")[0].replace(" href=\"https://demos.isl.ics.forth.gr/lodsyndesis/RunQuery?URI=dbpedia.org$resource$", "").replace("&", "");
                        return about;

                        //RDFa+="\n</div>";
                    }
                }
            }
        }

        return "";

    }
   public String getImage(String uri) {
        try {
            long startTime = System.currentTimeMillis();

            String query = "select distinct ?s where {<" + uri + "> <http://dbpedia.org/ontology/thumbnail> ?s}"; // http://dbpedia.org/resource/Category:FIFA_100
            String service = "https://dbpedia.org/sparql";
            String res = this.dbpediaQuery(query, service);
            long estimatedTime = System.currentTimeMillis() - startTime;
           // System.out.println(uri + " " + (double) estimatedTime / (1000));
            return res;
        } catch (MalformedURLException ex) {
            Logger.getLogger(ExternalKnowledgeDemoMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExternalKnowledgeDemoMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    } 
    
   
    public String text2JSON(String text, String tool,boolean moreURIs) throws MalformedURLException, IOException, JSONException {
        EntityRecognitionProcess erp=new EntityRecognitionProcess();
        String[][] results2 = erp.runWithTool(text, tool,false);
        HashMap<String, String> entities = new HashMap<String, String>();
         ArrayList<TextPart> textparts=new ArrayList<TextPart>();
        if (results2 != null) {
            for (int i = 1; i < results2.length - 1; i++) {
                String entity = "", href = "", before = "";
                if (results2[i][0].contains("<br>")) {
                    String[] splitBR = results2[i][0].split("<br>");
                    entity = splitBR[1].split("\t")[0];
                    href = splitBR[1].split("\t")[1];
                    before = splitBR[0] + "<br>";
                } else {
                    entity = results2[i][0].split("\t")[0];
                    href = results2[i][0].split("\t")[1];
                }

                String entity2 = Character.toUpperCase(entity.charAt(0)) + entity.substring(1);

                results2[i][0] = entity2;
                if (entity2.contains(" ")) {
                    String[] split = entity2.split(" ");
                    String newStr = "";
                    for (String k : split) {
                        newStr += Character.toUpperCase(k.charAt(0)) + k.substring(1) + " ";
                    }
                    newStr = newStr.substring(0, newStr.length() - 1);
                    entities.put(newStr, href);
                } else {
                    entities.put(entity2, href);

                }
            }
        }

        String input = text + " ";
        input = input.replace(",", "!comma");
        input = input.replace(".", "!dot");
        input = input.replace(")", "!par");
        for (String str : entities.keySet()) {
            if (!str.contains(".")) {
                input = input.replaceAll("(?i)" + str + " ", "<a href=\"Link:" + str + "\"><b>" + str + "</b></a> ");
                input = input.replaceAll("(?i)" + str + "!comma", "<a href=\"Link:" + str + "\"><b>" + str + "</b></a>,");
                input = input.replaceAll("(?i)" + str + "!dot", "<a href=\"Link:" + str + "\"><b>" + str + "</b></a>.");
                input = input.replaceAll("(?i)" + str + "!par", "<a href=\"Link:" + str + "\"><b>" + str + "</b></a>)");
                input = input.replace("Link:" + str, entities.get(str));
            }
        }
        input = input.replace("!comma", ",");
        input = input.replace("!dot", ".");
        input = input.replace("!par", ")");

        for (String str : entities.keySet()) {
            if (str.contains(".")) {
                input = input.replaceAll("(?i)" + str, "<a href=\"Link:" + str + "\"><b>" + str + "</b></a>");
                input = input.replace("Link:" + str, entities.get(str));
            }
        }

        String RDFa = "<!DOCTYPE html>\n"
                + "<html>\n"
                + "<body>\n";
        for (String k : input.split("<a")) {
           
            if (!k.contains("</a>")) {
                if(k.trim().equals(""))
                    continue;
                 TextPart tp=new TextPart();
                tp.isEntity=false;
                        tp.img=null;
                        tp.textpart=k;
                        tp.lodsyndesisURL=null;
                        tp.moreInfo=null;
                        tp.type=null;
                        tp.otherURIs=null;
                        textparts.add(tp);
            } else {
                int i = 0;
                for (String p : k.split("</a>")) {
                    TextPart tp=new TextPart();
                    if (i == 0) {
                        tp.isEntity=true;
                        
                        tp.textpart=p.split("<b>")[1].replace("</b>", "");
                        tp.lodsyndesisURL=p.split("<b>")[0].replace(" href=", "").replaceAll("\"","").replace(">","");
                        tp.moreInfo=null;
                        
                        textparts.add(tp);
                        String about = "http://dbpedia.org/resource/" + p.split("queryType")[0].replace(" href=\"https://demos.isl.ics.forth.gr/lodsyndesis/RunQuery?URI=dbpedia.org$resource$", "").replace("&", "");
                        String defType = this.getType(about);
                        tp.type=defType;
                        tp.dbpediaURI=about;
                        tp.img=this.getImage(about).replace("\n","");
                        
                        if(moreURIs){
                        LODsyndesisRestClient rest = new LODsyndesisRestClient();
                        ArrayList<String> resTemp = rest.objectCoreference(about.trim(), false);
                        for (String x : resTemp) {
                            String uri = x.replace("<", "").replace(">", "");
                            tp.otherURIs.add(uri);
                        }}
                        else{
                            tp.otherURIs=null;
                        }

                       
                        i++;
                    } else {
                        tp.isEntity=false;
                        tp.img=null;
                        tp.textpart=p;
                        tp.lodsyndesisURL=null;
                        tp.moreInfo=null;
                        tp.type=null;
                          tp.otherURIs=null;
                        textparts.add(tp);
                        
                    }
                }
            }

        }
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        
        return gson.toJson(textparts);

    }
    
    public String text2RDFa(String text, String tool) throws MalformedURLException, IOException, JSONException {
        EntityRecognitionProcess erp=new EntityRecognitionProcess();
        String[][] results2 = erp.runWithTool(text, tool,false);
        HashMap<String, String> entities = new HashMap<String, String>();
        
        if (results2 != null) {
            for (int i = 1; i < results2.length - 1; i++) {
                String entity = "", href = "", before = "";
                if (results2[i][0].contains("<br>")) {
                    String[] splitBR = results2[i][0].split("<br>");
                    entity = splitBR[1].split("\t")[0];
                    href = splitBR[1].split("\t")[1];
                    before = splitBR[0] + "<br>";
                } else {
                    entity = results2[i][0].split("\t")[0];
                    href = results2[i][0].split("\t")[1];
                }

                String entity2 = Character.toUpperCase(entity.charAt(0)) + entity.substring(1);

                results2[i][0] = entity2;
                if (entity2.contains(" ")) {
                    String[] split = entity2.split(" ");
                    String newStr = "";
                    for (String k : split) {
                        newStr += Character.toUpperCase(k.charAt(0)) + k.substring(1) + " ";
                    }
                    newStr = newStr.substring(0, newStr.length() - 1);
                    entities.put(newStr, href);
                } else {
                    entities.put(entity2, href);

                }
            }
        }

        String input = text + " ";
        input = input.replace(",", "!comma");
        input = input.replace(".", "!dot");
        input = input.replace(")", "!par");
        for (String str : entities.keySet()) {
            if (!str.contains(".")) {
                input = input.replaceAll("(?i)" + str + " ", "<a href=\"Link:" + str + "\"><b>" + str + "</b></a> ");
                input = input.replaceAll("(?i)" + str + "!comma", "<a href=\"Link:" + str + "\"><b>" + str + "</b></a>,");
                input = input.replaceAll("(?i)" + str + "!dot", "<a href=\"Link:" + str + "\"><b>" + str + "</b></a>.");
                input = input.replaceAll("(?i)" + str + "!par", "<a href=\"Link:" + str + "\"><b>" + str + "</b></a>)");
                input = input.replace("Link:" + str, entities.get(str));
            }
        }
        input = input.replace("!comma", ",");
        input = input.replace("!dot", ".");
        input = input.replace("!par", ")");

        for (String str : entities.keySet()) {
            if (str.contains(".")) {
                input = input.replaceAll("(?i)" + str, "<a href=\"Link:" + str + "\"><b>" + str + "</b></a>");
                input = input.replace("Link:" + str, entities.get(str));
            }
        }

        String RDFa = "<!DOCTYPE html>\n"
                + "<html>\n"
                + "<body>\n";
        for (String k : input.split("<a")) {
            if (!k.contains("</a>")) {
                RDFa += k;
            } else {
                int i = 0;
                for (String p : k.split("</a>")) {
                    if (i == 0) {
                        String about = "http://dbpedia.org/resource/" + p.split("queryType")[0].replace(" href=\"https://demos.isl.ics.forth.gr/lodsyndesis/RunQuery?URI=dbpedia.org$resource$", "").replace("&", "");
                        String defType = this.getType(about);

                        String newStr = "\n<span vocab=\"http://schema.org/\" typeof=\"" + defType + "\" about=\"" + about + "\">\n"
                                + "<a property=\"url\"" + p;//
                        newStr = newStr.replace("<b>", "\n<span property=\"name\">").replace("</b>", "</span></a>");

                        LODsyndesisRestClient rest = new LODsyndesisRestClient();
                        ArrayList<String> resTemp = rest.objectCoreference(about.trim(), false);
                        for (String x : resTemp) {
                            String uri = x.replace("<", "").replace(">", "");
                            if (!uri.equals(about)) {
                                newStr += "\n<a property=\"sameAs\" href=\"" + uri + "\"></a>";
                            }
                        }

                        RDFa += newStr + "\n</span>\n";
                        i++;
                    } else {
                        RDFa += p;//+"\n";
                    }
                }
                //RDFa+="\n</div>";
            }

        }
        RDFa += "</body>\n" + "</html>";
        // RDFa=RDFa.replace("</span>\n,",",</span>\n").replace("</span>\n.","</span>\n");
        return RDFa;

    }

    public String getTriplesOfEntities(String input, String toolType) throws IOException, JSONException {
        EntityRecognitionProcess erp=new EntityRecognitionProcess();
        String[][] results2 = erp.runWithTool(input, toolType,false);
        HashMap<String, String> entities = new HashMap<String, String>();
        int j = 0;
        String output = "";
        if (results2 != null) {
            for (int i = 1; i < results2.length - 1; i++) {
                String entity = "", href = "", before = "";
                if (results2[i][0].contains("<br>")) {
                    String[] splitBR = results2[i][0].split("<br>");
                    entity = splitBR[1].split("\t")[0];
                    // href = splitBR[1].split("\t")[1];
                    // before = splitBR[0] + "<br>";
                } else {
                    entity = results2[i][0].split("\t")[0];
                    //   href = results2[i][0].split("\t")[1];
                }
                output += "#" + entity + "\n";

                LODsyndesisRestClient rest = new LODsyndesisRestClient();
                ArrayList<String> resTemp = rest.allFacts(ekdm.getEntityURIs().get(entity), false);
                for (String x : resTemp) {
                    output += x + "\n";
                }

                // results2[i][0] = before + "<a href=\"" + href + "\">" + results2[i][0] + "</a>";
            }
        }
        return output;
    }

    public String FindRelatedFacts(String text, String tool, String keyEntity) throws MalformedURLException, IOException, JSONException {
        EntityRecognitionProcess erp=new EntityRecognitionProcess();
        String[][] results2 = erp.runWithTool(text, tool,false);
        String output = "", words = "";
        if (keyEntity.equals("")) {
            keyEntity = this.returnFirstURI(text,results2);
        }
        for (String x : ekdm.getUnsortedURIs()) {
            if (!x.equals(keyEntity)) {
                words += x + " ";
            }
        }

        if (words.length() != 0) {

            LODsyndesisRestClient chanel = new LODsyndesisRestClient();

            words = words.substring(0, words.length() - 1);
            String[] split = words.split(" ");
            double thres = 1.0 / (double) split.length;
            //ntln(words);

            TreeMap<String, String> res = chanel.relatedFacts(keyEntity, words, thres);
            for (String x : res.keySet()) {
                for (String k : res.get(x).split("\n")) {
                    output += "<" + keyEntity + ">\t" + k.split("\t")[0] + "\t" + x + "\t" + k.split("\t")[1] + ".\n";
                }
            }
        }

        return output;
    }

    
      public String getEntitiesRDF(String input, String toolType, boolean equiv, boolean prov) throws IOException, JSONException {
        EntityRecognitionProcess erp=new EntityRecognitionProcess();
        String[][] results2 = erp.runWithTool(input, toolType,false);
        String output="";
        HashMap<String, String> entities = new HashMap<String, String>();
        int j = 0;
        int cnt = 1;
        if (equiv == true) {
            cnt++;
        }
        if (prov == true) {
            cnt++;
        }
        String[][] entitiesToURIs = new String[results2.length - 2][cnt + 1];
        if (results2 != null) {
            for (int i = 1; i < results2.length - 1; i++) {
                String entity = "", href = "", before = "";
                if (results2[i][0].contains("<br>")) {
                    String[] splitBR = results2[i][0].split("<br>");
                    entity = splitBR[1].split("\t")[0];
                     href = splitBR[1].split("\t")[1];
                    // before = splitBR[0] + "<br>";
                } else {
                    entity = results2[i][0].split("\t")[0];
                      href = results2[i][0].split("\t")[1];
                }
                output+="#"+entity+"\n";
                entitiesToURIs[j][0] = entity;
                entitiesToURIs[j][1] = ekdm.getEntityURIs().get(entity);
                output+= "<" + entitiesToURIs[j][1] + "> <http://www.w3.org/2002/07/owl#sameAs> <" + href + "> .\n";
                if (equiv == true) {
                    entitiesToURIs[j][2] = "";
                    output+="# Equivalent URIs of "+entity+"\n";
                    LODsyndesisRestClient rest = new LODsyndesisRestClient();
                    ArrayList<String> resTemp = rest.objectCoreference(ekdm.getEntityURIs().get(entity), false);
                    for (String x : resTemp) {
                        
                        String uri = x.replace("<", "").replace(">", "");
                        if (!uri.equals(ekdm.getEntityURIs().get(entity))) {
                            output+= "<" + entitiesToURIs[j][1] + "> <http://www.w3.org/2002/07/owl#sameAs> " + x + " .\n";

                        }
                    }
                   // entitiesToURIs[j][2] = entitiesToURIs[j][2].substring(0, entitiesToURIs[j][2].length() - 1);

                }
                if (prov == true) {
                     
                    entitiesToURIs[j][cnt] = "";
                    output+="# Provenance of "+entity+"\n";

                    LODsyndesisRestClient rest = new LODsyndesisRestClient();
                    ArrayList<String> resTemp = rest.allDatasets(ekdm.getEntityURIs().get(entity), false);
                    for (String x : resTemp) {
                        if(!x.contains("dbpedia.org/resource"))
                            continue;
                        output+=x+"\n";
                    }
                  //  entitiesToURIs[j][cnt] = entitiesToURIs[j][cnt].substring(0, entitiesToURIs[j][cnt].length() - 1);
                }

                j++;
                // results2[i][0] = before + "<a href=\"" + href + "\">" + results2[i][0] + "</a>";
            }
        }
        return output;
    }
      
     public String textEntitiesDD(String text, String tool, int subset,int topK,String mType) throws MalformedURLException, IOException, JSONException {
        EntityRecognitionProcess erp=new EntityRecognitionProcess();
        String[][] results2 = erp.runWithTool(text, tool,false);
        String output="",entities = "";
        for (int j=0;j<ekdm.getUnsortedURIs().size();j++) {
              //  output+=ekdm.getUnsortedURIs().get(j);
              if(!entities.contains(ekdm.getUnsortedURIs().get(j))){
              entities += ekdm.getUnsortedURIs().get(j);//
                 if(j+1!=ekdm.getUnsortedURIs().size()){
                     entities+= "%20";
                   //  output+=" ";
                 }
        }}
       

        if (entities.length() != 0) {
           
            LODsyndesisRestClient chanel = new LODsyndesisRestClient();
            output+=chanel.datasetDiscoveryEntities(entities, subset, topK, mType);
        }

        return output;
    }

    
}
