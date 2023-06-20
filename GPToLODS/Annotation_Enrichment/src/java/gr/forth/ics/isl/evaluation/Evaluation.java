/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.forth.ics.isl.evaluation;

import gr.forth.ics.isl.demoExternal.LODsyndesis.WAT_REST;
import gr.forth.ics.isl.demoExternal.main.ExternalKnowledgeDemoMain;
import gr.forth.ics.isl.lodsyndesisie.restApi.Functionality;
import gr.forth.ics.isl.utilities.StringUtils;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.codehaus.jettison.json.JSONException;

/**
 *
 * @author mountant
 */
public class Evaluation {

    public ExternalKnowledgeDemoMain ekdm = new ExternalKnowledgeDemoMain();

    HashMap<String, String> exceptions = new HashMap<String, String>();

    ArrayList<Text> texts = new ArrayList<Text>();
    ArrayList<String> entities = new ArrayList<String>();
    String[] choices = {"WAT","DBpedia Spotlight", "DBWAT","Stanford CoreNLP", "SCNLPWAT", "DBSCNLP", "ALL"};

    public static void main(String[] args) throws IOException, JSONException {
        Evaluation ev = new Evaluation();
        //ev.setExceptions();
        String file = "C:\\Users\\mountant\\Desktop\\msnbc2.txt";
         ev.initTexts(file);
        // ev.printTextInfo();
        //ev.iterateTextsEfficiency("er");
       //  ev.printStatistics();
         ev.iterateTexts();
//        String[] texts={"APW19980603_0791"};// //{"Ent16453733","Tec16454435","TvN16442342","TvN16442287","Wor16447201"};
//       for(String dts:texts){
//            String file2="C:\\Users\\mountant\\Desktop\\RawTexts\\AQUA\\"+dts+".htm";
//
//            String text=ev.fileToString(file2);
//            ev.identifyEntitiesFromTexts(text,dts);
//        }
//       
//       for(Text text:ev.texts){
//           ev.identifyEntitiesFromTexts(text.text,text.getText());
//       }
    }
    
    public String fileToString(String file) throws FileNotFoundException, IOException{
        int i = 0;   
        BufferedReader br = new BufferedReader(new FileReader(file));
        int lastID = 0;
        String s="", result="";
        String mode = "none";
        while ((s = br.readLine()) != null) {
            if(!s.trim().equals(""))
            result+="\n"+s.replaceAll("[^a-zA-Z]", " ");//+"\n";//+"\n";
            i++;
        }
    
        br.close();
        return result;
    }
    
    
    
    
    public void identifyEntitiesFromTexts(String t,String dataset) throws IOException, JSONException{
        HashMap<String,Integer> map=new HashMap<>(); 
        Functionality func = new Functionality();
         String[] tools={"DBpedia Spotlight","Stanford CoreNLP", "WAT",    "DBWAT", "SCNLPWAT", "DBSCNLP", "ALL"};
         for(String x:tools){   
            String[][] entities = func.getEntities(t, x, false,false);
             System.out.println(x+" "+(entities.length-1));
            map.put(x,entities.length-1);
         }
          System.out.println(dataset);
         for(String key:map.keySet()){
            
             System.out.println(key+"\t"+map.get(key));
         }
    }

    
    
    
    
    public void setExceptions() {
        //  exceptions.put("http://dbpedia.org/resource/Gauharara_Begum","http://dbpedia.org/resource/Gauhar_Ara_Begum");
        //  exceptions.put("http://dbpedia.org/resource/Gauhara_Begum","http://dbpedia.org/resource/Gauhar_Ara_Begum");
        exceptions.put("http://dbpedia.org/resource/MIT", "http://dbpedia.org/resource/Massachusetts_Institute_of_Technology");
        //  exceptions.put("http://dbpedia.org/resource/Stagira","http://dbpedia.org/resource/Stagira_(ancient_city)");
        exceptions.put("http://dbpedia.org/resource/ATHENS", "http://dbpedia.org/resource/Athens");
        exceptions.put("http://dbpedia.org/resource/Heracles", "http://dbpedia.org/resource/Hercules");

    }

    public void printTextInfo() {
        for (Text t : texts) {
            System.out.print("Text ID:" + t.textID);
            System.out.print("&" + t.wordsNum());
            System.out.println();
            System.out.println("&" + t.getEntities().size());
            for (String x : t.getEntities()) {
                //  System.out.println(x);
            }
        }
    }

    public void printStatistics() {
        ekdm.entityStatistics(entities);
    }

    public void iterateTexts() throws IOException, JSONException {

        HashMap<String, Double> precisionAll = new HashMap<String, Double>();
        HashMap<String, Double> recallAll = new HashMap<String, Double>();
        HashMap<String, Double> fScoreAll = new HashMap<String, Double>();
        for (String k : choices) {
            precisionAll.put(k, 0.0);
            recallAll.put(k, 0.0);
            fScoreAll.put(k, 0.0);
        }

        for (Text t : texts) {
            // if (!"###Text10".equals(t.textID)) {
            //   continue;
            //}
            for (String tool : choices) {
                HashSet<String> entities = this.runWithTool(t, tool, true);
                int numOfEntities = entities.size();
                entities.retainAll(t.entities);
                int intersection = entities.size();
                double precision = (double) intersection / (double) numOfEntities;
                double recall = (double) intersection / (double) t.textEntities;
                double fscore = (double) 2 * recall * precision / ((double) precision + recall);
                
                precisionAll.put(tool, precisionAll.get(tool) + precision);
                recallAll.put(tool, recallAll.get(tool) + recall);
                fScoreAll.put(tool, fScoreAll.get(tool) + fscore);

                t.interEntities.put(tool, intersection);
                t.recEntities.put(tool, numOfEntities);
                t.precision.put(tool, precision);
                t.recall.put(tool, recall);
                t.fScore.put(tool, fscore);
            }
        }
        
        
        
         for (String tool : choices) {
             System.out.println("\n"+tool);
              for (Text t : texts) {
                  System.out.print("\t"+t.precision.get(tool));
              }
             
         }
         
          for (String tool : choices) {
             System.out.println("\n"+tool);
              for (Text t : texts) {
                  System.out.print("\t"+t.recall.get(tool));
              }
             
         }
         for (String tool : choices) {
             System.out.println("\n"+tool);
              for (Text t : texts) {
                  System.out.print("\t"+t.fScore.get(tool));
              }
             
         } 
          
        
        for (Text t : texts) {
            System.out.println(t);
            System.out.println("=====Precision+Recall+Fscore=====");
            for (String tool : choices) {
                System.out.println(tool + "\t" + t.precision.get(tool) + "\t" + t.recall.get(tool) + "\t" + t.fScore.get(tool));
                //System.out.println("Recognized Entities:"+t.recEntities.get(tool));
                //  System.out.println("Intersection Entities:"+t.interEntities.get(tool));
                //   System.out.println("Precision:"+t.precision.get(tool));
                //    System.out.println("Recall:"+t.recall.get(tool));

            }
            System.out.println("====");
        }

        System.out.println("=====Average Precision+Recall+Fscore=====");
        for (String tool : choices) {
            System.out.println(tool + "\t" + precisionAll.get(tool) / (double) texts.size() + "\t" + recallAll.get(tool) / (double) texts.size() + "\t" + fScoreAll.get(tool) / (double) texts.size());
            //System.out.println("Recognized Entities:"+t.recEntities.get(tool));
            //  System.out.println("Intersection Entities:"+t.interEntities.get(tool));
            //   System.out.println("Precision:"+t.precision.get(tool));
            //    System.out.println("Recall:"+t.recall.get(tool));

        }
        System.out.println("====");

    }

    public void initTexts(String file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));

        String s = "";
        int i = 0,cnt=0;
        int lastID = 0;
        Text t = new Text();
        String mode = "none";
        while ((s = br.readLine()) != null) {
            if (s.startsWith("###")) {
                if (!"none".equals(mode)) {
                    t.textEntities = t.entities.size();
                    texts.add(t);
                }
                mode = "none";
                if(cnt==5){
                    break;
                }
                t = new Text();
                t.textID = s;
               // cnt++;
            } else if (s.startsWith("#Plain Text")) {
                mode = "plain";
            } else if (s.startsWith("#Entities")) {
                mode = "entities";
            } else if (mode.equals("entities")) {
                t.entities.add(s.split("\t")[1]);
                entities.add(s.split("\t")[1]);
            } else if (mode.equals("plain")) {
                t.text += s + " \n";
            }

        }
        br.close();
    }

    public HashMap<String, String> replaceExceptions(HashMap<String, String> map) {
        HashMap<String, String> repExc = new HashMap<String, String>();
        for (String k : map.keySet()) {
            if (this.exceptions.containsKey(map.get(k))) {
                repExc.put(k, this.exceptions.get(map.get(k)));
            } else {
                repExc.put(k, map.get(k));
            }
        }
        return repExc;

    }

    
    

    
    
    public HashSet<String> runWithTool(Text t, String toolType, boolean exceptions) throws IOException, JSONException {
        String input = t.getText();
        String[][] results2 = null;

        if (toolType.equals("WAT")) {
            WAT_REST wr = new WAT_REST();
            HashMap<String, String> entity_URI = wr.getWAT(input.replace("\n", ""));
            if (exceptions == true) {
                entity_URI = replaceExceptions(entity_URI);
            }
            if (entity_URI != null && !entity_URI.isEmpty()) {
                results2 = ekdm.getResults(entity_URI, true);
            }

        } else if (toolType.equals("DBWAT")) {
            WAT_REST wr = new WAT_REST();
            HashMap<String, String> entity_URI = ekdm.getEntities(input, "DBpedia Spotlight");
            HashMap<String, String> watURIs = wr.getWAT(input.replace("\n", ""));
            if (exceptions == true) {
                entity_URI = replaceExceptions(entity_URI);
                watURIs = replaceExceptions(watURIs);
            }
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
                results2 = ekdm.getResults(entity_URI, true);
            }

        } else if (toolType.equals("SCNLPWAT")) {
            WAT_REST wr = new WAT_REST();
            HashMap<String, String> entity_URI = new HashMap<>();
            entity_URI.putAll(ekdm.getEntities(input, "Stanford CoreNLP"));
            HashMap<String, String> watURIs = wr.getWAT(input.replace("\n", ""));

            if (exceptions == true) {
                entity_URI = replaceExceptions(entity_URI);
                watURIs = replaceExceptions(watURIs);
            }

            for (String ent : watURIs.keySet()) {
                if (entity_URI.containsKey(ent) && !entity_URI.get(ent).equals(watURIs.get(ent))) {
                    String watURI = watURIs.get(ent);
                    String nlpURI = entity_URI.get(ent);
                    String finalURI = this.getLevenhsteinURI(ent, nlpURI, watURI);
                    entity_URI.put(ent, finalURI);
                } else {
                    entity_URI.put(ent, watURIs.get(ent));
                }
            }
            //entity_URI.putAll(wr.getWAT(input));
            if (entity_URI != null && !entity_URI.isEmpty()) {
                results2 = ekdm.getResults(entity_URI, true);
            }

        } else if (toolType.equals("ALL")) {
            WAT_REST wr = new WAT_REST();
            HashMap<String, String> entity_URI = new HashMap<>();
            HashMap<String, ArrayList<String>> entities = new HashMap<>();

            HashMap<String, String> DBpediaURIs = ekdm.getEntities(input, "DBpedia Spotlight");
            HashMap<String, String> nlpURIs = ekdm.getEntities(input, "Stanford CoreNLP");
            HashMap<String, String> watURIs = wr.getWAT(input.replace("\n", ""));
            if (exceptions == true) {
                DBpediaURIs = replaceExceptions(DBpediaURIs);
                nlpURIs = replaceExceptions(nlpURIs);
                watURIs = replaceExceptions(watURIs);
            }

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
                results2 = ekdm.getResults(entity_URI, true);
            }

        } else if (toolType.equals("DBSCNLP")) {
            // WAT_REST wr=new WAT_REST();
            HashMap<String, String> entity_URI = new HashMap<>();
            entity_URI.putAll(ekdm.getEntities(input, "Stanford CoreNLP"));
            HashMap<String, String> dbURIs = ekdm.getEntities(input, "DBpedia Spotlight");
            if (exceptions == true) {
                entity_URI = replaceExceptions(entity_URI);
                dbURIs = replaceExceptions(dbURIs);
            }

            for (String ent : dbURIs.keySet()) {
                if (entity_URI.containsKey(ent) && !entity_URI.get(ent).equals(dbURIs.get(ent))) {
                    String dbURI = dbURIs.get(ent);
                    String nlpURI = entity_URI.get(ent);
                    String finalURI = this.getLevenhsteinURI(ent, nlpURI, dbURI);
                    entity_URI.put(ent, finalURI);
                } else {
                    entity_URI.put(ent, dbURIs.get(ent));
                }
            }
            if (!entity_URI.isEmpty()) {
                results2 = ekdm.getResults(entity_URI, true);
            }

        } else {
            HashMap<String, String> entity_URI = ekdm.getEntities(input, toolType);
            if (exceptions == true) {
                entity_URI = replaceExceptions(entity_URI);
            }
            if (entity_URI != null && !entity_URI.isEmpty()) {
                results2 = ekdm.getResults(entity_URI, true);
            }
        }
        return ekdm.getEntityURIsForEvaluation();

    }

    public static String getSuffixOfURI(String uri) {
        String[] tmp = uri.split("\\/|#");
        String suffix = tmp[tmp.length - 1];
        return suffix;
    }

    public String getLevenhsteinURI(String entity, String URI1, String URI2) {
        int distance1 = StringUtils.LevenshteinDistance(entity, getSuffixOfURI(URI1).split("\\(")[0].replace("_", ""));
        int distance2 = StringUtils.LevenshteinDistance(entity, getSuffixOfURI(URI2).split("\\(")[0].replace("_", ""));
        System.out.println("Levenhstein!!!: " + entity + "\t" + URI1 + "\t" + distance1 + "\t" + URI2 + "\t" + distance2);
        if (distance1 < distance2) {
            return URI1;
        } else {
            return URI2;
        }

    }

    public void iterateTextsEfficiency(String type) throws IOException, JSONException {

        Functionality func = new Functionality();
        //String[][] entities = func.getEntities("Aristotle for initialization", "WAT", false, false);
        //entities = func.getEntities("Aristotle for initialization", "Stanford CoreNLP",  false, false);
        //entities = func.getEntities("Aristotle for initialization",  "DBpedia Spotlight",  false, false);

        HashMap<String, Float> efficiencyAll = new HashMap<String, Float>();
        for (String k : choices) {
            efficiencyAll.put(k, 0.0F);
        }
        for (Text t : texts) {
//             if (!"###Text1".equals(t.textID)) {
//               continue;
//            }
            for (String tool : choices) {
                long start = System.currentTimeMillis();
                // String[][] entities = func.getEntities(t.text, tool, true,true);
                String x=func.getTriplesOfEntities(t.text, tool);
                //String x=func.textEntitiesDD(t.text, tool,5, 10, "coverage");
               // String x=func.FindRelatedFacts(t.text, tool, "");
                
                long finish = System.currentTimeMillis();
                float sec = (finish - start) / 1000F;
                System.out.println(sec + " seconds");
                System.out.println(tool + "\t" + sec);
                t.efficiency.put(tool, sec);
                if ("###Text0".equals(t.textID)) {
                    continue;
                }
                efficiencyAll.put(tool, efficiencyAll.get(tool) + sec);
            }
        }

        for (Text t : texts) {
            for (String tool : choices) {
                System.out.println(tool + "\t" + t.efficiency.get(tool));
            }
        }

        System.out.println("=====Average Time");
        for (String tool : choices) {
            System.out.println(tool + "\t" + efficiencyAll.get(tool) / (double) (texts.size()-1));
        }
        System.out.println("====");

    }

}
