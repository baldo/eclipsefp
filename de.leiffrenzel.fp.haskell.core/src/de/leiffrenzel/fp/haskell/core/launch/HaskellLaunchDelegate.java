// Copyright (c) 2003-2005 by Leif Frenzel - see http://leiffrenzel.de
package de.leiffrenzel.fp.haskell.core.launch;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.sf.eclipsefp.common.core.util.Assert;

import org.eclipse.core.runtime.*;
import org.eclipse.debug.core.*;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.core.model.IProcess;



/** <p>Implements the launching functionality for Haskell launch 
  * configurations.</p>
  * 
  * @author Leif Frenzel
  */
public class HaskellLaunchDelegate implements ILaunchConfigurationDelegate {

  public void launch( final ILaunchConfiguration configuration,
                      final String mode,
                      final ILaunch launch,
                      final IProgressMonitor monitor ) throws CoreException {
    if( !monitor.isCanceled() ) {
      try {
        IPath loc = getLocation( configuration );
        checkCancellation( monitor );
        String[] arguments = determineArguments( configuration );
        checkCancellation( monitor );
        String[] cmdLine = createCmdLine( loc, arguments );
        checkCancellation( monitor );
        File workingDir = determineWorkingDir( configuration );
        checkCancellation( monitor );
        IProcess process = createProcess( launch, loc, cmdLine, workingDir );
        if( !isBackground( configuration ) ) {
          while( !process.isTerminated() ) {
            try {
              if( monitor.isCanceled() ) {
                process.terminate();
                break;
              }
              Thread.sleep( 50 );
            } catch( InterruptedException iex ) {
              // ignored
            }
          }
        }
      } catch( LaunchCancelledException lcex ) {
        // cancelled on user request
      }
    }
  }

  private IProcess createProcess( final ILaunch launch, 
                                  final IPath location, 
                                  final String[] cmdLine, 
                                  final File workingDir ) throws CoreException {
    Process proc = DebugPlugin.exec( cmdLine, workingDir );
    Map processAttrs = new HashMap();
    String programName = determineProgramName( location );
    processAttrs.put( IProcess.ATTR_PROCESS_TYPE, programName );
    IProcess process = null;
    if( proc != null ) {
      String loc = location.toOSString();
      process = DebugPlugin.newProcess( launch, proc, loc, processAttrs );
    }
    process.setAttribute( IProcess.ATTR_CMDLINE, 
                          CommandLineUtil.renderCommandLine( cmdLine ) );
    return process;
  }

  private String[] createCmdLine( final IPath location, 
                                  final String[] arguments ) {
    int cmdLineLength = 1;
    if( arguments != null ) {
      cmdLineLength += arguments.length;
    }
    String[] cmdLine = new String[ cmdLineLength ];
    cmdLine[ 0 ] = location.toOSString();
    if( arguments != null ) {
      System.arraycopy( arguments, 0, cmdLine, 1, arguments.length );
    }
    return cmdLine;
  }

  private File determineWorkingDir( final ILaunchConfiguration config ) 
                                                          throws CoreException {
    String name = ILaunchAttributes.WORKING_DIRECTORY;
    String attribute = config.getAttribute( name, ( String )null );
    File result = null;
    if( attribute != null ) {
      IPath workingDirectory = new Path( attribute );
      if( workingDirectory != null ) {
        result = workingDirectory.toFile();
      }
    }
    return result;
  }

  private String[] determineArguments( final ILaunchConfiguration config ) 
                                                          throws CoreException {
    String args = config.getAttribute( ILaunchAttributes.ARGUMENTS, 
                                       ( String )null );
    return CommandLineUtil.parse( args );
  }

  private void checkCancellation( final IProgressMonitor monitor ) {
    if( monitor.isCanceled() ) {
      throw new LaunchCancelledException();
    }
  }

  private String determineProgramName( final IPath location ) {
    String programName = location.lastSegment();
    String extension = location.getFileExtension();
    if( extension != null ) {
      int len = programName.length() - ( extension.length() + 1 );
      programName = programName.substring( 0, len );
    }
    return programName.toLowerCase();
  }
  
  
  // helping methods
  //////////////////
  
  public IPath getLocation( final ILaunchConfiguration config ) 
                                                          throws CoreException {
    String defaultValue = null;
    String location = config.getAttribute( ILaunchAttributes.EXECUTABLE,
                                           defaultValue );
    Assert.isNotNull( location );
    return new Path( location );
  }

  private boolean isBackground( final ILaunchConfiguration config ) 
                                                          throws CoreException {
    return config.getAttribute( ILaunchAttributes.RUN_IN_BACKGROUND, true );
  }

  private class LaunchCancelledException extends RuntimeException {
    private LaunchCancelledException() {
      super();
    }
  }
}