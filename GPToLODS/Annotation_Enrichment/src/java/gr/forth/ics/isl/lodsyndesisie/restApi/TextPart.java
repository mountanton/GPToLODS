/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.forth.ics.isl.lodsyndesisie.restApi;

import java.util.ArrayList;

/**
 *
 * @author mountant
 */
public class TextPart {
    String textpart, img;
    boolean isEntity;
    String type;
    String lodsyndesisURL;
    String moreInfo;
    String dbpediaURI;
    ArrayList<String> otherURIs=new ArrayList<String>();
}
