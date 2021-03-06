/**
 * (c) 2011, Alejandro Serrano
 * Released under the terms of the EPL.
 */
package net.sf.eclipsefp.haskell.browser.client;

import java.io.IOException;

import net.sf.eclipsefp.haskell.browser.BrowserServer;
import net.sf.eclipsefp.haskell.browser.DatabaseType;
import net.sf.eclipsefp.haskell.browser.items.Declaration;
import net.sf.eclipsefp.haskell.browser.items.HaskellPackage;
import net.sf.eclipsefp.haskell.browser.items.HoogleResult;
import net.sf.eclipsefp.haskell.browser.items.Module;
import net.sf.eclipsefp.haskell.browser.items.PackageIdentifier;
import net.sf.eclipsefp.haskell.browser.items.Packaged;

import org.json.JSONException;

/**
 * A virtual connection to a server which gives no responses. Used at
 * initialization time and when there is no scion-browser installed.
 * 
 * @author Alejandro Serrano
 */
public class NullBrowserServer extends BrowserServer {

	public NullBrowserServer() {
		// Do nothing
	}
	
	@Override
	public boolean isDatabaseLoaded() {
		return true;
	}
	
	@Override
	public boolean isHoogleLoaded() {
		return true;
	}

	@Override
	protected void loadLocalDatabaseInternal(String path, boolean rebuild) throws IOException, JSONException {
		// Do nothing
	}

	@Override
	public void setCurrentDatabase(DatabaseType current, PackageIdentifier id) throws IOException,
			JSONException {
		// Do nothing
	}

	@Override
	public HaskellPackage[] getPackages() throws IOException, JSONException {
		// Return nothing
		return new HaskellPackage[0];
	}

	@Override
	public Module[] getAllModules() throws IOException, JSONException {
		// Return nothing
		return new Module[0];
	}

	@Override
	public Module[] getModules(String module) throws IOException, JSONException {
		// Return nothing
		return new Module[0];
	}

	@SuppressWarnings("unchecked")
	@Override
	public Packaged<Declaration>[] getDeclarations(String module) throws Exception {
		// Return nothing
		return (Packaged<Declaration>[]) new Packaged[0];
	}
	
	@Override
	public Module[] findModulesForDeclaration(String decl) throws IOException, JSONException {
		return new Module[0];
	}

	@Override
	public HoogleResult[] queryHoogle(String query) throws Exception {
		// Return nothing
		return new HoogleResult[0];
	}
	
	@Override
	public void downloadHoogleData() {
		// Do nothing
	}
	
	@Override
	public boolean checkHoogle() {
		// Do nothing
		return true;
	}

	@Override
	public void stop() {
		// Do nothing
	}
}
