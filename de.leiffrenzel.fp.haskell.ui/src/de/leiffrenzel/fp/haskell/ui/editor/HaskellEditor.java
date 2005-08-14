// Copyright (c) 2003-2005 by Leif Frenzel - see http://leiffrenzel.de
package de.leiffrenzel.fp.haskell.ui.editor;

import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.*;
import org.eclipse.jface.text.*;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.*;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import de.leiffrenzel.fp.haskell.core.halamo.*;
import de.leiffrenzel.fp.haskell.ui.HaskellUIPlugin;
import de.leiffrenzel.fp.haskell.ui.editor.text.HaskellCharacterPairMatcher;
import de.leiffrenzel.fp.haskell.ui.preferences.editor.IEditorPreferenceNames;
import de.leiffrenzel.fp.haskell.ui.views.outline.HaskellOutlinePage;

/** <p>The main editor class for the Haskell editor.</p>
  * 
  * @author Leif Frenzel
  */
public class HaskellEditor extends TextEditor 
                           implements IEditorPreferenceNames {

  
  /** <p>the id under which the Haskell editor is declared.</p> */
  public static final String ID = HaskellEditor.class.getName();
  
  private HaskellOutlinePage outlinePage;
  private ProjectionSupport projectionSupport;

  private ICompilationUnit model;
  
  public void reveal( final IHaskellLanguageElement element ) {
    Assert.isNotNull( element );
    IDocument doc = getSourceViewer().getDocument();
    ISourceLocation srcLoc = element.getSourceLocation();
    int offset = -1;
    try {
      offset = doc.getLineOffset( srcLoc.getLine() ) + srcLoc.getColumn();
    } catch( final BadLocationException badlox ) {
      // ignore
    }
    int length = element.getName().length();
    getSourceViewer().revealRange( offset, length );
  }

  
  // interface methods of TextEditor
  //////////////////////////////////
  
  protected void initializeEditor() {
    super.initializeEditor();
    setSourceViewerConfiguration( new HaskellConfiguration( this ) );
    setEditorContextMenuId( "#HaskellEditorContext" );
    // we configure the preferences ourselves
    setPreferenceStore( HaskellUIPlugin.getDefault().getPreferenceStore() );
  }

  
  protected boolean affectsTextPresentation( final PropertyChangeEvent evt ) {
    String prop = evt.getProperty();
    return super.affectsTextPresentation( evt ) || isAffectingProperty( prop );
  }
  
  protected void configureSourceViewerDecorationSupport( 
      final SourceViewerDecorationSupport support ) {
    super.configureSourceViewerDecorationSupport( support );
    support.setCharacterPairMatcher( new HaskellCharacterPairMatcher() );
    String bracketsKey = EDITOR_MATCHING_BRACKETS;
    String colorKey = EDITOR_MATCHING_BRACKETS_COLOR;
    support.setMatchingCharacterPainterPreferenceKeys( bracketsKey, colorKey );
    support.setSymbolicFontName( getFontPropertyPreferenceKey() );
  }
  
  public void editorContextMenuAboutToShow( final IMenuManager menu ) {
    super.editorContextMenuAboutToShow( menu );
    if (isEditable()) {
      addAction( menu, "Comment" );
      addAction( menu, "Uncomment" );
    }
    menu.add( new Separator( "net.sf.eclipsefp.haskell.ui.refactoring.menu" ) );
  }
  
  protected void createActions() {
    super.createActions();

    // content assist
    String defId = ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS;
    createTextOpAction( "ContentAssistProposal", 
                        ISourceViewer.CONTENTASSIST_PROPOSALS, 
                        defId );

    // comment/uncomment
    createTextOpAction( "Comment", 
                        ITextOperationTarget.PREFIX, 
                        IActionDefinitionIds.COMMENT );
    createTextOpAction( "Uncomment", 
                        ITextOperationTarget.STRIP_PREFIX, 
                        IActionDefinitionIds.UNCOMMENT );
  }
  
  protected ISourceViewer createSourceViewer( final Composite parent, 
                                              final IVerticalRuler ruler, 
                                              final int styles ) {
    // copied this from the super class, replaced source viewer with
    // projection viewer
    fAnnotationAccess = createAnnotationAccess();
    fOverviewRuler = createOverviewRuler( getSharedColors() );
    ISourceViewer viewer = new ProjectionViewer( parent, 
                                                 ruler, 
                                                 getOverviewRuler(), 
                                                 isOverviewRulerVisible(), 
                                                 styles);
    // ensure decoration support has been created and configured.
    getSourceViewerDecorationSupport(viewer);
    return viewer;
  }
  
  public void createPartControl( final Composite parent ) {
    super.createPartControl( parent );
    ProjectionViewer projectionViewer = ( ProjectionViewer )getSourceViewer();
    projectionSupport = new ProjectionSupport( projectionViewer,
                                               getAnnotationAccess(), 
                                               getSharedColors() );
    projectionSupport.install();
    projectionViewer.doOperation( ProjectionViewer.TOGGLE );
  }

  /** <p>if we are asked for a ContentOutlinePage, we show what we have.</p> */ 
  public Object getAdapter( final Class required ) {
    Object result = null;
    // adapt the displayed source file to the outline viewer
    if( IContentOutlinePage.class.equals( required ) ) {
      if( outlinePage == null ) {
        outlinePage = new HaskellOutlinePage( this );
        if( getEditorInput() != null ) {
          outlinePage.setInput( getEditorInput() );
        }
      }
      result = outlinePage;
    } else if ( projectionSupport != null ) {
      result = projectionSupport.getAdapter( getSourceViewer(), required );
    }
    
    if( result == null ) {
      result = super.getAdapter( required );
    }
    return result;
  }

  // supplement some TextEditor funtionality with specific handling
  // needed because we have an attached outline page
  /////////////////////////////////////////////////////////////////
  
  public void dispose() {
    if( outlinePage != null ) {
      outlinePage.setInput( null );
    }
    super.dispose();
  }
  
  public void doRevertToSaved() {
    super.doRevertToSaved();
    if( outlinePage != null ) {
      outlinePage.update();
    }
  }
  
  public void doSave( final IProgressMonitor monitor ) {
    super.doSave( monitor );
    if( outlinePage != null ) {
      outlinePage.update();
    }
  }
  
  public void doSaveAs() {
    super.doSaveAs();
    if( outlinePage != null ) {
      outlinePage.update();
    }
  }
  
  public void doSetInput( final IEditorInput input ) throws CoreException {
    super.doSetInput( input );
    if( outlinePage != null ) {
      outlinePage.setInput( input );
    }
  }

  
  // attribute setters and getters
  ////////////////////////////////
  
  public ICompilationUnit getModel() {
    return model;
  }

  public void setModel( final ICompilationUnit model ) {
    this.model = model;
  }  

  
  // helping methods
  //////////////////

  private boolean isAffectingProperty( final String property ) {
    return    property.equals( EDITOR_COMMENT_COLOR )
           || property.equals( EDITOR_COMMENT_BOLD )
           || property.equals( EDITOR_LITERATE_COMMENT_COLOR )
           || property.equals( EDITOR_LITERATE_COMMENT_BOLD )           
           || property.equals( EDITOR_DEFAULT_COLOR )
           || property.equals( EDITOR_DEFAULT_BOLD )
           || property.equals( EDITOR_FUNCTION_COLOR )
           || property.equals( EDITOR_FUNCTION_BOLD )
           || property.equals( EDITOR_KEYWORD_COLOR )
           || property.equals( EDITOR_KEYWORD_BOLD )
           || property.equals( EDITOR_STRING_COLOR )
           || property.equals( EDITOR_STRING_BOLD );
  }

  private void createTextOpAction( final String name, 
                                   final int targetId,
                                   final String actionDefinitionId ) {
    ResourceBundle bundle = HaskellUIPlugin.getDefault().getResourceBundle();
    Action action = new TextOperationAction( bundle, 
                                             name + ".", 
                                             this, 
                                             targetId );
    action.setActionDefinitionId( actionDefinitionId );
    setAction( name, action );
    markAsStateDependentAction( name, true );
  }
}