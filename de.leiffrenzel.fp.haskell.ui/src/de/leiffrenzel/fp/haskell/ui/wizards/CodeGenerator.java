// Copyright (c) 2003-2005 by Leif Frenzel - see http://leiffrenzel.de
package de.leiffrenzel.fp.haskell.ui.wizards;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;

import de.leiffrenzel.fp.haskell.core.util.ResourceUtil;

/** <p>helper to generate the code in the new module.</p>
  * 
  * @author Leif Frenzel
  */
class CodeGenerator {

  /** Creates the new type using the specified information values. */
  public static IFile createFile( final IProgressMonitor monitor,
                                  final ModuleCreationInfo info ) 
                                                          throws CoreException {
    monitor.beginTask( "Creating module...", 12 );
    IContainer destFolder = createFolders( info, monitor );   // (6)
    IFile result = createFile( info, destFolder, monitor );   // (4)
    refresh( info, monitor );                                 // (2)
    monitor.done();
    return result;
  }

  
  // helping methods
  //////////////////

  private static IContainer createFolders( final ModuleCreationInfo info, 
                                           final IProgressMonitor monitor ) 
                                                          throws CoreException {
    IPath foldersPath = info.getFolders();
    IContainer sourceContainer = info.getSourceContainer();
    IContainer result = null;
    if( foldersPath != null && foldersPath.segmentCount() > 0 ) {
      String[] segments = foldersPath.segments();
      IContainer folder = sourceContainer;
      for( int i = 0; i < segments.length; i++ ) {
        IPath path = new Path( segments[ i ] );
        folder = folder.getFolder( path );
        if( !folder.exists() && folder instanceof IFolder ) {
          SubProgressMonitor subMon = new SubProgressMonitor( monitor, 1 );
          ( ( IFolder )folder ).create( false, true, subMon );
        }
      }
      result = folder;
    } else {
      result = sourceContainer;
    }
    return result;
  }

  private static void refresh( final ModuleCreationInfo info, 
                               final IProgressMonitor monitor ) 
                                                          throws CoreException {
    SubProgressMonitor refMon = new SubProgressMonitor( monitor, 2 );
    IContainer srcContainer = info.getSourceContainer();
    srcContainer.refreshLocal( IResource.DEPTH_INFINITE, refMon );
  }

  private static IFile createFile( final ModuleCreationInfo info, 
                                   final IContainer destFolder, 
                                   final IProgressMonitor monitor ) 
                                                          throws CoreException {
    String[] segments = getPathSegments( info );
    String fileContent = createFileContent( segments, info.getModuleName() );
    String fileName = createFileName( info.getModuleName() );
    IFile result = destFolder.getFile( new Path( fileName ) );
    InputStream isContent = new ByteArrayInputStream( fileContent.getBytes() ); 
    SubProgressMonitor subMon = new SubProgressMonitor( monitor, 4 );
    result.create( isContent, true, subMon );
    return result;
  }

  private static String[] getPathSegments( final ModuleCreationInfo info ) {
    IPath path = info.getFolders();
    return ( path == null ) ? new String[ 0 ] : path.segments();
  }


  private static String createFileName( final String moduleName ) {
    return moduleName + "." + ResourceUtil.EXTENSION_HS;
   }


  private static String createFileContent( final String[] folderNames, 
                                           final String name ) {
    StringBuffer sb = new StringBuffer();
    sb.append( getLineDelimiter() );
    sb.append( "module " ); 
    for( int i = 0; i < folderNames.length; i++ ) {
      sb.append( folderNames[ i ] );
      sb.append( "." );
    }
    sb.append( name );
    sb.append( " where" );
    sb.append( getLineDelimiter() );
    return sb.toString();
  }
  
  private static String getLineDelimiter() {
    return System.getProperty( "line.separator", "\n" );    
  }
}