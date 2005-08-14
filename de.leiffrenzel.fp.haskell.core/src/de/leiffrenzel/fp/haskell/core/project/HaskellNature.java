// Copyright (c) 2003-2005 by Leif Frenzel - see http://leiffrenzel.de
package de.leiffrenzel.fp.haskell.core.project;

import net.sf.eclipsefp.common.core.project.FPNature;
import de.leiffrenzel.fp.haskell.core.builder.HaskellBuilder;

/** <p>The project nature that indicates Haskell projects.</p>
 * 
 * @author Leif Frenzel
 */
public class HaskellNature extends FPNature {

  public static final String NATURE_ID = HaskellNature.class.getName();

  protected String getBuilderID() {
    return HaskellBuilder.BUILDER_ID;
  }
}