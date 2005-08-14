// Copyright (c) 2003-2005 by Leif Frenzel - see http://leiffrenzel.de
package de.leiffrenzel.fp.haskell.ghccompiler.core.preferences;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import de.leiffrenzel.fp.haskell.ghccompiler.GhcCompilerPlugin;
import de.leiffrenzel.fp.haskell.ghccompiler.core.IGhcParameters;


/** <p>initializer for the GHC compiler preferences (declared in the 
  * <code>plugin.xml</code>).</p>
  * 
  * @author Leif Frenzel
  */
public class PreferenceInitializer extends AbstractPreferenceInitializer
                                   implements IGhcPreferenceNames,
                                              IGhcParameters {
 
  // interface methods of AbstractPreferenceInitializer
  /////////////////////////////////////////////////////
  
  public void initializeDefaultPreferences() {
    Preferences prefs = GhcCompilerPlugin.getDefault().getPluginPreferences();
    initializeDefaultValues( prefs );
  }

  
  // helping methods
  //////////////////
  
  private void initializeDefaultValues( final Preferences prefs ) {
    prefs.setDefault( EXECUTABLE_NAME, "ghc" );
    prefs.setDefault( EXTRA_OPTIONS, "" );
    prefs.setDefault( USE_EXTRA_OPTIONS, false );
    prefs.setDefault( OPTIMIZATION_LEVEL, -1 );
    prefs.setDefault( GHCI_USES_GHC_OPTIONS, false );
    initializeLanguageDefaults( prefs );
    initializeOptimizationDefaults( prefs );
    initializeMoreOptimizationDefaults( prefs );
  }

  private void initializeLanguageDefaults( final Preferences prefs ) {
    // boolean preferences use the parameter as key
    prefs.setDefault( LANG_GLASGOW_EXTS, false );
    prefs.setDefault( LANG_FI, false );
    prefs.setDefault( LANG_FFI, false );
    prefs.setDefault( LANG_WITH, false );
    prefs.setDefault( LANG_NO_MONOMORPHISM_RESTRICTION, false );
    prefs.setDefault( LANG_ALLOW_OVERLAPPING_INSTANCES, false );
    prefs.setDefault( LANG_ALLOW_UNDECIDABLE_INSTANCES, false );
    prefs.setDefault( LANG_ALLOW_INCOHERENT_INSTANCES, false );
    prefs.setDefault( LANG_GENERICS, false );
    prefs.setDefault( LANG_NO_IMPLICIT_PRELUDE, false );
  }

  private void initializeOptimizationDefaults( final Preferences prefs ) {
    // boolean preferences use the parameter as key    
    prefs.setDefault( OPT_EXCESS_PRECISION, false );
    prefs.setDefault( OPT_IGNORE_ASSERTS, false );
    prefs.setDefault( OPT_NO_STRICTNESS, false );
    prefs.setDefault( OPT_NO_CPR, false );
    prefs.setDefault( OPT_UNBOX_STRICT_FIELDS, false );    
  }

  private void initializeMoreOptimizationDefaults( final Preferences prefs ) {
    // boolean preferences use the parameter as key    
    prefs.setDefault( OPT_CASE_MERGE, false );
    prefs.setDefault( OPT_DICTS_STRICT, false );
    prefs.setDefault( OPT_DO_ETA_REDUCTION, false );
    prefs.setDefault( OPT_DO_LAMBDA_ETA_EXPANSION, false );
    prefs.setDefault( OPT_FOLDR_BUILD_ON, false );
    prefs.setDefault( OPT_IGNORE_INTERFACE_PRAGMAS, false );
    prefs.setDefault( OPT_LET_NO_ESCAPE, false );
    prefs.setDefault( OPT_OMIT_INTERFACE_PRAGMAS, false );
    prefs.setDefault( OPT_NO_CSE, false );
    prefs.setDefault( OPT_NO_PRE_INLINING, false );    
    prefs.setDefault( OPT_NUMBERS_STRICT, false );
    prefs.setDefault( OPT_USAGESP, false );
  }
}