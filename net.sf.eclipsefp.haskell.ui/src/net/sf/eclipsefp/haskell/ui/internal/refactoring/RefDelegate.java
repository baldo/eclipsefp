// Copyright (c) 2007-2008 by Leif Frenzel - see http://leiffrenzel.de
// This code is made available under the terms of the Eclipse Public License,
// version 1.0 (EPL). See http://www.eclipse.org/legal/epl-v10.html
package net.sf.eclipsefp.haskell.ui.internal.refactoring;

import net.sf.eclipsefp.haskell.ui.internal.util.UITexts;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;

/** <p>super class for refactoring delegates, encapsulates some common
  * functionality.</p>
  *
  * @author Leif Frenzel
  */
abstract class RefDelegate {

  protected final RefInfo info;

  RefDelegate( final RefInfo info ) {
    this.info = info;
  }

  RefactoringStatus checkInitialConditions() {
    RefactoringStatus result = new RefactoringStatus();
    IFile sourceFile = info.getSourceFile();
    if( sourceFile == null || !sourceFile.exists() ) {
      result.addFatalError( UITexts.refDelegate_noSourceFile );
    } else if( info.getSourceFile().isReadOnly() ) {
      result.addFatalError( UITexts.refDelegate_roFile );
    } else if( !info.isAllowEmptySelection() && isEmpty( info.getText() ) ) {
      result.addFatalError( UITexts.refDelegate_noSelection );
    }
    return result;
  }

  abstract RefactoringStatus checkFinalConditions(
    final IProgressMonitor pm,
    final CheckConditionsContext ctxt );

  abstract void createChange( final IProgressMonitor pm, final CompositeChange rootChange );


  // helping methods
  //////////////////

  private boolean isEmpty( final String candidate ) {
    return candidate == null || candidate.trim().length() == 0;
  }
}
