/**
 * (c) 2011, Alejandro Serrano & JP Moresmau
 * Released under the terms of the EPL.
 */
package net.sf.eclipsefp.haskell.browser.views.hoogle;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import net.sf.eclipsefp.haskell.browser.BrowserEvent;
import net.sf.eclipsefp.haskell.browser.BrowserPlugin;
import net.sf.eclipsefp.haskell.browser.IHoogleLoadedListener;
import net.sf.eclipsefp.haskell.browser.items.DeclarationType;
import net.sf.eclipsefp.haskell.browser.items.HaskellPackage;
import net.sf.eclipsefp.haskell.browser.items.HoogleResult;
import net.sf.eclipsefp.haskell.browser.items.HoogleResultConstructor;
import net.sf.eclipsefp.haskell.browser.items.HoogleResultDeclaration;
import net.sf.eclipsefp.haskell.browser.items.HoogleResultModule;
import net.sf.eclipsefp.haskell.browser.items.HoogleResultPackage;
import net.sf.eclipsefp.haskell.browser.util.HtmlUtil;
import net.sf.eclipsefp.haskell.browser.views.NoDatabaseContentProvider;
import net.sf.eclipsefp.haskell.browser.views.NoDatabaseRoot;
import net.sf.eclipsefp.haskell.ui.HaskellUIPlugin;
import net.sf.eclipsefp.haskell.ui.internal.util.UITexts;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.part.ViewPart;

/**
 * View part for Hoogle search.
 * @author Alejandro Serrano & JP Moresmau
 *
 */
public class HoogleView extends ViewPart implements SelectionListener,
    ISelectionChangedListener, IDoubleClickListener, IHoogleLoadedListener {

  /**
   * search for the given text in Hoogle as if it was typed in the view
   * @param text the text to show in the view
   */
  public static void searchHoogle(final String text){
    // remove prefix if qualified
    int ix=text.lastIndexOf( '.' );
    final String txt=(ix>-1)
      ?text.substring( ix+1 )
      :text;

    Display.getDefault().asyncExec( new Runnable() {

      public void run() {
        try {
          IWorkbench w=PlatformUI.getWorkbench();
          IViewPart p=null;
          for (IWorkbenchWindow iww : w.getWorkbenchWindows()) {
            for (IWorkbenchPage page : iww.getPages()) {
              p=page.findView( ID );
              if (p!=null){
                iww.setActivePage( page );
                page.activate( p );
                break;
              }
            }
          }
          if (p==null){
            p=w.getActiveWorkbenchWindow().getActivePage().showView( ID );
            final HoogleView hv=(HoogleView)p;
            // will run after the notifications hoogleLoaded
            Display.getDefault().asyncExec( new Runnable() {

              public void run() {
                hv.text.setText( txt );
                Event evt=new Event();
                evt.widget=hv.text;
                hv.widgetDefaultSelected(new SelectionEvent( evt ));
              }
            });
            return;
              //PlatformUI.getWorkbench().getViewRegistry().find( ID ).createView();
          }

          final HoogleView hv=(HoogleView)p;

          hv.text.setText( txt );
          Event evt=new Event();
          evt.widget=hv.text;
          hv.widgetDefaultSelected(new SelectionEvent( evt ));


        } catch (CoreException ce){
          HaskellUIPlugin.log( ce );
        }
      }
    });
  }

  /**
   * The ID of the view as specified by the extension.
   */
  public static final String ID = "net.sf.eclipsefp.haskell.browser.views.hoogle.HoogleView";

  Text text;
  TreeViewer viewer;
  Browser doc;
  IContentProvider provider;

  @Override
  public void createPartControl( final Composite parent ) {
    GridLayout layout = new GridLayout();
    layout.numColumns = 1;
    layout.verticalSpacing = layout.horizontalSpacing = 0;
    layout.marginBottom = layout.marginHeight = layout.marginLeft = layout.marginRight = layout.marginTop = layout.marginWidth = 0;
    parent.setLayout( layout );

    text = new Text( parent, SWT.SINGLE | SWT.SEARCH | SWT.ICON_SEARCH
        | SWT.ICON_CANCEL );
    GridData textData = new GridData();
    textData.horizontalAlignment = SWT.FILL;
    textData.grabExcessHorizontalSpace = true;
    text.setLayoutData( textData );
    text.addSelectionListener( this );

    SashForm form = new SashForm( parent, SWT.VERTICAL );
    GridData formData = new GridData();
    formData.horizontalAlignment = SWT.FILL;
    formData.verticalAlignment = SWT.FILL;
    formData.grabExcessVerticalSpace = true;
    formData.grabExcessHorizontalSpace = true;
    form.setLayoutData( formData );
    viewer = new TreeViewer( form );
    viewer.setLabelProvider( new HoogleLabelProvider() );
    // Load if needed
    if (BrowserPlugin.getDefault().isHoogleLoaded()) {
      hoogleLoaded( null );
    } else {
      hoogleUnloaded( null );
    }

    doc = new Browser( form, SWT.NONE );
    form.setWeights( new int[] { 70, 30 } );
    // Hook for double clicking
    viewer.addDoubleClickListener( this );
    // Hook for changes in selection
    viewer.addPostSelectionChangedListener( this );
    // Wait the Hoogle database to be ready
    BrowserPlugin.getDefault().addHoogleLoadedListener( this );
  }

  public void hoogleLoaded( final BrowserEvent e ) {
    Display.getDefault().asyncExec( new Runnable() {

      public void run() {
        provider = new HoogleContentProvider();
        //viewer.setLabelProvider( new HoogleLabelProvider() );
        viewer.setContentProvider( provider );
        viewer.setInput( text.getText() );
        viewer.refresh();
      }
    } );
  }



  public void hoogleUnloaded( final BrowserEvent e ) {
    Display.getDefault().asyncExec( new Runnable() {

      public void run() {
        provider = new NoDatabaseContentProvider();
        //viewer.setLabelProvider( new NoDatabaseLabelProvider( true ) );
        viewer.setContentProvider( provider );
        viewer.setInput( NoDatabaseRoot.ROOT );
        viewer.refresh();
      }
    } );
  }


  @Override
  public void setFocus() {
    text.setFocus();
  }

  public void widgetSelected( final SelectionEvent e ) {
    // Do nothing
  }

  public void widgetDefaultSelected( final SelectionEvent e ) {
    if( e.detail == SWT.CANCEL ) {
      viewer.setInput( "" );
      viewer.refresh();
    } else if (provider != null) {
      viewer.setInput( text.getText() );
      viewer.refresh();
      if (provider instanceof HoogleContentProvider) {
        Object first = ((HoogleContentProvider)provider).getFirstElement();
        if( first != null ) {
          viewer.setSelection( new StructuredSelection( first ), true );
          viewer.getControl().setFocus();
        }
      }
    }
  }

  @SuppressWarnings ( "unchecked" )
  public void selectionChanged( final SelectionChangedEvent event ) {
    TreeSelection selection = ( TreeSelection )event.getSelection();

    Object o = selection.getFirstElement();
    if( o == null || o instanceof NoDatabaseRoot ) {
      doc.setText( "" );
      return;
    }

    // Try to find element to show
    HoogleResult result = null;
    if( o instanceof HoogleResult ) {
      result = ( HoogleResult )o;
    } else {
      Map.Entry<String, Object> entry = ( Map.Entry<String, Object> )o;
      if( entry.getValue() instanceof HoogleResult ) {
        result = ( HoogleResult )entry.getValue();
      }
    }

    if( result != null ) {
      String text = null;
      switch( result.getType() ) {
        case PACKAGE:
          HaskellPackage pkg = ( ( HoogleResultPackage )result ).getPackage();
          text = HtmlUtil.generateDocument( "package "
              + pkg.getIdentifier().toString(), pkg.getDoc() );
          break;
        case MODULE:
          HoogleResultModule mod = ( HoogleResultModule )result;
          text = HtmlUtil.generateDocument( "module " + mod.getName(), mod
              .getPackageIdentifiers(), null, false, mod.getModule().getDoc() );
          break;
        case DECLARATION:
          HoogleResultDeclaration decl = ( HoogleResultDeclaration )result;
          text = HtmlUtil.generateDocument( decl.getDeclaration()
              .getCompleteDefinition(), decl.getPackageIdentifiers(), decl
              .getModule(), false, decl.getDeclaration().getDoc() );
          break;
        case CONSTRUCTOR:
          HoogleResultConstructor con = ( HoogleResultConstructor )result;
          text = HtmlUtil.generateDocument( con.getConstructor()
              .getCompleteDefinition(), con.getPackageIdentifiers(), con
              .getModule(), false, con.getDeclaration().getDoc() );
          break;
      }

      doc.setText( text );
    } else {
      doc.setText( HtmlUtil
          .generateText( UITexts.browser_definedInSeveralLocations ) );
    }
  }

  @SuppressWarnings ( "unchecked" )
  public void doubleClick( final DoubleClickEvent event ) {
    TreeSelection selection = ( TreeSelection )event.getSelection();
    Object o = selection.getFirstElement();
    if( o == null || o instanceof NoDatabaseRoot ) {
      return;
    }

    // Try to find element to show
    HoogleResult result = null;
    if( o instanceof HoogleResult ) {
      result = ( HoogleResult )o;
    } else {
      Map.Entry<String, Object> entry = ( Map.Entry<String, Object> )o;
      if( entry.getValue() instanceof HoogleResult ) {
        result = ( HoogleResult )entry.getValue();
      } else {
        // Show the first one (better than nothing)
        result = ( ( ArrayList<HoogleResult> )entry.getValue() ).get( 0 );
      }
    }

    String url = null;
    switch( result.getType() ) {
      case PACKAGE:
        HoogleResultPackage pkg = ( HoogleResultPackage )result;
        url = HtmlUtil.generatePackageUrl( pkg.getPackage().getIdentifier() );
        break;
      case MODULE:
        HoogleResultModule mod = ( HoogleResultModule )result;
        url = HtmlUtil.generateModuleUrl( mod.getPackageIdentifiers().get( 0 ),
            mod.getName() );
        break;
      case CONSTRUCTOR:
        HoogleResultConstructor con = ( HoogleResultConstructor )result;
        url = HtmlUtil.generateElementUrl(
            con.getPackageIdentifiers().get( 0 ), con.getModule(), true,
            con.getName() );
        break;
      case DECLARATION:
        HoogleResultDeclaration decl = ( HoogleResultDeclaration )result;
        url = HtmlUtil.generateElementUrl(
            decl.getPackageIdentifiers().get( 0 ), decl.getModule(), decl
                .getDeclaration().getType() == DeclarationType.FUNCTION, decl
                .getName() );
        break;
    }

    // Open browser
    if( url != null ) {
      try {
        IWorkbenchBrowserSupport browserSupport = this.getSite()
            .getWorkbenchWindow().getWorkbench().getBrowserSupport();
        URL webUrl = new URL( url );
        IWebBrowser browser = browserSupport.createBrowser(
            IWorkbenchBrowserSupport.AS_EDITOR
                | IWorkbenchBrowserSupport.LOCATION_BAR, null,
            "Haskell Browser", "Haskell Browser" );
        browser.openURL( webUrl );
      } catch( Throwable ex ) {
        // Do nothing
      }
    }
  }
}
