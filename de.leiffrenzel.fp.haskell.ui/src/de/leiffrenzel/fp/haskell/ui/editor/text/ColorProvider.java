// Copyright (c) 2003-2005 by Leif Frenzel - see http://leiffrenzel.de
package de.leiffrenzel.fp.haskell.ui.editor.text;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import de.leiffrenzel.fp.haskell.ui.HaskellUIPlugin;
import de.leiffrenzel.fp.haskell.ui.preferences.editor.IEditorPreferenceNames;


/** <p>Provides colors for syntax coloring in the editor.</p>
  * 
  * <p>This is implemented as singleton to make it accessible from 
  * everywhere and also to reduce resource management to one single place.</p>
  * 
  * @author Leif Frenzel
  */
public class ColorProvider implements IEditorPreferenceNames {

  public static final RGB DEFAULT_COMMENT           = new RGB( 128, 128, 192 );
  public static final RGB DEFAULT_LITERATE_COMMENT  = new RGB( 128, 128, 192 );
  public static final RGB DEFAULT_KEYWORD           = new RGB( 0, 0, 196 );
  public static final RGB DEFAULT_FUNCTION          = new RGB( 64, 192, 192 );
  public static final RGB DEFAULT_STRING            = new RGB( 128, 64, 64 );
  public static final RGB DEFAULT_OTHER             = new RGB( 0, 0, 0 );

  /** the singleton instance of ColorProvider. */
  private static ColorProvider _instance;

  private HashMap colors;
  private HashMap rgbs;

  /** <p>constructs the singleton instance of ColorProvider. Private in order
   * to ensure the singleton pattern.</p> */
  private ColorProvider() {
    colors = new HashMap( 10 );
    rgbs = new HashMap( 10 );
    initializeRgbs();
  }

  public static synchronized ColorProvider getInstance() {
    if( _instance == null ) {
      _instance = new ColorProvider();
    }
    return _instance;
  }

  /** <p>releases all of the color resources held by this ColorProvider.</p> */ 
  public void dispose() {
    Iterator it = colors.values().iterator();
    while( it.hasNext() ) {
      ( ( Color )it.next() ).dispose();
    }
  }

  public Color getColor( final String key ) {
    RGB rgb = ( RGB )rgbs.get( key );
    Assert.isNotNull( rgb );
    return getColor( rgb );
  }

  void changeColor( final String key, final Object newValue ) {
    RGB oldRgb = ( RGB )rgbs.get( key );
    if( oldRgb != null ) {
      RGB newRgb = getNewRgb( newValue );
      if( newRgb != null ) {
        rgbs.put( key, newRgb );
      }
    }
  }
  

  // helping methods
  //////////////////

  private RGB getNewRgb( final Object value ) {
    RGB result = null;
    if( value instanceof RGB ) {
      result = ( RGB )value;
    } else if( value instanceof String ) {
      result = StringConverter.asRGB( ( String )value );
    }
    return result;
  }

  private Color getColor( final RGB rgb ) {
    Color color = ( Color )colors.get( rgb );
    if( color == null ) {
      color = new Color( Display.getCurrent(), rgb );
      colors.put( rgb, color );
    }
    return color;
  }
  
  private void initializeRgbs() {
    putRgb( EDITOR_COMMENT_COLOR, DEFAULT_COMMENT );
    putRgb( EDITOR_LITERATE_COMMENT_COLOR, DEFAULT_LITERATE_COMMENT );
    putRgb( EDITOR_FUNCTION_COLOR, DEFAULT_FUNCTION );
    putRgb( EDITOR_KEYWORD_COLOR, DEFAULT_KEYWORD );
    putRgb( EDITOR_DEFAULT_COLOR, DEFAULT_OTHER );    
    putRgb( EDITOR_STRING_COLOR, DEFAULT_STRING );
  }
  
  private void putRgb( final String key, final RGB defaultRgb ) {
    RGB rgb = PreferenceConverter.getColor( getPreferenceStore(), key );
    if( rgb == null ) {
      rgb = defaultRgb;
    }
    rgbs.put( key, rgb );
  }

  private IPreferenceStore getPreferenceStore() {
    return HaskellUIPlugin.getDefault().getPreferenceStore();
  }
}