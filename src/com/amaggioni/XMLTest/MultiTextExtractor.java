/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.amaggioni.XMLTest;

import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * ***********************************************************************************************
 *
 * @author magang
 * create date : 03.01.2013
 * change date : 11.03.2013
 *
 * description: The class manage a trading strategy for multiple symbols
 *
 * flow:
 
 *
 * Change Log:
 * 28.01.2013 OCAGroup added by Orders
 
 */
public class MultiTextExtractor implements ContentHandler {

    private List documents;
  
  // This field is deliberately not initialized in the
  // constructor. It is initialized for each document parsed, not
  // for each object constructed.
  private StringBuffer currentDocument;
  
  public MultiTextExtractor(List documents) {
    
    if (documents == null) {
      throw new NullPointerException(
       "Documents list must be non-null");
    }
    this.documents = documents;   
  }

  // Initialize the per-document data structures
  public void startDocument() {
    
    currentDocument = new StringBuffer();
    
  }
  
  // Flush and commit the per-document data structures
  public void endDocument() {
    
    String text = currentDocument.toString();
    documents.add(text);
    
  }
    
  // Update the per-document data structures
  public void characters(char[] text, int start, int length) {

    currentDocument.append(text, start, length); 
      
  }  
    
  // do-nothing methods
  public void setDocumentLocator(Locator locator) {}
  public void startPrefixMapping(String prefix, String uri) {}
  public void endPrefixMapping(String prefix) {}
  public void startElement(String namespaceURI, String localName,
   String qualifiedName, Attributes atts) {}
  public void endElement(String namespaceURI, String localName,
   String qualifiedName) {}
  public void ignorableWhitespace(char[] text, int start, 
   int length) {}
  public void processingInstruction(String target, 
   String data) {}
  public void skippedEntity(String name) {}

    

}
