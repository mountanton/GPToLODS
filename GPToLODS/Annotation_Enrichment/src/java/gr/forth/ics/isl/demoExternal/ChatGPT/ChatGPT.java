/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.forth.ics.isl.demoExternal.ChatGPT;

import gr.forth.ics.isl.demoExternal.LODsyndesis.LODsyndesisIERestClient;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.codehaus.jettison.json.JSONObject;


/**
 *
 * @author mountant
 */
public class ChatGPT {
 
    
    
    
    public String getChatGPTResponse(String text,String model) throws Exception{
        if(model.equals("davinci")){
            return chatGPT(text);
        }
        else if(model.equals("turbo")){
            return chatGPT_TURBO(text);
        }
        
        
        return "";//"Panathinaikos BC, a professional basketball team from Athens, Greece, won the EuroLeague championship in 2011. They defeated Maccabi Tel Aviv in the final game with a score of 78-70, which was held on May 15, 2011, at the Palais Omnisports de Paris-Bercy in Paris, France. This was the sixth time that Panathinaikos BC won the EuroLeague title, and it was a significant achievement for the team and its fans."; //
                
                //"The birthplace of Aristotle is the ancient Greek city of Stagira, which is located in what is now known as the Chalkidiki peninsula of Greece.";//"Barack Obama was born in Honolulu, Hawaii.";
               
                //"scdwfwefewfew dfer";
            //   "Aristotle was an ancient Greek philosopher and scientist born in 384 BC in Stagira, Greece. He was a student of Plato and teacher of Alexander the Great. He wrote on many topics, including physics, metaphysics, poetry, theater, music, logic, rhetoric, politics, government, ethics, biology, and zoology. He is considered one of the most influential figures in Western philosophy and science. Aristotle's writings shaped the Western philosophical tradition for over two thousand years. His works were the first to create a comprehensive system of Western philosophy, encompassing morality, aesthetics, logic, science, politics, and metaphysics.";//chatGPT(text);
    }
    
    
    public String getChatGPTfacts(){
        String facts="1. <Aristotle> <born_in> <Stagira> .\n"
                + "2. <Aristotle> <born_in> <Chalcidice> .\n"
                + "3. <Stagira> <located_in> <northern Greece> .\n"
                + "4. <Aristotle> <born_in_year> <384 BC> .";
        ArrayList<String> allFacts=new   ArrayList<String>();
        String[] split=facts.split("\n");
        for(String str: split){
            String fact=str.replaceAll("<", "").replaceAll(">", "").replaceAll("_", " ").replace(".", " ").replace(" ","%20");
            LODsyndesisIERestClient ld=new LODsyndesisIERestClient();
            System.out.println(ld.getEntities(fact));
            
        }
        return "";
    }
    
    public static void main(String[] args) throws Exception {
        ChatGPT gpt=new ChatGPT();
        //gpt.getChatGPTfacts();
        

         chatGPT("Hello, how are you?");
    }
    
      public static String chatGPT_TURBO(String text) throws Exception {
         String url = "https://api.openai.com/v1/chat/completions";
         HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();

         con.setRequestMethod("POST");
         con.setRequestProperty("Content-Type", "application/json");
         //con.setRequestProperty("Authorization", "Bearer YOUR-API-KEY");
         con.setRequestProperty("Authorization", "Bearer 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");


         JSONObject data = new JSONObject();
         data.put("model", "gpt-3.5-turbo");
         data.put("messages", "[{'role': 'user', 'content': 'MyText'}]");
         data.put("temperature", 1.0);
         data.put("max_tokens", 1000);
        
         String body=data.toString().replace("\"[", "[").replace("]\"", "]").replace("'","\"").replace("MyText", text);
          System.out.println(body);
         con.setDoOutput(true);
         con.getOutputStream().write(body.toString().getBytes());

         String output = new BufferedReader(new InputStreamReader(con.getInputStream())).lines()
                 .reduce((a, b) -> a + b).get();

         return new JSONObject(output).getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
     }
    
      public static String chatGPT(String text) throws Exception {
         String url = "https://api.openai.com/v1/completions";
         HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();

         con.setRequestMethod("POST");
         con.setRequestProperty("Content-Type", "application/json");
         //con.setRequestProperty("Authorization", "Bearer YOUR-API-KEY");
         con.setRequestProperty("Authorization", "Bearer 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");


         JSONObject data = new JSONObject();
         data.put("model", "text-davinci-003");
         data.put("prompt", text);
         //data.put("max_tokens", 4000);
         data.put("max_tokens", 4000);

         data.put("temperature", 1.0);

         con.setDoOutput(true);
         con.getOutputStream().write(data.toString().getBytes());

         String output = new BufferedReader(new InputStreamReader(con.getInputStream())).lines()
                 .reduce((a, b) -> a + b).get();

         return new JSONObject(output).getJSONArray("choices").getJSONObject(0).getString("text");
     }


     public static void startDialogue() throws Exception {
         Scanner myObj = new Scanner(System.in);  // Create a Scanner object
         String userQ;
         System.out.print("- ");
         while(1==1) {
             userQ = myObj.nextLine();  // Read user input
             //System.out.println("Username is: " + userQ);  // Output user input
             System.out.print("- ");
             chatGPT(userQ);
             System.out.print("- ");
          }

     }

    
}
