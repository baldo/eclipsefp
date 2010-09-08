// Copyright (c) 2004-2005 by Leif Frenzel
// See http://leiffrenzel.de
package net.sf.eclipsefp.haskell.ui.internal.preferences.scion;

import java.util.ArrayList;
import java.util.List;
import net.sf.eclipsefp.haskell.core.cabal.CabalImplementation;
import net.sf.eclipsefp.haskell.ui.internal.util.UITexts;
import net.sf.eclipsefp.haskell.ui.util.SWTUtil;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;


/** <p>TODO</p>
 *
 * @author Leif Frenzel
 */
public class CabalImplsBlock implements ISelectionProvider {
  private CheckboxTableViewer viewer;
  private Table table;
  private Button btnAdd;
  private Button btnRemove;
  private Button btnEdit;
  private final List<CabalImplementation> impls = new ArrayList<CabalImplementation>();
  private final ListenerList selectionListeners = new ListenerList();
  private ISelection lastSelection = new StructuredSelection();

  Composite createControl( final Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    Font parentFont = parent.getFont();

    GridLayout glayout = new GridLayout(2, false);
    glayout.marginHeight = 0;
    glayout.marginWidth = 0;
    composite.setLayout( glayout );
    composite.setFont( parentFont );

    Label tableLabel = new Label( composite, SWT.NONE );
    tableLabel.setText( UITexts.cabalImplsBlock_installed );
    GridData gdata = new GridData( SWT.FILL, SWT.TOP, true, false );
    gdata.horizontalSpan = 2;
    tableLabel.setLayoutData( gdata );
    tableLabel.setFont( parentFont );

    int style = SWT.CHECK | SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION;
    table = new Table( composite, style );

    gdata = new GridData( SWT.FILL, SWT.TOP, true, true );
    gdata.widthHint = 450;
    table.setLayoutData( gdata );
    table.setFont( parentFont );
    table.setHeaderVisible( true );
    table.setLinesVisible( true );

    createColumns();
    createViewer();

    Composite buttonsComp = new Composite( composite, SWT.NONE );

    glayout = new GridLayout(1, true);
    glayout.marginHeight = 0;
    glayout.marginWidth = 0;
    buttonsComp.setLayout( glayout );
    buttonsComp.setLayoutData( new GridData( SWT.CENTER, SWT.TOP, false, false ) );
    buttonsComp.setFont( parentFont );
    createButtons( buttonsComp );

    return composite;
  }

  public void addSelectionChangedListener( final ISelectionChangedListener listener ) {
    selectionListeners.add( listener );
  }

  public ISelection getSelection() {
    return new StructuredSelection( viewer.getCheckedElements() );
  }

  public void removeSelectionChangedListener( final ISelectionChangedListener listener ) {
    selectionListeners.remove( listener );
  }

  public void setSelection( final ISelection selection ) {
    if( selection instanceof IStructuredSelection ) {
      if( !selection.equals( lastSelection ) ) {
        lastSelection = selection;
        Object elem = ( ( IStructuredSelection )selection ).getFirstElement();
        if( elem == null ) {
          viewer.setCheckedElements( new Object[ 0 ] );
        } else {
          viewer.setCheckedElements( new Object[] { elem } );
          viewer.reveal( elem );
        }
        fireSelectionChanged();
      }
    }
  }

  // Helper methods:
  private void createColumns() {
    createColumn( UITexts.cabalImplsBlock_colName, new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent evt ) {
        // sortByName();
      }
    } );
    createColumn (UITexts.cabalImplsBlock_colVersion, new SelectionAdapter() {
      @Override
      public void widgetSelected ( final SelectionEvent evt ) {
        // Insert something here.
      }
    } );
  }

  private TableColumn createColumn( final String text,
      final SelectionListener listener ) {
    TableColumn result = new TableColumn( table, SWT.NULL );
    result.setText( text );
    result.addSelectionListener( listener );
    return result;
  }

  private void createButtons( final Composite buttonsComp ) {
    String sAdd = UITexts.implementationsBlock_btnAdd;
    btnAdd = SWTUtil.createPushButton( buttonsComp, sAdd );
    btnAdd.addListener( SWT.Selection, new Listener() {
      public void handleEvent( final Event evt ) {
        // TODO: Add new cabal implementation
      }
    } );

    String sEdit = UITexts.implementationsBlock_btnEdit;
    btnEdit = SWTUtil.createPushButton( buttonsComp, sEdit );
    btnEdit.addListener( SWT.Selection, new Listener() {
      public void handleEvent( final Event evt ) {
        // TODO: Edit existing cabal implementation
      }
    } );

    String sRemove = UITexts.implementationsBlock_btnRemove;
    btnRemove = SWTUtil.createPushButton( buttonsComp, sRemove );
    btnRemove.addListener( SWT.Selection, new Listener() {
      public void handleEvent( final Event evt ) {
        // TODO: remove selected cabal implementation
      }
    } );
  }

  private void createViewer() {
    viewer = new CheckboxTableViewer( table );
    viewer.setLabelProvider( new CabalImplsLP() );
    viewer.setContentProvider( new CabalImplsCP( impls ) );
    // by default, sort by name
    // sortByName();

    viewer.addSelectionChangedListener( new ISelectionChangedListener() {
      public void selectionChanged( final SelectionChangedEvent evt ) {
        enableButtons();
      }
    } );

    viewer.addCheckStateListener( new ICheckStateListener() {
      public void checkStateChanged( final CheckStateChangedEvent event ) {
        if( event.getChecked() ) {
          CabalImplementation element = ( CabalImplementation )event.getElement();
          // TODO: setCheckedHsImplementation( element.getName() );
        } else {
          // TODO: setCheckedHsImplementation( null );
        }
      }
    } );

    viewer.addDoubleClickListener( new IDoubleClickListener() {
      public void doubleClick( final DoubleClickEvent e ) {
        if( !viewer.getSelection().isEmpty() ) {
          // TODO: editHsImplementation();
        }
      }
    } );

  }

  private void fireSelectionChanged() {
    SelectionChangedEvent evt = new SelectionChangedEvent( this, getSelection() );
    Object[] lis = selectionListeners.getListeners();
    for( int i = 0; i < lis.length; i++ ) {
      ISelectionChangedListener li = ( ISelectionChangedListener )lis[ i ];
      li.selectionChanged( evt );
    }
  }

  private void enableButtons() {
    IStructuredSelection ssel = (IStructuredSelection) viewer.getSelection();
    btnEdit.setEnabled( ssel.size() == 1 );
    boolean moreThanSelectedItem = ssel.size() > 0 && ssel.size() < viewer.getTable().getItemCount();
    btnRemove.setEnabled( moreThanSelectedItem );
  }
}
