/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gr.forth.ics.isl.demoExternal.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author micha
 */
public class SPARQLQuery {
    String columnNames[];
    ConnectToVirtuoso cn = new ConnectToVirtuoso();
    Connection conn;
    
    /**
     *
     */
    public  SPARQLQuery(){
        try {
           
            cn.startJDBC_Connection();
        } catch (ClassNotFoundException ex) {
          //  Logger.getLogger(Create_URI_Sequences.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
           // Logger.getLogger(Create_URI_Sequences.class.getName()).log(Level.SEVERE, null, ex);
        }
        conn = ConnectToVirtuoso.getJDBC_Connection();
    }
    /**
     * It sets the columnLabels with the array columns[]
     *
     * @param columns String[] which includes column names
     */
    public void setColumnLabels(String columns[]) {
        columnNames = columns;
    }
    
    /**
     * It runs a SPARQL query
     * @param query 
     * @param provenance
     * @param prefix
     * @return
     */
    public String[][] runSPARQLQuery(String query, boolean provenance, boolean prefix, String spl) {

        HashSet<String> results;
        results = this.ExecuteQuery(query, false);
        if(results==null)
            return null;
        String[][] toGui = new String[results.size() + 1][this.getColumnLabels().length + 1];
        int counter = 0;
        toGui[counter++] = this.getColumnLabels();
        for (String triple : results) {
            String str = triple;

            String[] parts = str.split(spl);
            if (prefix == false) {
                for (int i = 0; i < parts.length; i++) {
                    String[] split = parts[i].split("/");
                    String[] splitCell = split[split.length - 1].split("#");
                    parts[i] = splitCell[splitCell.length - 1];
                }
            }

            toGui[counter] = parts;
            counter++;
        }
        return toGui;
    }

    /**
     *
     * @param str
     * @param isFile
     * @return
     */
    public HashSet<String> ExecuteQuery(String str, boolean isFile) {
         HashSet<String> ret = new HashSet<String>();
        try {
            String query;
            if (!str.toLowerCase().contains("rdfs_rule_set") && !str.toLowerCase().contains("delete") && !str.toLowerCase().contains("update db") && !str.toLowerCase().contains("db.dba.sys_rdf_schema")) {
                query = "SPARQL " + str;
            } else {
                query = str;
            }

            String triples[];
            ResultSetMetaData meta;
            Statement stmt;
            ResultSet result;
            int count;

                stmt = conn.createStatement();
            
            if (query.toLowerCase().contains("delete") || query.toLowerCase().contains("insert") || query.toLowerCase().contains("update db")) {
                stmt.execute(query);
                stmt.close();
                return null;
            }
            result = stmt.executeQuery(query);
            meta = result.getMetaData();
            count = meta.getColumnCount();
            String columns[] = new String[count];
            for (int g = 1; g <= count; g++) {
                columns[g - 1] = meta.getColumnLabel(g);
            }
            this.setColumnLabels(columns);
            while (result.next()) {
                String triple = "";
                for (int i = 1; i <= count; i++) {
                    if (i > 1) {
                        triple += "\t";
                    }
                    PrintStream out = new PrintStream(System.out, true, "UTF-8");
                    if (result.getString(i) == null || (result.getString(i) != null)) {
                        triple += result.getString(i);
                    } else {
                        triple += "BNode";
                    }

                }
                ret.add(triple);
            }
            result.close();
            stmt.close();

        } catch (SQLException ex) {
           Logger.getLogger(SPARQLQuery.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SPARQLQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
        ret.remove(null);
        ret.remove("");
        return ret;
    }
    
    /**
     *
     * @return
     */
    public String[] getColumnLabels() {
        return columnNames;
    }
     
    /**
     *
     * @param query
     * @param endpoint
     * @return
     * @throws UnsupportedEncodingException
     * @throws MalformedURLException
     * @throws IOException
     */
    public String dbpediaQuery(String query, String endpoint) throws UnsupportedEncodingException, MalformedURLException, IOException {

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

}
