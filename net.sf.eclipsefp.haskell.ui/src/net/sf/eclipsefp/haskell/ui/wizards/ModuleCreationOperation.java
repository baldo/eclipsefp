// Copyright (c) 2003-2005 by Leif Frenzel - see http://leiffrenzel.de
package net.sf.eclipsefp.haskell.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import net.sf.eclipsefp.haskell.core.cabalmodel.CabalSyntax;
import net.sf.eclipsefp.haskell.core.cabalmodel.PackageDescription;
import net.sf.eclipsefp.haskell.core.cabalmodel.PackageDescriptionLoader;
import net.sf.eclipsefp.haskell.core.cabalmodel.PackageDescriptionStanza;
import net.sf.eclipsefp.haskell.core.cabalmodel.RealValuePosition;
import net.sf.eclipsefp.haskell.core.code.ModuleCreationInfo;
import net.sf.eclipsefp.haskell.core.code.SourceFileGenerator;
import net.sf.eclipsefp.haskell.scion.client.ScionInstance;
import net.sf.eclipsefp.haskell.ui.HaskellUIPlugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.texteditor.IDocumentProvider;


/** <p>the operation that creates a new module in a Haskell project.</p>
  *
  * @author Leif Frenzel
  */
public class ModuleCreationOperation implements IRunnableWithProgress {

  private final ModuleCreationInfo info;
  // the file that is finally generated in this operation
  private IFile generatedFile;

  public ModuleCreationOperation( final ModuleCreationInfo info ) {
    this.info = info;
  }

  IFile getGeneratedFile() {
    return generatedFile;
  }


  public void setGeneratedFile( final IFile generatedFile ) {
    this.generatedFile = generatedFile;
  }

  public ModuleCreationInfo getInfo() {
    return info;
  }

  // interface methods of IRunnableWithProgress
  /////////////////////////////////////////////

  public void run( final IProgressMonitor monitor )
                                              throws InvocationTargetException {
    Assert.isNotNull( info );
    try {
      boolean onlyAdd=false;
      if (generatedFile==null){
        generatedFile = new SourceFileGenerator().createFile( monitor, info );
        onlyAdd=true;
      }
      IProject p=generatedFile.getProject();
      IFile f=ScionInstance.getCabalFile( p );
      IDocumentProvider prov=new TextFileDocumentProvider();
      prov.connect( f );
      IDocument doc=prov.getDocument( f );

      String moduleName=info.getQualifiedModuleName();


      if (onlyAdd){
        for (PackageDescriptionStanza pds:info.getIncluded()){
          RealValuePosition vp=pds.addToPropertyList( CabalSyntax.FIELD_OTHER_MODULES, moduleName );
          vp.updateDocument( doc );
        }
        for (PackageDescriptionStanza pds:info.getExposed()){
          RealValuePosition vp=pds.addToPropertyList( CabalSyntax.FIELD_EXPOSED_MODULES, moduleName );
          vp.updateDocument( doc );
        }
      } else {
        Set<String> sIncluded=new HashSet<String>();
        for (PackageDescriptionStanza pds:info.getIncluded()){
          sIncluded.add( pds.toTypeName() );
        }
        Set<String> sExposed=new HashSet<String>();
        for (PackageDescriptionStanza pds:info.getExposed()){
          sExposed.add( pds.toTypeName() );
        }
        PackageDescription pd=PackageDescriptionLoader.load( f );
        for (PackageDescriptionStanza pds:pd.getStanzas()){
          if (pds.getType()!=null && (pds.getType().equals( CabalSyntax.SECTION_LIBRARY ) || pds.getType().equals( CabalSyntax.SECTION_EXECUTABLE ))){
            RealValuePosition vp=null;
            if(sIncluded.contains( pds.toTypeName() )){
              vp=pds.addToPropertyList( CabalSyntax.FIELD_OTHER_MODULES, moduleName );
            } else {
              vp=pds.removeFromPropertyList( CabalSyntax.FIELD_OTHER_MODULES, moduleName );
            }
            vp.updateDocument( doc );
            if (sExposed.contains( pds.toTypeName() )){
              vp=pds.addToPropertyList( CabalSyntax.FIELD_EXPOSED_MODULES, moduleName );
            } else {
              vp=pds.removeFromPropertyList( CabalSyntax.FIELD_EXPOSED_MODULES, moduleName );
            }
            vp.updateDocument( doc );
          }
        }
      }
      prov.saveDocument( monitor, f, doc, true );

      HaskellUIPlugin.getDefault().getScionInstanceManager( generatedFile ).buildProject( false );

    } catch( CoreException ex ) {
      throw new InvocationTargetException( ex );
    }
  }
}