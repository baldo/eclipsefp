package net.sf.eclipsefp.haskell.core.test.codeassist;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import net.sf.eclipsefp.haskell.core.codeassist.CompletionEngine;
import net.sf.eclipsefp.haskell.core.parser.test.util.Parser_PDETestCase;
import net.sf.eclipsefp.haskell.core.test.util.CompletionProposalTestCase;
import de.leiffrenzel.fp.haskell.core.halamo.ICompilationUnit;

public class CompletionEngine_PDETest extends Parser_PDETestCase {
	
	public void testDeclarationCompletion() throws CoreException {
		final String input = "module CompletionEngineTest where\n" +
				             "\n" +
				             "putStr str = str\n" +
				             "\n" +
				             "main = pu\n";
		final ICompilationUnit unit = parse(input);
		final CompletionEngine engine = new CompletionEngine();
		
		assertEquals('u', input.charAt(62 - 1));
		
		ICompletionProposal[] proposals = engine.complete(unit, 62);
		
		assertContains(createProposal("pu", "putStr", 62), proposals);
	}
	
	public void testPreludeClassCompletion() throws CoreException {
		final String input = "module CompletionEngineTest where\n" +
                             "\n" +
                             "fat :: N";
		final ICompilationUnit unit = parse(input);
		final CompletionEngine engine = new CompletionEngine();

		assertEquals('N', input.charAt(43 - 1));

		ICompletionProposal[] proposals = engine.complete(unit, 43);

		assertContains(createProposal("N", "Num", 43), proposals);
	}

	public void testKeywordCompletion() throws CoreException {
		final String input = "module CompletionEngineTest wh";
		//TODO avoid complaining about parsing error here
		final ICompilationUnit unit = parse(input);
		final CompletionEngine engine = new CompletionEngine();

		assertEquals('h', input.charAt(30 - 1));

		ICompletionProposal[] proposals = engine.complete(unit, 30);

		assertContains(createProposal("wh", "where", 30), proposals);
	}
	
	//TODO test if the proposals really start with the preffix
	
	public void testDiscoverPreffixAfterLeftParen() throws CoreException {
		final String input = "module Factorial where\n" +
				             "\n" +
				             "fat 0 = 1\n" +
				             "fat 1 = n * (f";
		final ICompilationUnit unit = parse(input);
		final CompletionEngine engine = new CompletionEngine();
		
		assertEquals('f', input.charAt(48 - 1));

		ICompletionProposal[] proposals = engine.complete(unit, 48);

		assertContains(createProposal("f", "fat", 48), proposals);
	}
	
	//TODO do not complete on empty preffix
	
	//TODO test preffix with underscore
	
	private void assertContains(ICompletionProposal proposal, ICompletionProposal[] proposals) {
		CompletionProposalTestCase.assertContains(proposal, proposals);
	}

	private ICompletionProposal createProposal(String replaced, String replacement, int offset) {
		return CompletionProposalTestCase.createProposal(replaced, replacement, offset);
	}
	
}
