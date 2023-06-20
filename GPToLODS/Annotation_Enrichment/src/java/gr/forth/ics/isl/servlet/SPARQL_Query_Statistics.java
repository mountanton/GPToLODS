/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.forth.ics.isl.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 *
 * @author micha
 */
public class SPARQL_Query_Statistics {
    
     String endpoint = "http://83.212.97.78:8890/sparql";

    
    public int sendQuery(String query) throws UnsupportedEncodingException, MalformedURLException, IOException {
        String sparqlQueryURL = endpoint + "?query=" + URLEncoder.encode(query, "utf8");
        URL url = new URL(sparqlQueryURL);
        URLConnection con = url.openConnection();
        String type = "text/tab-separated-values";
        con.setRequestProperty("ACCEPT", type);

        InputStream is = con.getInputStream();
        InputStreamReader isr = new InputStreamReader(is, "utf8");
        BufferedReader in = new BufferedReader(isr);

        String input;
        String resultsString = "";
        int count = 0;
        while ((input = in.readLine()) != null) {
            resultsString = input.replace("^^http://www.w3.org/2001/XMLSchema#integer", "").replace("\"", "");
            //System.out.println(resultsString);
        }

        in.close();
        isr.close();
        is.close();
        return Integer.parseInt(resultsString);

    }
    
    public String sendCountQuery(String query) throws UnsupportedEncodingException, MalformedURLException, IOException {
        String sparqlQueryURL = endpoint + "?query=" + URLEncoder.encode(query, "utf8");
        URL url = new URL(sparqlQueryURL);
        URLConnection con = url.openConnection();
        String type = "text/tab-separated-values";
        con.setRequestProperty("ACCEPT", type);
        //http://www.ics.forth.gr/isl/directCount

        InputStream is = con.getInputStream();
        InputStreamReader isr = new InputStreamReader(is, "utf8");
        BufferedReader in = new BufferedReader(isr);

        String input;
        String resultsString = "";
        int count = 0;
        while ((input = in.readLine()) != null) {
            resultsString = input.replace("^^http://www.w3.org/2001/XMLSchema#integer", "").replace("\"", "");
            //System.out.println(resultsString);
        }
        in.close();
        isr.close();
        is.close();
        return resultsString;

    }
    
}
