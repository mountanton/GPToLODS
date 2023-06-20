/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.forth.ics.isl.demoExternal.LODsyndesis;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreNLPProtos.Sentence;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import gr.forth.ics.isl.nlp.NlpAnalyzer;
import java.util.List;


public class CoreNLPTest {

  public static String text = "Joe Smith was born in California. "+
  "Study studying studied. " +
  "In 2017, he went to Paris, France in the summer. " +
  "His flight left at 3:00pm on July 10th, 2017. " +
  "After eating some escargot for the first time, Joe said, \"That was delicious!\" " +
  "He sent a postcard to his sister Jane Smith. " +
  "He is ok. " +
  "Simple, right? Remove removed removing was were is are element at given gave give index, insert it at desired index. Let's see if it works for the second test case."+
  "He is ok to go now. " +
  "After hearing about Joe's trip, Jane decided she might go to France one day.";

public static void main(String[] args) {
   NlpAnalyzer n=new NlpAnalyzer();
    System.out.println(NlpAnalyzer.getWordsWithPosNer(text));

}

}