package net.sf.eclipsefp.haskell.ui.internal.editors.haskell.codeassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.sf.eclipsefp.haskell.core.util.ResourceUtil;
import net.sf.eclipsefp.haskell.scion.client.ScionInstance;
import net.sf.eclipsefp.haskell.scion.client.ScionPlugin;
import net.sf.eclipsefp.haskell.ui.HaskellUIPlugin;
import net.sf.eclipsefp.haskell.ui.internal.util.UITexts;
import net.sf.eclipsefp.haskell.util.FileUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateVariable;
import org.eclipse.jface.text.templates.TemplateVariableResolver;

public class HsClassTypeNameTemplateVariable extends TemplateVariableResolver {
  /** The template variable name */
  private static final String NAME = "classTypeName";
  /** Associated Scion-server instance, if supplied. */
  private final ScionInstance scion;

  public HsClassTypeNameTemplateVariable() {
    super( NAME, UITexts.HaskellTemplateVariables_classTypeName_description );
    scion = null;
  }

  public HsClassTypeNameTemplateVariable(final String name, final String description) {
    super(name, description);
    scion = null;
  }

  public HsClassTypeNameTemplateVariable(final ScionInstance scion) {
    super( NAME, UITexts.HaskellTemplateVariables_classTypeName_description );
    this.scion = scion;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void resolve( final TemplateVariable variable, final TemplateContext context ) {
    if( context instanceof DocumentTemplateContext ) {
      DocumentTemplateContext docContext = ( DocumentTemplateContext )context;
      IDocument doc = docContext.getDocument();

      Assert.isNotNull( doc );

      IFile file = HaskellUIPlugin.getFile( doc );
      if( file != null ) {
        Assert.isTrue( FileUtil.hasHaskellExtension( file ) );
        Assert.isTrue( ResourceUtil.isInHaskellProject( file ) );

        final ScionInstance si = getScionInstance( file );

        Assert.isNotNull( si );

        Map<String, String> completions = si.completionsForClassTypeNames( file, doc );

        if (completions != null) {
          List<String> keys = new ArrayList<String>( completions.keySet() );

          Collections.sort(keys);
          variable.setValues( keys.toArray( new String[keys.size()] ) );
        } else {
          variable.setValue( new String() );
          variable.setResolved( false );
        }
      } else {
        variable.setValue( new String() );
      }
    } else {
      variable.setValue( new String() );
    }

    variable.setResolved( true );
  }

  /** scion-server instance accessor */
  private final ScionInstance getScionInstance(final IFile file) {
    return (scion == null ? ScionPlugin.getScionInstance( file ) : scion);
  }
}
