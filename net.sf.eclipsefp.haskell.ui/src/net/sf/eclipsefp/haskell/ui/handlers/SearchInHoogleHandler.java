/**
 * (c) 2011, JP Moresmau
 * Released under the terms of the EPL.
 */
package net.sf.eclipsefp.haskell.ui.handlers;

import net.sf.eclipsefp.haskell.browser.views.hoogle.HoogleView;
import net.sf.eclipsefp.haskell.ui.internal.editors.haskell.HaskellEditor;
import net.sf.eclipsefp.haskell.ui.util.text.WordFinder;
import net.sf.eclipsefp.haskell.ui.util.text.WordFinder.EditorThing;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * Handler to search for selected element in Hoogle
 * @author JP Moresmau
 *
 */
public class SearchInHoogleHandler extends AbstractHandler {

  public Object execute( final ExecutionEvent event ) {
    IEditorPart editor = HandlerUtil.getActiveEditor( event );
    if( !( editor instanceof HaskellEditor ) ) {
      return null;
    }

    final HaskellEditor haskellEditor = ( HaskellEditor )editor;
    WordFinder.getEditorThing( haskellEditor, false, false,new WordFinder.EditorThingHandler() {

      public void handle( final EditorThing thing ) {
        String name = thing.getName();
        HoogleView.searchHoogle( name );
      }
    });
    return null;
  }

}
