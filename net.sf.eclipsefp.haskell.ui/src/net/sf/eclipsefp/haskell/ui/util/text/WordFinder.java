// Copyright (c) 2004-2011 by Leif Frenzel & JP Moresmau
// See http://leiffrenzel.de
package net.sf.eclipsefp.haskell.ui.util.text;

import net.sf.eclipsefp.haskell.core.parser.ParserUtils;
import net.sf.eclipsefp.haskell.scion.client.ScionInstance;
import net.sf.eclipsefp.haskell.scion.client.ScionPlugin;
import net.sf.eclipsefp.haskell.scion.types.Location;
import net.sf.eclipsefp.haskell.scion.types.ThingAtPointHandler;
import net.sf.eclipsefp.haskell.ui.internal.editors.haskell.HaskellEditor;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;


/** <p>A helping class for detecting words in documents.</p>
  *
  * <p>Taken from
  * <code>org.eclipse.jface.text.DefaultTextDoubleClickStrategy</code>.</p>
  *
  * @author Leif Frenzel & JP Moresmau
  */
public class WordFinder {

  //private final DocumentCharacterIterator docIter = new DocumentCharacterIterator();

  public static String findWord( final IDocument document, final int position ) {
    //IRegion result = null;
    if(document!=null && position >= 0 ) {
      try {
        IRegion r=document.getLineInformationOfOffset( position );
        String line=document.get( r.getOffset(), r.getLength() );
        int off=position-r.getOffset();
        return ParserUtils.getHaskellWord(line,off);

        /*IRegion line = document.getLineInformationOfOffset( position );

        int lineEnd = line.getOffset() + line.getLength();
        if( position != lineEnd ) {
          BreakIterator breakIter = BreakIterator.getWordInstance();
          docIter.setDocument( document, line );
          breakIter.setText( docIter );
          int start = breakIter.preceding( position );
          if( start == BreakIterator.DONE ) {
            start = line.getOffset();
          }
          int end = breakIter.following( position );
          if( end == BreakIterator.DONE ) {
            end = lineEnd;
          }
          if( breakIter.isBoundary( position ) ) {
            if( end - position > position - start ) {
              start = position;
            } else {
              end = position;
            }
          }
          if( start != end ) {
            result = new Region( start, end - start );
          }
        }*/
      } catch( final BadLocationException badlox ) {
        badlox.printStackTrace();
      }
    }
    return null;
    //return result;
  }

  public static EditorThing getEditorThing(final HaskellEditor haskellEditor,final boolean qualify, final boolean typed,final EditorThingHandler handler){
    final IFile file = haskellEditor.findFile();
    final ISelection selection = haskellEditor.getSelectionProvider().getSelection();

    if( selection instanceof TextSelection && file!=null) {
          final ScionInstance instance = ScionPlugin.getScionInstance( file );
          if (instance!=null){
            final TextSelection textSel = ( TextSelection )selection;
            final String fName = textSel.getText().trim();
            try {
              Location l = new Location( file.getLocation().toOSString(),
                  haskellEditor.getDocument(), new Region( textSel.getOffset(), 0 ) );
              instance.thingAtPoint(haskellEditor.getDocument(), l, qualify, typed,new ThingAtPointHandler() {

                @Override
                public void handleThing( final String thing ) {
                  char haddockType = ' ';
                  String name=fName;
                  if( thing != null && thing.length() > 0 ) {
                    name = thing;
                    if (name.startsWith("expr: ") || name.startsWith("bind:") || name.startsWith("stmt: ")){
                      name="";
                    }
                    if( name.length() > 2 && name.charAt( name.length() - 2 ) == ' ' ) {
                      haddockType = name.charAt( name.length() - 1 );
                      name = name.substring( 0, name.length() - 2 );
                    }
                  }


                  if( name.length() == 0 ) {
                    name = WordFinder.findWord( haskellEditor.getDocument(),
                        textSel.getOffset() );
                  }
                  if (name!=null && name.length()>0){
                    handler.handle( new EditorThing(instance, file, name, haddockType ));
                  }
                }
              });

            } catch( BadLocationException ble ) {
              ble.printStackTrace();
            }
          }
    }
    return null;
  }

  /**
   * Simple structure wrapping a word in the editor with some useful pointers
    *
    * @author JP Moresmau
   */
  public static class EditorThing {
    private final String name;
    private final char haddockType;
    private final ScionInstance instance;
    private final IFile file;

    public EditorThing(final ScionInstance instance,final IFile file, final String name, final char haddockType ) {
      super();
      this.instance=instance;
      this.file=file;
      this.name = name;
      this.haddockType = haddockType;
    }


    public ScionInstance getInstance() {
      return instance;
    }


    public IFile getFile() {
      return file;
    }

    public String getName() {
      return name;
    }


    public char getHaddockType() {
      return haddockType;
    }

  }

  /**
   * <p>Callback interface</p>
    *
    * @author JP Moresmau
   */
  public static interface EditorThingHandler{
    void handle(EditorThing thing);
  }
}
