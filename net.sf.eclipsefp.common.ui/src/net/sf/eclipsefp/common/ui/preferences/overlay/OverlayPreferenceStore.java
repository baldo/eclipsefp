// Copyright (c) 2003-2004 by Leif Frenzel - see http://leiffrenzel.de
package net.sf.eclipsefp.common.ui.preferences.overlay;

import java.util.*;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

/** <p>A preference store that can be used for caching preference values
  * (e.g. on tabbed pages).</p>
  *
  * @author Leif Frenzel  
  */
public class OverlayPreferenceStore implements IPreferenceStore {

  /** The parent preference store. */
  private IPreferenceStore parent;
  /** The underlying preference store. */
  private IPreferenceStore store;
  /** The keys of this store. */
  private List overlayKeys;
  private IPropertyChangeListener propertyListener;


  public OverlayPreferenceStore( final IPreferenceStore parent ) {
    this.parent = parent;
    overlayKeys = new ArrayList();
    store = new PreferenceStore();
  }

  public void addKey( final OverlayType type, final String key ) {
    this.overlayKeys.add( new OverlayKey( type, key ) );
  }

  public void addBooleanKey( final String key ) {
    addKey( OverlayType.BOOLEAN, key );
  }

  public void addDoubleKey( final String key ) {
    addKey( OverlayType.DOUBLE, key );
  }

  public void addFloatKey( final String key ) {
    addKey( OverlayType.FLOAT, key );
  }

  public void addIntKey( final String key ) {
    addKey( OverlayType.INT, key );
  }

  public void addLongKey( final String key ) {
    addKey( OverlayType.LONG, key );
  }

  public void addStringKey( final String key ) {
    addKey( OverlayType.STRING, key );
  }

  /**
   * Propagates all overlay keys from this store to the parent store.
   */
  public void propagate() {
    for( Iterator i = overlayKeys.iterator(); i.hasNext(); ) {
      propagateProperty( store, ( OverlayKey )i.next(), parent );
    }
  }

  public void load() {
    Iterator iter = overlayKeys.iterator();
    while( iter.hasNext() ) {
      OverlayKey key = ( OverlayKey )iter.next();
      Loader.loadProperty( parent, key, store, true );
    }
  }

  public void loadDefaults() {
    Iterator iter = overlayKeys.iterator();
    while( iter.hasNext() ) {
      OverlayKey key = ( OverlayKey )iter.next();
      setToDefault( key.getKey() );
    }
  }

  /**
   * Starts to listen for changes.
   */
  public void startListening() {
    if( propertyListener == null ) {
      propertyListener = new IPropertyChangeListener() {
        public void propertyChange( final PropertyChangeEvent event ) {
          OverlayKey key = findOverlayKey( event.getProperty() );
          if( key != null ) {
            propagateProperty( parent, key, store );
          }
        }
      };
      parent.addPropertyChangeListener( propertyListener );
    }
  }

  public void stopListening() {
    if( propertyListener != null ) {
      parent.removePropertyChangeListener( propertyListener );
      propertyListener = null;
    }
  }

  // IPreferenceStore Implementation -----------------------------------------

  public void addPropertyChangeListener( final IPropertyChangeListener li ) {
    store.addPropertyChangeListener( li );
  }

  public void removePropertyChangeListener( final IPropertyChangeListener li ) {
    store.removePropertyChangeListener( li );
  }

  public void firePropertyChangeEvent( final String name, 
                                       final Object oldValue,
                                       final Object newValue ) {
    store.firePropertyChangeEvent( name, oldValue, newValue );
  }

  public boolean contains( final String name ) {
    return store.contains( name );
  }

  public boolean getBoolean( final String name ) {
    return store.getBoolean( name );
  }

  public boolean getDefaultBoolean( final String name ) {
    return store.getDefaultBoolean( name );
  }

  public double getDefaultDouble( final String name ) {
    return store.getDefaultDouble( name );
  }

  public float getDefaultFloat( final String name ) {
    return store.getDefaultFloat( name );
  }

  public int getDefaultInt( final String name ) {
    return store.getDefaultInt( name );
  }

  public long getDefaultLong( final String name ) {
    return store.getDefaultLong( name );
  }

  public String getDefaultString( final String name ) {
    return store.getDefaultString( name );
  }

  public double getDouble( final String name ) {
    return store.getDouble( name );
  }

  public float getFloat( final String name ) {
    return store.getFloat( name );
  }

  public int getInt( final String name ) {
    return store.getInt( name );
  }

  public long getLong( final String name ) {
    return store.getLong( name );
  }

  public String getString( final String name ) {
    return store.getString( name );
  }

  public boolean isDefault( final String name ) {
    return store.isDefault( name );
  }

  public boolean needsSaving() {
    return store.needsSaving();
  }

  public void putValue( final String name, final String value ) {
    if( covers( name ) ) {
      store.putValue( name, value );
    }
  }

  public void setDefault( final String name, final double value ) {
    if( covers( name ) ) {
      store.setDefault( name, value );
    }
  }

  public void setDefault( final String name, final float value ) {
    if( covers( name ) ) {
      store.setDefault( name, value );
    }
  }

  public void setDefault( final String name, final int value ) {
    if( covers( name ) ) {
      store.setDefault( name, value );
    }
  }

  public void setDefault( final String name, final long value ) {
    if( covers( name ) ) {
      store.setDefault( name, value );
    }
  }

  public void setDefault( final String name, final String value ) {
    if( covers( name ) ) {
      store.setDefault( name, value );
    }
  }

  public void setDefault( final String name, final boolean value ) {
    if( covers( name ) ) {
      store.setDefault( name, value );
    }
  }

  public void setToDefault( final String name ) {
    store.setToDefault( name );
  }

  public void setValue( final String name, final double value ) {
    if( covers( name ) ) {
      store.setValue( name, value );
    }
  }

  public void setValue( final String name, final float value ) {
    if( covers( name ) ) {
      store.setValue( name, value );
    }
  }

  public void setValue( final String name, final int value ) {
    if( covers( name ) ) {
      store.setValue( name, value );
    }
  }

  public void setValue( final String name, final long value ) {
    if( covers( name ) ) {
      store.setValue( name, value );
    }
  }

  public void setValue( final String name, final String value ) {
    if( covers( name ) ) {
      store.setValue( name, value );
    }
  }

  public void setValue( final String name, final boolean value ) {
    if( covers( name ) ) {
      store.setValue( name, value );
    }
  }

  /**
   * Tries to find and return the overlay key for the given preference key
   * string.
   * 
   * @param key
   *          the preference key string
   * @return the overlay key or <code>null</code> if none can be found
   */
  protected OverlayKey findOverlayKey( final String key ) {
    OverlayKey result = null;
    Iterator iter = overlayKeys.iterator();
    while( iter.hasNext() ) {
      OverlayKey overlayKey = ( OverlayKey )iter.next();
      if( overlayKey.getKey().equals( key ) ) {
        result = overlayKey;
      }
    }
    return result;
  }

  protected final void propagateProperty( final IPreferenceStore origin,
                                          final OverlayKey key, 
                                          final IPreferenceStore target ) {
    if( origin.isDefault( key.getKey() ) ) {
      if( !target.isDefault( key.getKey() ) ) {
        target.setToDefault( key.getKey() );
      }
    } else {
      Propagator.propagate( origin, key, target );
    }
  }

  private boolean covers( final String key ) {
    return ( findOverlayKey( key ) != null );
  }
}