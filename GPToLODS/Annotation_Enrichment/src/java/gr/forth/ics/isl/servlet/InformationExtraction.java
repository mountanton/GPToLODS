
/*  This code belongs to the Semantic Access and Retrieval (SAR) group of the
 *  Information Systems Laboratory (ISL) of the
 *  Institute of Computer Science (ICS) of the
 *  Foundation for Research and Technology - Hellas (FORTH)
 *  Nobody is allowed to use, copy, distribute, or modify this work.
 *  It is published for reasons of research results reproducibility.
 *  (c) 2020 Semantic Access and Retrieval group, All rights reserved
 */
package gr.forth.ics.isl.servlet;

import gr.forth.ics.isl.demoExternal.ChatGPT.ChatGPT;
import gr.forth.ics.isl.demoExternal.main.ExternalKnowledgeDemoMain;
import gr.forth.ics.isl.demoExternal.LODsyndesis.LODsyndesisRestClient;
import gr.forth.ics.isl.demoExternal.main.EntityRecognitionProcess;
import gr.forth.ics.isl.lodsyndesisie.restApi.Functionality;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import org.openrdf.repository.RepositoryConnection;

/**
 *
 * @author micha_000
 */
public class InformationExtraction extends HttpServlet {

    RepositoryConnection connec = null;
    Connection conn, conn2;
    public int limitNumber = 0;
    public static HashMap<String, String> images = new HashMap<>();
    String columnNames[];
    public static ExternalKnowledgeDemoMain ekdm = new ExternalKnowledgeDemoMain();

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
            ////System.out.println(inputURIs);
            String ret = inputURIs.get(0).replace("http://schema.org/", "");
            return ret;
        }
        //return inputURIs;
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

    public String text2RDFa(String input) throws MalformedURLException, IOException {
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

    public String returnFirstURI(String input) throws MalformedURLException, IOException {

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

    public String getUpperCase(String entity) {
        String entity2 = Character.toUpperCase(entity.charAt(0)) + entity.substring(1);
        String newStr = "";
        if (entity2.contains(" ")) {
            String[] split = entity2.split(" ");

            for (String k : split) {
                newStr += Character.toUpperCase(k.charAt(0)) + k.substring(1) + " ";
            }
            newStr = newStr.substring(0, newStr.length() - 1);

        } else {
            return entity2;
        }
        return newStr;
    }

    public String outputFactsRDF(String URI, TreeMap<String, String> input) {
        String output = "";
        for (String x : input.keySet()) {
            String obj = x;
            String sub = "<" + URI + ">";
            for (String k : input.get(x).split("\n")) {
                String[] split = k.split("\t");
                if (split[0].contains("*>")) {
                    String prop = split[0].replace("*>", ">");
                    output += obj + " " + prop + " " + sub + " " + split[1];
                } else {
                    output += sub + " " + split[0] + " " + obj + " " + split[1];
                }
                output += "\n";

            }
        }
        // //System.out.println(output);
        return output;

    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        /* TODO output your page here. You may use following sample code. */
        if (request.getParameter("relFacts") != null) {
            HttpSession session = request.getSession();

            HashMap<String, String> results = (HashMap<String, String>) session.getAttribute("relatedURIs");
            ArrayList<String> unURIs = (ArrayList<String>) session.getAttribute("unsortedURIs");
            String words = "";
            String URI = this.returnFirstURI(session.getAttribute("rdfaText").toString());

            System.out.println(session.getAttribute("rdfaText").toString());
            if (request.getParameter("entity") != null) {
                URI = request.getParameter("entity").toString();
                //System.out.println(URI);
            }
            // //System.out.println(URI);
            for (String x : unURIs) {
                if (!x.equals(URI)) {
                    words += x + " ";
                }
            }

            LODsyndesisRestClient chanel = new LODsyndesisRestClient();
            String[][] rel = new String[1][4];
            rel[0][0] = "Key Entity";
            rel[0][1] = "Relationship";
            rel[0][2] = "Entity 2";
            rel[0][3] = "Provenance";
            if (words.length() != 0) {
                words = words.substring(0, words.length() - 1);
                String[] split = words.split(" ");
                double thres = 1.0 / (double) split.length;
                //ntln(words);

                TreeMap<String, String> res = chanel.relatedFacts(URI, words, thres);
                // //System.out.println(res);

                rel = new String[res.size() + 1][4];
                rel[0][0] = "Key Entity";
                rel[0][1] = "Relationship";
                rel[0][2] = "Entity 2";
                rel[0][3] = "Provenance";
                int i = 1;
                for (String x : res.keySet()) {
                    String[] sub = results.get(URI).split("\t");
                    String image = ekdm.getImage(URI);
                    if (!image.equals("")) {
                        rel[i][0] = "<img src=" + image + " width='75' height='75'><br>" + "<a href=\"" + sub[1] + "\">" + this.getUpperCase(sub[0]) + "</a>";
                    } else {
                        rel[i][0] = "<a href=\"" + sub[1] + "\">" + this.getUpperCase(sub[0]) + "</a>";
                    }
                    int j = 0;
                    String prov = "";
                    for (String k : res.get(x).split("\n")) {
                        if (j == 0 || rel[i][1].contains("wikidata")) {
                            rel[i][1] = k.split("\t")[0].replace("<", "").replace(">", "");
                        }
                        if (!prov.contains(k.split("\t")[1].replace("<", "").replace(">", "<br>"))) {
                            prov += k.split("\t")[1].replace("<", "").replace(">", "<br>");
                        }
                        j++;
                    }
                    if (!results.containsKey(x.replace("<", "").replace(">", ""))) {
                        rel[i][2] = "<a href=\"" + x.replace("<", "").replace(">", "") + "\">" + x.replace("<", "").replace(">", "").replace("http://dbpedia.org/resource/", "") + "</a>";
                    } else {
                        String obj = results.get(x.replace("<", "").replace(">", ""));

                        String[] split2 = obj.split("\t");

                        rel[i][2] = "<a href=\"" + split2[1] + "\">" + this.getUpperCase(split2[0]) + "</a>";
                    }
                    String img2 = ekdm.getImage(x.replace("<", "").replace(">", ""));
                    if (!img2.equals("")) {
                        rel[i][2] = "<img src=" + img2 + " width='75' height='75'><br>" + rel[i][2];
                    }

                    rel[i][3] = prov;
                    // //System.out.println(x);
                    i++;
                }
                session.setAttribute("outputRDFfacts", this.outputFactsRDF(URI, res));
            }
            request.setAttribute("page2", "table.jsp");
            request.setAttribute("page", "lodsyndesisGPT.jsp");
            //  request.setAttribute("URIs", request.getParameter("URIs"));
            request.setAttribute("results", rel);
            request.setAttribute("keyURI", URI.replace("http://dbpedia.org/resource/", ""));
            //System.out.println(URI);
            if (!results.isEmpty()) {
                request.setAttribute("keyEntity", this.getUpperCase(results.get(URI).split("\t")[0]));
            }
            request.setAttribute("text", session.getAttribute("rdfaText"));
            request.setAttribute("question", session.getAttribute("question"));
            request.setAttribute("msg", rel.length - 1 + " Related Facts found in LODsyndesis");
            request.setAttribute("intro", session.getAttribute("anmsg"));
            request.setAttribute("outputRDFfactsHTML", "true");
            String txt = "";
            for (String k : results.keySet()) {
                if (!k.equals(URI)) {
                    txt += this.getUpperCase(results.get(k).split("\t")[0]) + "$" + k + "\t";
                } else {
                    txt = this.getUpperCase(results.get(k).split("\t")[0]) + "$" + k + "\t" + txt;
                }
            }
            request.setAttribute("entities", txt);
            //  session.setAttribute("relatedURIs", ekdm.getURIs());
            getServletConfig().getServletContext().getRequestDispatcher(
                    "/index.jsp").forward(request, response);

        } else if (request.getParameter("RDFFacts") != null) {
            HttpSession session = request.getSession();

            String rdf = session.getAttribute("outputRDFfacts").toString();
            response.setContentType("text/nq");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"factsRDF.nq\"");
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(rdf.getBytes());
            outputStream.flush();
            outputStream.close();
        } else if (request.getParameter("RDFa") != null) {
            HttpSession session = request.getSession();

            String rdfa = session.getAttribute("rdfaText").toString();
            //  //System.out.println("Input:" + rdfa);
            response.setContentType("text/html");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"output.html\"");
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(text2RDFa(rdfa).getBytes());
            outputStream.flush();
            outputStream.close();
        } else if (request.getParameter("export") != null) {
            try {
                if (request.getParameter("export").contains("nq")) {
                    response.setContentType("text/nq");
                } else if (request.getParameter("export").contains("json")) {
                    response.setContentType("application/json");
                } else {
                    response.setContentType("text/csv");
                }
                response.setCharacterEncoding("UTF-8");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + request.getParameter("export") + "\"");
                OutputStream outputStream = response.getOutputStream();
                ArrayList<String> results = new ArrayList<String>();
                LODsyndesisRestClient rest = new LODsyndesisRestClient();
                String input = request.getParameter("URIs");
                if (request.getParameter("single") != null) {
                    if (request.getParameter("export").equals("provenance.json")) {
                        ArrayList<ArrayList<String>> res = rest.provenanceAsJSON(input);
                        //System.out.println(res.get(0));
                        if (res != null && res.size() > 0) {
                            if (res.get(0) != null && res.get(0).size() > 0) {
                                String str = res.get(0).get(0).replace("}", "}\n");
                                results.add(str);
                            }
                        }
                        //results.add()
                    } else if (request.getParameter("export").equals("objectCoreference.json")) {
                        ArrayList<ArrayList<String>> res = rest.objectCoreferenceAsJSON(input);
                        if (res != null && res.size() > 0) {
                            if (res.get(0) != null && res.get(0).size() > 0) {
                                String str = res.get(0).get(0).replace("}", "}\n");
                                results.add(str);
                            }
                        }
                        //results.add()
                    } else if (request.getParameter("export").equals("allFacts.json")) {
                        ArrayList<ArrayList<String>> res = rest.allFactsAsJSON(input);
                        if (res != null && res.size() > 0) {
                            for (String k : res.get(0)) {
                                String str = k.replace("}", "}\n");
                                results.add(str);
                            }
                        }
                        //results.add()
                    }
                } else if (request.getParameter("export").equals("textEntitiesDatasetDiscovery.csv")) {
                    String entities = "", lastK = "";
                    for (String k : input.split("\n")) {
                        if (!entities.contains(k.replace("%3A", ":").replace("%2F", "/").replace("%3F", "/"))) {
                            entities += k.replace("%3A", ":").replace("%2F", "/").replace("%3F", "/") + "%20";
                            lastK = k.replace("%3A", ":").replace("%2F", "/").replace("%3F", "/");
                        }
                    }
                    entities = entities.replace(lastK + "%20", lastK);
                    //System.out.println(entities);
                    if (entities.length() != 0) {

                        //LODsyndesisRestClient chanel = new LODsyndesisRestClient();
                        results.add(rest.datasetDiscoveryEntities(entities, 3, 10, "coverage"));
                    }
                } else {
                    input = request.getParameter("text");

                    for (String k : input.split("\n")) {
                        // //System.out.println(k.trim());
                        if (request.getParameter("export").equals("objectCoreferenceAllEntities.nt")) {
                            ArrayList<String> resTemp = rest.objectCoreference(k.trim(), false);
                            for (String x : resTemp) {
                                String str = "<" + k + "> <http://www.w3.org/2002/07/owl#sameAs> " + x + " .\n";
                                results.add(str);
                            }
                        } else if (request.getParameter("export").equals("allFactsAllEntities.nq")) {
                            ArrayList<String> resTemp = rest.allFacts(k.trim(), false);
                            for (String x : resTemp) {
                                //String str = "<" + k + "> <http://www.w3.org/2002/07/owl#sameAs> " + x + " .\n";
                                results.add(x + "\n");
                            }
                        } else if (request.getParameter("export").equals("allDatasetsAllEntities.nt")) {
                            ArrayList<String> resTemp = rest.allDatasets(k.trim(), false);
                            for (String x : resTemp) {
                                if (!x.contains("dbpedia.org/resource")) {
                                    continue;
                                }
                                //String str = "<" + k + "> <http://www.w3.org/2002/07/owl#sameAs> " + x + " .\n";
                                results.add(x + " .\n");
                            }
                        } else if (request.getParameter("export").equals("allDatasetsAllEntities.json")) {
                            ArrayList<ArrayList<String>> res = rest.provenanceAsJSON(k.trim());
                            if (res != null && res.size() > 0) {
                                if (res.get(0) != null && res.get(0).size() > 0) {
                                    String str = res.get(0).get(0).replace("}", "}\n").replace("[", "").replace("]", "");
                                    results.add(str);
                                }
                            }
                        }
                    }
                    ////System.out.println(resTemp);

                }
                //   results.addAll(resTemp);
                String outputResult = "";
                if (request.getParameter("export").contains(".json") && request.getParameter("single") != null) {
                    outputResult = "[";
                }

                for (String str : results) {
                    outputResult += str;//<"+request.getParameter("URI")+">\t<"++">\t\""+results[i][1]+"\"\t"+results[i][2]+"\n";
                }
                if (request.getParameter("export").contains(".json") && request.getParameter("single") != null) {
                    outputResult += "]";
                }
                // //System.out.println(outputResult);

                outputStream.write(outputResult.getBytes());
                outputStream.flush();
                outputStream.close();

                return;
            } catch (Exception e) {
                //System.out.println(e.toString());
            }
        } else {
            PrintWriter out = response.getWriter();
            String responseOnlygpt="";
            try {
                String question = request.getParameter("text");
                String gptmodel = request.getParameter("gptModel");
                String input = " ";
                ChatGPT chatgpt = new ChatGPT();
                responseOnlygpt=chatgpt.getChatGPTResponse(question,gptmodel);
                input = "Prompt: " + question.replace("?", " ") + " ChatGPT Response: " +   responseOnlygpt+" ";
                
                input = input.replaceAll("\n", " ");
                //System.out.println(input);
                //input="Prompt: Which is the birth place of Aristotle ChatGPT Response: Aristotle was born in Stageira in Chalkidiki, Greece in 384 BC.";
                //.println(input);
                String toolType = request.getParameter("toolType");
                ////System.out.println(toolType);

//Theseus met Princess Ariadne, daughter of King Minos, who fell madly in love with him and decided to help Theseus. She gave him a thread and told him to unravel it as he would penetrate deeper and deeper into the Labyrinth, so that he knows the way out when he kills the monster.Theseus followed her suggestion and entered the labyrinth with the thread. Theseus managed to kill the Minotaur and save the Athenians, and with Ariadne thread he managed to retrace his way out.
                //(info);             
                EntityRecognitionProcess erp = new EntityRecognitionProcess();
                String[][] results2 = erp.runWithTool(input, toolType, true);
                //String[][] results2 = ekdm.getEntities(input, toolType);// //this.coverage(Integer.parseInt(limit), entityInfo, subset, dtsets, mt);//new String[Integer.parseInt(limit)][2];
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
                input = input.replaceAll(">", "");
                input = input.replaceAll("<", "");
                input = input.replace(",", "!comma");
                input = input.replace(".", "!dot");
                input = input.replace(")", "!par");
                for (String str : entities.keySet()) {
                  //  System.out.println(str);
                 //   System.out.println(input);
                    if(str.contains(",")){
                        str=str.replace(",","!comma");
                        input = input.replaceAll("(?i)" + str + " ", "<a href=\"Link:" + str + "\"><b>" + str + "</b></a> ");
                        input = input.replaceAll("(?i)" + str + "!comma", "<a href=\"Link:" + str + "\"><b>" + str + "</b></a>,");
                        input = input.replaceAll("(?i)" + str + "!dot", "<a href=\"Link:" + str + "\"><b>" + str + "</b></a>.");
                        input = input.replaceAll("(?i)" + str + "!par", "<a href=\"Link:" + str + "\"><b>" + str + "</b></a>)");
                       input = input.replace("Link:" + str, entities.get(str.replace("!comma",",")));
                        
                    }
                    else if (!str.contains(".")) {
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
                input = input.replace("Prompt:", "User Prompt: ");
                input = input.replace("ChatGPT Response:", "<br><br><b>ChatGPT Annotated Response:</b><br><br>");
                for (String str : entities.keySet()) {
                    if (str.contains(".")) {
                        input = input.replaceAll("(?i)" + str, "<a href=\"Link:" + str + "\"><b>" + str + "</b></a>");
                        input = input.replace("Link:" + str, entities.get(str));
                    }
                }

                input = this.input2popup(input, results2);


                request.setAttribute("page2", "table.jsp");
                request.setAttribute("page", "lodsyndesisGPT.jsp");
                //  request.setAttribute("URIs", request.getParameter("URIs"));
                request.setAttribute("results", results2);
                request.setAttribute("text", input);
                request.setAttribute("responseOnlyGPT", responseOnlygpt.trim().replaceAll("\n", " ").replaceAll("\"",""));
                if (toolType.equals("ALL")) {
                    toolType = "All 3 Tools";
                } else if (toolType.equals("DBWAT")) {
                    toolType = "DBpedia Spotlight, WAT";
                } else if (toolType.equals("SCNLPWAT")) {
                    toolType = "Stanford CoreNLP, WAT";
                } else if (toolType.equals("DBSCNLP")) {
                    toolType = "DBpedia Spotlight, Stanford CoreNLP";
                }
                String msg = "Annotated ChatGPT Response with links to Hundreds of Knowledge Graphs";
                String msg2 = "No Recognized Entities in the given Response";
                if (results2 != null) {
                    msg2 = "Extract more information for these " + (results2.length - 2) + " entities from LODsyndesis";
                }
                request.setAttribute("msg", msg2);
                request.setAttribute("question", question);
                request.setAttribute("intro", msg);
                HttpSession session = request.getSession(true);
                session.setAttribute("rdfaText", input);
                session.setAttribute("question", question);

                session.setAttribute("relatedURIs", ekdm.getURIs());
                session.setAttribute("unsortedURIs", ekdm.getUnsortedURIs());
                session.setAttribute("anmsg", msg);
                getServletConfig().getServletContext().getRequestDispatcher(
                        "/index.jsp").forward(request, response);
            } catch (Exception ex) {
                Logger.getLogger(InformationExtraction.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                out.close();
            }
        }
    }

    public String input2popup(String text, String results[][]) throws MalformedURLException, IOException {
        if(!text.contains("href")){
            return text;
        }
        int id = 0;
        //System.out.println(results[1][0]);
        String[] split = text.split("</a>");
        String output = "<div style=\"font-size:30px; border-width: 2px;\n"
                + "                border-color: #000;text-align: justify; padding:2px\"> ";
        for (String x : split) {
            //System.out.println(x);
            String[] split2 = x.split("<a");
            output += split2[0];
            if (split2.length > 1) {

                String[] split3 = split2[1].split("<b>");
                String URL = split3[0].split("URI=")[1].split("&")[0];
                String DBpediaURL = "http://" + URL.replace("$", "/");
                String provenance = "https://demos.isl.ics.forth.gr/lodsyndesis/RunQuery?URI=" + URL + "&queryType=DatDom";
                String uris = "https://demos.isl.ics.forth.gr/lodsyndesis/RunQuery?URI=" + URL + "&queryType=EquivalentURIs";
                String facts = "https://demos.isl.ics.forth.gr/lodsyndesis/RunQuery?URI=" + URL + "&queryType=triples";
                String topk = "https://demos.isl.ics.forth.gr/lodsyndesis/RunQueryCov?URI=" + DBpediaURL + "&mt=coverage&qt=3&lt=10";
                ExternalKnowledgeDemoMain ekdm = new ExternalKnowledgeDemoMain();
                String image = ekdm.getImage(DBpediaURL);
               // System.out.println(DBpediaURL);
                String name = split3[1].split("</b>")[0];
                SPARQL_Query_Statistics sqs = new SPARQL_Query_Statistics();
                String countURIs = "select count(distinct ?s1) where {<" + DBpediaURL + "> ?p ?o . ?s1 ?p ?o}";
                int allURIs = sqs.sendQuery(countURIs);
                String provenanceQuery = "select distinct ?o1 where {<" + DBpediaURL + ">  ?p ?o . ?o <http://www.ics.forth.gr/isl/directCount> ?o1}";
                 
                String result=sqs.sendCountQuery(provenanceQuery);
                String[] splitDirect=result.split("DC");
                int allDtsets=splitDirect[0].split(",").length;
                //int allDtsets = sqs.sendCountQuery(provenanceQuery,"Datasets");
                int triples=0;
                if(splitDirect.length>1){
                String[] splitDirectCountOfDatasets=splitDirect[1].split("\\$");
                
                for(String str:splitDirectCountOfDatasets){
                    triples+=Integer.parseInt(str.split("\t")[1]);
                }
                }
                Functionality f=new Functionality();
                String type=f.getType(DBpediaURL);
//System.out.println(name);
                output += "<div class=\"popup\" onclick=myFunction(" + id + ")>\n"
                        + "<mark><i>" + name + "</i></mark>\n"
                        + "  <span class=\"popuptext\" id=\"myPopup" + id + "\"><b>" + name + "</b><br> \n"
                        + "  <img src=\"" + image + "\" alt=\"" + name + "\" width=\"100px\" height=\"100px\">\n"
                        + "<br>\n"
                        + "<i>"+type+"</i><br>"
                        + "<a href=\"" + provenance + "\">RDF Datasets</a>: "+allDtsets+"\n"
                        + "<br><a href=\"" + uris + "\">URIs</a>: " + allURIs + "\n"
                        + "<br><a href=\"" + facts + "\">Facts</a>: " +triples+"\n"
                        //+ "<br><a href=\"" + topk + "\">Find Top-K datasets</a>\n"
                        + "</span>"
                        + "</div>";
                id++;
            }

        }
        output += "</div>";
        //System.out.println(output);
        return output;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
