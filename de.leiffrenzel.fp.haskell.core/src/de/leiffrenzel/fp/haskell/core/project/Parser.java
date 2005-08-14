// Copyright (c) 2003-2005 by Leif Frenzel - see http://leiffrenzel.de
package de.leiffrenzel.fp.haskell.core.project;

import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IFile;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import de.leiffrenzel.fp.haskell.core.HaskellCorePlugin;

/** <p>A mini parser to get the project descriptor information from the
  * .hsproject file into an IHaskellProject.</p>
  * 
  * @author Leif Frenzel
  */
class Parser implements IXMLNames {

  static void readIn( final IFile projectDescriptor, 
                      final HaskellProject project ) {
    try {
      Reader reader = new InputStreamReader( projectDescriptor.getContents() );
      Element rootElement = null;
      try {
        DocumentBuilder parser 
          = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource inputSource = new InputSource( reader );
        rootElement = parser.parse( inputSource ).getDocumentElement();
      } catch( Exception ex ) {
        HaskellCorePlugin.log( "Problem when parsing .hsproject file.\n", ex );
      } finally {
        reader.close();
      }
      applyPaths( project, rootElement );
    } catch( Exception ex ) {
      HaskellCorePlugin.log( "Problem when reading .hsproject file.\n", ex );
    }
  }


  // helping methods
  //////////////////

  private static String getValue( final Element rootElement,
                                  final String tagName,
                                  final String attributeName ) {
    String result = "";
    NodeList list = rootElement.getElementsByTagName( tagName );
    if( list.getLength() > 0 ) {
      // we use only the first entry for each path type
      NamedNodeMap attributes = list.item( 0 ).getAttributes();
      Node pathNode = attributes.getNamedItem( attributeName );
      result = pathNode.getNodeValue(); 
    }
    return result;
  }

  private static void applyPaths( final HaskellProject project, 
                                  final Element rootElement ) {
    if(    rootElement != null 
        && rootElement.getNodeName().equalsIgnoreCase( DOCUMENT_ELEMENT ) ) {
      project.setSourcePath( getValue( rootElement,
                                       SOURCE_PATH_ELEMENT, 
                                       PATH_ATT ) );
      project.setOutputPath( getValue( rootElement, 
                                       OUTPUT_PATH_ELEMENT,
                                       PATH_ATT ) );
      project.setBinPath( getValue( rootElement, 
                                    BIN_PATH_ELEMENT,
                                    PATH_ATT ) );
      project.setTargetName( getValue( rootElement,
                                       TARGET_NAME_ELEMENT,
                                       NAME_ATT ) );
    }
  }
}