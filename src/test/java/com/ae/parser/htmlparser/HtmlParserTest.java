package com.ae.parser.htmlparser;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.util.Strings;
import org.jsoup.nodes.Element;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HtmlParserTest {

  private static final String COMPONENT_ID = "#make-everything-ok-button";
  private static final String PATH_HTML_ORIGIN = "src/test/resources/origin.html";
  private static final String PATH_HTML_TARGET = "src/test/resources/sample-evil-gemini.html";
  private static final String PATH_TO_COMPONENT = "src/test/resources/component.txt";
  private static final String PATH_TO_ELEMENT = "src/test/resources/pathToElement.txt";
  private static final String NOT_AN_ID = "notAnInd";

  @Test
  public void testFindComponent(){
    Element element = HtmlParserApplication.findBestCandidate(COMPONENT_ID,PATH_HTML_ORIGIN,PATH_HTML_TARGET);
    assertThat(element.toString(),is(getContentFromFile(PATH_TO_COMPONENT)));
    assertThat(HtmlParserApplication.pathToElement(element),is(getContentFromFile(PATH_TO_ELEMENT)));
  }

  @Test(expected = RuntimeException.class)
  public void testNullId(){
    HtmlParserApplication.findBestCandidate(null,PATH_HTML_ORIGIN,PATH_HTML_TARGET);
  }

  @Test(expected = RuntimeException.class)
  public void testComponentNotFound(){
    HtmlParserApplication.findBestCandidate(NOT_AN_ID,PATH_HTML_ORIGIN,PATH_HTML_TARGET);
  }

  private String getContentFromFile(String path){
    try{
     return FileUtils.readFileToString(new File(path), StandardCharsets.UTF_8.name());
    }catch (Exception e){
     return Strings.EMPTY;
    }
  }

}

