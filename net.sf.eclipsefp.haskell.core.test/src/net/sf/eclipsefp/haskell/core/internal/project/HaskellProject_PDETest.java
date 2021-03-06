// Copyright (c) 2003-2005 by Leif Frenzel - see http://leiffrenzel.de
package net.sf.eclipsefp.haskell.core.internal.project;

import net.sf.eclipsefp.haskell.core.project.HaskellProjectManager;
import net.sf.eclipsefp.haskell.core.test.TestCaseWithProject;
import net.sf.eclipsefp.haskell.core.util.ResourceUtil;
import net.sf.eclipsefp.haskell.util.FileUtil;
import org.eclipse.core.resources.IFolder;


public class HaskellProject_PDETest extends TestCaseWithProject {

  // interface methods of TestCase
  ////////////////////////////////

  @Override
  protected void tearDown() throws Exception {
    HaskellProjectManager.clear();
    super.tearDown();
  }


  // test cases
  /////////////

  public void testSingleSourcePath() {
    HaskellProject hp = ( HaskellProject )HaskellProjectManager.get( project );
    assertEquals( 1, hp.getSourcePaths().size() );
    assertEquals( project.getFolder( FileUtil.DEFAULT_FOLDER_SRC ), hp.getSourceFolder() );

    assertTrue( ResourceUtil.isSourceFolder( project.getFolder( FileUtil.DEFAULT_FOLDER_SRC ) ) );
    assertFalse( ResourceUtil.isSourceFolder( project.getFolder( "crs" ) ) );
    assertFalse( ResourceUtil.isSourceFolder( project.getFolder( "src/bla" ) ) );
  }

  public void testMultipleSourcePaths() {
    HaskellProject hp = ( HaskellProject )HaskellProjectManager.get( project );
    hp.addSourcePath( "test" );
    assertEquals( 2, hp.getSourcePaths().size() );
    // TODO lf we should really get two here
//    assertEquals( project.getFolder( FileUtil.DEFAULT_FOLDER_SRC ), hp.getSourceFolder() );

    assertTrue( ResourceUtil.isSourceFolder( project.getFolder( FileUtil.DEFAULT_FOLDER_SRC ) ) );
    IFolder testFolder = project.getFolder( "test" );
    assertTrue( ResourceUtil.isSourceFolder( testFolder ) );
    assertFalse( ResourceUtil.isSourceFolder( project.getFolder( "crs" ) ) );
  }

//  public void testTargetExecutable_single() throws CoreException {
//    HaskellProject hp = ( HaskellProject )HaskellProjectManager.get( project );
//    // atm one target exe is added automatically during project creation
//    assertEquals( 1, hp.getTargetNames().size() );
//
//    IPath path = new Path( "bla.exe" );
//    InputStream is = new ByteArrayInputStream( new byte[ 0 ] );
//    IFile f=project.getFile( path );
//    f.create( is, true, null );
//    try {
//      hp.addTargetName( path );
//      assertEquals( 2, hp.getTargetNames().size() );
//
//      assertTrue( ResourceUtil.isProjectExecutable( project, "bla" ));
//      assertFalse( ResourceUtil.isProjectExecutable( project, "blubb" ) );
//    } finally {
//      f.delete( true, null );
//    }
//  }
//
//  public void testTargetExecutable_multiple() throws CoreException {
//    HaskellProject hp = ( HaskellProject )HaskellProjectManager.get( project );
//    // atm one target exe is added automatically during project creation
//    assertEquals( 1, hp.getTargetNames().size() );
//
//    IPath path = new Path( "bin/bli.exe" );
//    IPath path2 = new Path( "bla.exe" );
//    InputStream is = new ByteArrayInputStream( new byte[ 0 ] );
//    project.getFolder( "bin" ).create( true, true, null );
//    project.getFile( path ).create( is, true, null );
//    IFile f=project.getFile( path2 );
//    f.create( is, true, null );
//    try {
//      hp.addTargetName( path );
//      hp.addTargetName( path2 );
//      assertEquals( 3, hp.getTargetNames().size() );
//
//      assertTrue( ResourceUtil.isProjectExecutable( project, "bla" ) );
//      assertTrue( ResourceUtil.isProjectExecutable( project, "bin/bli" ) );
//      assertFalse( ResourceUtil.isProjectExecutable( project, "blubb" ) );
//    } finally {
//      f.delete( true, null );
//    }
//  }
}
