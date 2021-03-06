// Copyright (c) 2003-2005 by Leif Frenzel - see http://leiffrenzel.de
package net.sf.eclipsefp.haskell.ui.internal.preferences;


/** <p>contains constants for preference names in the Haskell UI.</p>
  *
  * @author Leif Frenzel
  */
public interface IPreferenceConstants {

  // code assist
  public final static String AUTO_ACTIVATION_DELAY  = "AUTO_ACTIVATION_DELAY"; //$NON-NLS-1$
  public final static String AUTO_INSERT            = "AUTO_INSERT"; //$NON-NLS-1$
  public final static String ENABLE_AUTO_ACTIVATION = "ENABLE_AUTO_ACTIVATION";  //$NON-NLS-1$
  public final static String INSERT_COMPLETION      = "INSERT_COMPLETION";  //$NON-NLS-1$
  public final static String ORDER_PROPOSALS        = "ORDER_PROPOSALS";  //$NON-NLS-1$
  public final static String SHOW_VISIBLE_PROPOSALS = "SHOW_VISIBLE_PROPOSALS";  //$NON-NLS-1$

  // Build console
  public final static String CLEAR_BUILD_CONSOLE    = "CLEAR_BUILD_CONSOLE";  //$NON-NLS-1$

  // Console high and low water marks
  public final static String HASKELL_CONSOLE_LOW_WATER_MARK = "haskellConsoleLowWaterMark"; //$NON-NLS-1$
  public final static String HASKELL_CONSOLE_HIGH_WATER_MARK = "haskellConsoleHighWaterMark"; //$NON-NLS-1$

  // Scion
  public final static String SCION_SERVER_FLAVOR  = "scionServerFlavor"; //$NON-NLS-1$
  public final static String SCION_SERVER_BUILTIN = "SCION_SERVER_BUILTIN" ; //$NON-NLS-1$
  public final static String SCION_SERVER_EXECUTABLE = "SCION_SERVER_EXECUTABLE";  //$NON-NLS-1$
  public final static String RUN_CABAL_UPDATE = "RUN_CABAL_UPDATE" ; //$NON-NLS-1$

  // search paths
  public final static String HADDOCK_SEARCH_PATHS = "HADDOCK_SEARCH_PATHS";  //$NON-NLS-1$

  // Browser
  public final static String SCION_BROWSER_SERVER_BUILTIN = "SCION_BROWSER_SERVER_BUILTIN" ; //$NON-NLS-1$
  public final static String SCION_BROWSER_SERVER_EXECUTABLE = "SCION_BROWSER_SERVER_EXECUTABLE";  //$NON-NLS-1$
}