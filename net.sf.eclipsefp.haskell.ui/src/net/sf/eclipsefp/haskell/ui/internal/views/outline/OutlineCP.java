package net.sf.eclipsefp.haskell.ui.internal.views.outline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.eclipsefp.haskell.scion.types.OutlineDef;
import net.sf.eclipsefp.haskell.scion.types.OutlineDef.OutlineDefType;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * <p>Manages a list of OutlineDef for Outline view</p>
  *
  * @author JP Moresmau
 */
public class OutlineCP implements ITreeContentProvider{
  private Map<String,List<OutlineDef>> input;

  /**
   * For elements that expand into an identically-named single element with an obvious
   * type, we could just expand directly into that child element's children.
   */
  public boolean hasSingularChild( final Object o ){
    if( ((OutlineDef)o).getType() != OutlineDefType.DATA ) {
      return false;
    }

    Object[] children = getRawChildren( o );

    if( children.length != 1 ) {
      return false;
    }

    return ((OutlineDef)children[0]).getType() == OutlineDefType.CONSTRUCTOR &&
           ((OutlineDef)children[0]).getName().equals( ((OutlineDef)o).getName() );
    }

  public Object[] getChildren( final Object parentElement ) {
    Object[] result = getRawChildren( parentElement );
    if( hasSingularChild( parentElement ) ) {
      return getChildren( result[0] );
    } else {
      return result;
    }
  }

  public Object[] getRawChildren( final Object parentElement ) {
    List<OutlineDef> l=input.get(((OutlineDef )parentElement).getID());
    if (l!=null){
      return l.toArray();
    }
    return new Object[0];
  }

  public Object getParent( final Object element ) {
    return null;
  }

  public boolean hasChildren( final Object element ) {
   return true;
  }

  public Object[] getElements( final Object inputElement ) {
     //return input.toArray();
    List<OutlineDef> l=input.get( null );
    if (l!=null){
      return l.toArray();
    }
    return new Object[0];
  }

  public void dispose() {
   input=null;

  }

  public void inputChanged( final Viewer viewer, final Object oldInput, final Object newInput ) {
   if (newInput instanceof List<?>){
     input=new HashMap<String, List<OutlineDef>>();
     for (Object o:(List<?>)newInput){
       OutlineDef od=(OutlineDef)o;
       List<OutlineDef> l=input.get( od.getParentID() );
       if(l==null){
         l=new ArrayList<OutlineDef>();
         input.put( od.getParentID(), l );
       }
       l.add( od );
     }
   } else {
     input=Collections.emptyMap();
   }

  }

}
