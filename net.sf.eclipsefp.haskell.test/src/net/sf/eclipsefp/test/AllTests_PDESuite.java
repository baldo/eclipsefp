package net.sf.eclipsefp.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests_PDESuite {

	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTest(net.sf.eclipsefp.haskell.core.test.AllTests_PDESuite.suite());
		suite.addTest(net.sf.eclipsefp.haskell.debug.ui.test.AllTests_PDESuite.suite());
		suite.addTest(net.sf.eclipsefp.haskell.ghccompiler.test.AllTests_PDESuite.suite());
		suite.addTest(net.sf.eclipsefp.haskell.ui.test.AllTests_PDESuite.suite());
		return suite;
	}
	
}
