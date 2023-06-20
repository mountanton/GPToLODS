/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.forth.ics.isl.demoExternal.LODsyndesis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.TreeMap;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author micha
 */
public class WAT_REST {

    public HashMap<String,String> getWAT (String text) throws IOException, JSONException {
        HashMap<String,String> results=new HashMap<String,String>();
        URL urlForGetRequest = new URL("https://wat.d4science.org/wat/tag/tag?lang=en&gcube-token=6cad538c-ea3c-403a-ab4a-11ec94dbe12c-843339462&text=" + text.replaceAll(" ", "%20").replaceAll("’",""));
        String readLine = null;
        HttpURLConnection conection = (HttpURLConnection) urlForGetRequest.openConnection();
        conection.setRequestMethod("GET");
        //  conection.setRequestProperty("lang", "en"); // set userId its a sample here
        // conection.setRequestProperty("gcube-token","6cad538c-ea3c-403a-ab4a-11ec94dbe12c-843339462");
        // conection.setRequestProperty("text", "Obama+visited+U.K.+in+March"); 
        int responseCode = conection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conection.getInputStream()));
            StringBuffer response = new StringBuffer();
            while ((readLine = in.readLine()) != null) {
                response.append(readLine);
            }
            in.close();
            // print result
            System.out.println("JSON String Result " + response.toString());
            //GetAndPost.POSTRequest(response.toString());
            JSONObject js = new JSONObject(response.toString());
            JSONArray array = js.getJSONArray("annotations");
            for (int i = 0; i < array.length(); i++) {
                JSONObject row = array.getJSONObject(i);
                String spot = row.getString("spot");//replace("the ","");
                String title ="http://dbpedia.org/resource/"+row.getString("title");
                results.put(spot.toLowerCase(),title);
                //System.out.println(spot+" "+title);
            }
        } else {
            System.out.println(responseCode + " GET NOT WORKED");
        }
        return results;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, JSONException {
         String text = "Jason had won the favor of the goddesses Hera and Athena. With their help Jason built the fabled ship Argo, which had 50 oars. He recruited 50 remarkable people called the Argonauts. They included one woman, Atalanta, and Hercules, the strongest man who lived ever. Orpheus, the poet from Thrace, who could sing more sweetly than the Sirens, as well as Castor and Polydeukis, the brothers of Helen of Troy, were also in the team of the Argonauts." +
"Jason and the Argonauts set sail for the Black Sea where the legend said the Golden Fleece was hidden. After many adventures, the Argonauts reached the kingdom. The king, whose help the Argonauts needed, imposed seemingly impossible tasks upon Jason. One was to harness the fire breathing bulls with brazen feet and plow a field. Then he was to sow the plowed field with dragons’ teeth, from which would spring fully armed warriors.";//"Jason had won the favor of the goddesses Hera and Athena. With their help Jason built the fabled ship Argo, which had 50 oars. He recruited 50 remarkable people called the Argonauts. They included one woman, Atalanta, and Hercules, the strongest man who lived ever. Orpheus, the poet from Thrace, who could sing more sweetly than the Sirens, as well as Castor and Polydeukis, the brothers of Helen of Troy, were also in the team of the Argonauts.";
        
        WAT_REST wr=new WAT_REST();
        HashMap<String,String> res=wr.getWAT(text);
        for(String key:res.keySet()){
            System.out.println(key+" "+res.get(key));
        }
    }

}
