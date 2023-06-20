/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.forth.ics.isl.demoExternal.main;

import gr.forth.ics.isl.demoExternal.LODsyndesis.WAT_REST;
import gr.forth.ics.isl.evaluation.Text;
import static gr.forth.ics.isl.lodsyndesisie.restApi.Functionality.ekdm;
import gr.forth.ics.isl.utilities.StringUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.codehaus.jettison.json.JSONException;

/**
 *
 * @author mountant
 */
public class EntityRecognitionProcess {
       public String[][] runWithTool(String input, String toolType,boolean img) throws IOException, JSONException {
       // String input = t.getText();
        String[][] results2 = null;

        if (toolType.equals("WAT")) {
            WAT_REST wr = new WAT_REST();
            HashMap<String, String> entity_URI = wr.getWAT(input.replaceAll("\n"," "));
            if (entity_URI != null && !entity_URI.isEmpty()) {
                results2 = ekdm.getResults(entity_URI, img);
            }

        } else if (toolType.equals("DBWAT") || toolType.equals("DBS_WAT")) {
            WAT_REST wr = new WAT_REST();
            HashMap<String, String> entity_URI = ekdm.getEntities(input, "DBpedia Spotlight");
            HashMap<String, String> watURIs = wr.getWAT(input.replaceAll("\n"," "));
            for (String ent : watURIs.keySet()) {
                if (entity_URI.containsKey(ent) && !entity_URI.get(ent).equals(watURIs.get(ent))) {
                    String watURI = watURIs.get(ent);
                    String nlpURI = entity_URI.get(ent);
                    String finalURI = this.getLevenhsteinURI(ent, watURI, nlpURI);
                    entity_URI.put(ent, finalURI);
                } else {
                    entity_URI.put(ent, watURIs.get(ent));
                }
            }
            if (entity_URI != null && !entity_URI.isEmpty()) {
                results2 = ekdm.getResults(entity_URI, img);
            }

        } else if (toolType.equals("SCNLPWAT") || toolType.equals("SCNLP_WAT")) {
            WAT_REST wr = new WAT_REST();
            HashMap<String, String> entity_URI = new HashMap<>();
            entity_URI.putAll(ekdm.getEntities(input, "Stanford CoreNLP"));
            HashMap<String, String> watURIs = wr.getWAT(input.replaceAll("\n"," "));
            
            for (String ent : watURIs.keySet()) {
                if (entity_URI.containsKey(ent) && !entity_URI.get(ent).equals(watURIs.get(ent))) {
                    String watURI = watURIs.get(ent);
                    String nlpURI = entity_URI.get(ent);
                    String finalURI = this.getLevenhsteinURI(ent, nlpURI,watURI);
                    entity_URI.put(ent, finalURI);
                } else {
                    entity_URI.put(ent, watURIs.get(ent));
                }
            }
            //entity_URI.putAll(wr.getWAT(input));
            if (entity_URI != null && !entity_URI.isEmpty()) {
                results2 = ekdm.getResults(entity_URI, img);
            }

        } else if (toolType.equals("ALL") || toolType.equals("All")) {
            WAT_REST wr = new WAT_REST();
            HashMap<String, String> entity_URI = new HashMap<>();
            HashMap<String, ArrayList<String>> entities = new HashMap<>();

            HashMap<String, String> DBpediaURIs = ekdm.getEntities(input, "DBpedia Spotlight");
            HashMap<String, String> nlpURIs = ekdm.getEntities(input, "Stanford CoreNLP");
            HashMap<String, String> watURIs = wr.getWAT(input.replaceAll("\n"," "));
            
            
            for (String ent : nlpURIs.keySet()) {
                ArrayList<String> list = new ArrayList<String>();
                list.add(nlpURIs.get(ent));
                entities.put(ent, list);
            }
            for (String ent : DBpediaURIs.keySet()) {
                if (entities.containsKey(ent)) {
                    entities.get(ent).add(DBpediaURIs.get(ent));
                } else {
                    ArrayList<String> list = new ArrayList<String>();
                    list.add(DBpediaURIs.get(ent));
                    entities.put(ent, list);
                }
            }

            for (String ent : watURIs.keySet()) {
                if (entities.containsKey(ent)) {
                    entities.get(ent).add(watURIs.get(ent));
                } else {
                    ArrayList<String> list = new ArrayList<String>();
                    list.add(watURIs.get(ent));
                    entities.put(ent, list);
                }
            }

            for (String ent : entities.keySet()) {
                System.out.println(ent + "\t" + entities.get(ent));
                if (entities.get(ent).size() == 1) {
                    entity_URI.put(ent, entities.get(ent).get(0));
                }
                if (entities.get(ent).size() == 2 && entities.get(ent).get(0).equals(entities.get(ent).get(1))) {
                    entity_URI.put(ent, entities.get(ent).get(0));
                } else if (entities.get(ent).size() == 2) {
                    String firstURI = entities.get(ent).get(0);
                    String secondURI = entities.get(ent).get(1);
                    String finalURI = this.getLevenhsteinURI(ent, firstURI, secondURI);
                    entity_URI.put(ent, finalURI);
                } else if (entities.get(ent).size() == 3) {
                    if (entities.get(ent).get(0).equals(entities.get(ent).get(1)) || entities.get(ent).get(0).equals(entities.get(ent).get(2))) {
                        entity_URI.put(ent, entities.get(ent).get(0));
                    } else if (entities.get(ent).get(1).equals(entities.get(ent).get(2))) {
                        entity_URI.put(ent, entities.get(ent).get(1));
                    } else {
                        String firstURI = entities.get(ent).get(0);
                        String secondURI = entities.get(ent).get(1);
                        String tempURI = this.getLevenhsteinURI(ent, firstURI, secondURI);
                        String thirdURI = entities.get(ent).get(2);
                        String finalURI = this.getLevenhsteinURI(ent, tempURI, thirdURI);
                        entity_URI.put(ent, finalURI);
                    }
                }

            }

            if (!entity_URI.isEmpty()) {
                results2 = ekdm.getResults(entity_URI, img);
            }

        } else if (toolType.equals("DBSCNLP") || toolType.equals("DBS_SCNLP")) {
            // WAT_REST wr=new WAT_REST();
            HashMap<String, String> entity_URI = new HashMap<>();
            entity_URI.putAll(ekdm.getEntities(input, "Stanford CoreNLP"));
            HashMap<String, String> dbURIs = ekdm.getEntities(input, "DBpedia Spotlight");

            
            for (String ent : dbURIs.keySet()) {
                if (entity_URI.containsKey(ent) && !entity_URI.get(ent).equals(dbURIs.get(ent))) {
                    String dbURI = dbURIs.get(ent);
                    String nlpURI = entity_URI.get(ent);
                    String finalURI = this.getLevenhsteinURI(ent,nlpURI, dbURI);
                    entity_URI.put(ent, finalURI);
                } else {
                    entity_URI.put(ent, dbURIs.get(ent));
                }
            }
            if (!entity_URI.isEmpty()) {
                results2 = ekdm.getResults(entity_URI, img);
            }

        } else if (toolType.equals("SCNLP")) {
            HashMap<String, String> entity_URI = ekdm.getEntities(input, "Stanford CoreNLP");
            if (entity_URI != null && !entity_URI.isEmpty()) {
                results2 = ekdm.getResults(entity_URI,img);
            }
            //results2 = ekdm.getResults(entity_URI,false);
        } else if (toolType.equals("DBS")) {
            HashMap<String, String> entity_URI = ekdm.getEntities(input, "DBpedia Spotlight");
            if (entity_URI != null && !entity_URI.isEmpty()) {
                results2 = ekdm.getResults(entity_URI,img);
            }
            //results2 = ekdm.getResults(entity_URI,false);
        } 
        else {
            HashMap<String, String> entity_URI = ekdm.getEntities(input, toolType);
            if (entity_URI != null && !entity_URI.isEmpty()) {
                results2 = ekdm.getResults(entity_URI, true);
            }
        }
        return results2;
    }
       
       
    
    public static String getSuffixOfURI(String uri) {
        String[] tmp = uri.split("\\/|#");
        String suffix = tmp[tmp.length - 1];
        return suffix;
    }

    public String getLevenhsteinURI(String entity, String URI1, String URI2) {
        int distance1 = StringUtils.LevenshteinDistance(entity, getSuffixOfURI(URI1).split("\\(")[0].replace("_",""));
        int distance2 = StringUtils.LevenshteinDistance(entity, getSuffixOfURI(URI2).split("\\(")[0].replace("_",""));
        //System.out.println("Levenhstein!!!: "+entity + "\t" + URI1 + "\t" + distance1 + "\t" + URI2 + "\t" + distance2);
        if (distance1 < distance2) {
            return URI1;
        } else {
            return URI2;
        }

    }   
       
}
