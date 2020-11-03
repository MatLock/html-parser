package com.ae.parser.htmlparser;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.FuzzyScore;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class HtmlParserApplication {

    private static final String SCORE_MESSAGE = "Element {} Has Scored:  {}   ";
    private static final String CANDIDATE_HIGHEST_SCORE_MESSAGE = "Candidate with highest score: {}";
    private static final String STYLE_ATTR_KEY = "style";
    private static final String INVALID_ATTR_VALUE = "display:none";
    private static final String PATH_TO_ELEMENT = "Path to Element is {}";
    private static final String NOT_AN_ID = "ID cannot be empty";
    private static final String ID_NOT_FOUND = "Component Id not found";
    private static Logger LOGGER = LoggerFactory.getLogger(HtmlParserApplication.class);

	public static void main(String[] args) {

        try{
            String origin = args[1];
            String target = args[2];
            String id = args[0];
            Element element = findBestCandidate(id,origin,target);
            String path = pathToElement(element);
            LOGGER.info(PATH_TO_ELEMENT,path);
        }catch (Exception e) {
          throw new RuntimeException(e);
        }
	}

    public static String pathToElement(Element element){
      Element parent = element.parent();
      String pathToTarget = element.toString();
      while (parent.hasParent()){
          int index = parent.elementSiblingIndex();
          if(index == 0){
            pathToTarget = parent.tagName() + " > " + pathToTarget;
          }else{
            pathToTarget = parent.tagName()+"["+String.valueOf(index)+"]" + " > " + pathToTarget;
          }
          parent = parent.parent();
      }
      pathToTarget = parent.tagName() + " > " + pathToTarget;
      return  pathToTarget;
    }

    private static Map<Element,Integer> findComponentsSlylyDifferent(String id, String pathToHTMLOrigin, String pathToHTMLTarget){
      try{
          Document origin = openFile(pathToHTMLOrigin);
          Document target = openFile(pathToHTMLTarget);
          Element componentToFind = findElementById(origin,id);
          Map<String,String> maps = extractAttributesFrom(componentToFind);
          List<Element> candidates = findCandidates(maps,target);
          return sortByScore(componentToFind,candidates);
      }catch(Exception e){
        throw new RuntimeException(e.getMessage());
      }
    }

    public static Element findBestCandidate(String id, String pathToHTMLOrigin, String pathToHTMLTarget){
      Map<Element,Integer> candidates = findComponentsSlylyDifferent(id,pathToHTMLOrigin,pathToHTMLTarget);
      Element element = candidates.keySet().iterator().next();
      candidates.forEach((key,value) -> LOGGER.info(SCORE_MESSAGE,key ,value.toString()));
      LOGGER.info(CANDIDATE_HIGHEST_SCORE_MESSAGE,element.toString());
      return element;
    }

    private static Map<Element,Integer> sortByScore(Element component,List<Element> candidates){
        Map<Element,Integer> map = new HashMap<>();
        candidates.forEach(elem -> map.put(elem,scoring(component,elem)));
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }


    private static Integer scoring(Element origin,Element target){
      Integer score = 0;
      FuzzyScore fuzzyScore = new FuzzyScore(Locale.ENGLISH);
      Iterator<Attribute> iterator = target.attributes().iterator();
      while (iterator.hasNext()){
        Attribute attrTarget = iterator.next();
        if(STYLE_ATTR_KEY.equals(attrTarget.getKey()) && INVALID_ATTR_VALUE.equals(attrTarget.getValue())){
          return 0;
        }
        if(origin.hasAttr(attrTarget.getKey())){
            String valueTarget = attrTarget.getValue();
            String valueOrigin = origin.attr(attrTarget.getKey());
            score +=fuzzyScore.fuzzyScore(valueOrigin,valueTarget);
        }
      }
      score += fuzzyScore.fuzzyScore(origin.text(),target.text());
      return score;
    }

    private static List<Element> findCandidates(Map<String,String>attrs, Document document){
      List<Element>candidates = new ArrayList<>();
      attrs.forEach((key,value) -> candidates.addAll(document.getElementsByAttributeValue(key,value)));
      return candidates;
    }

    private static Map<String,String> extractAttributesFrom(Element target){
      return  target.attributes()
                    .asList()
                    .stream()
                    .collect(Collectors.toMap(Attribute::getKey,Attribute::getValue));
    }


    private static Element findElementById(Document target, String id){
      if(StringUtils.isEmpty(id)){
        throw new RuntimeException(NOT_AN_ID);
      }
        Element element = target.selectFirst(id);
        if(element == null){
          throw new RuntimeException(ID_NOT_FOUND);
        }
      return element;
    }

    private static Document openFile(String path){
      try{
          File file = new File(path);
          return Jsoup.parse(file, StandardCharsets.UTF_8.name());
      }catch(Exception e){
        LOGGER.info("Provided File not Found {}", path);
        throw new RuntimeException();
      }
    }

}
