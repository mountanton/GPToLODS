/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.forth.ics.isl.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author mountant
 */
public class Text {

    String text = "", textID = "";
    HashSet<String> entities = new HashSet<String>();
    int textEntities;
    HashMap<String, Double> precision = new HashMap<String, Double>();
    HashMap<String, Double> recall = new HashMap<String, Double>();
    HashMap<String, Double> fScore = new HashMap<String, Double>();

    HashMap<String, Integer> recEntities = new HashMap<String, Integer>();

    
     HashMap<String, Float> efficiency = new HashMap<String, Float>();
    HashMap<String, Integer> interEntities = new HashMap<String, Integer>();

    

    public String getText() {
        return text;
    }

    public int wordsNum(){
        return text.split(" ").length;
    }
    
    public void setText(String text) {
        this.text = text;
    }

    public HashSet<String> getEntities() {
        return entities;
    }

    public void setEntities(HashSet<String> entities) {
        this.entities = entities;
    }

    public int getTextEntities() {
        return textEntities;
    }

    public void setTextEntities(int textEntities) {
        this.textEntities = textEntities;
    }

}
