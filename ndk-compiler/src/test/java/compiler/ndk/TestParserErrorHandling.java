package compiler.ndk;

import static org.junit.Assert.*;

import java.util.List;

import compiler.ndk.lexer.Lexer;
import compiler.ndk.lexer.TokenStream;
import compiler.ndk.parser.Parser;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import compiler.ndk.parser.Parser.SyntaxException;
import compiler.ndk.lexer.TokenStream.Kind;
import compiler.ndk.ast.visitor.ASTNode;
import static compiler.ndk.lexer.TokenStream.Kind.*;

public class TestParserErrorHandling {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	
	private void parseIncorrectInput(String input,
			Kind... expectedIncorrectTokenKind) throws SyntaxException {
		TokenStream stream = new TokenStream(input);
		Lexer lexer = new Lexer(stream);
		lexer.scan();
		Parser parser = new Parser(stream);
//		System.out.println(stream);
		ASTNode ast =parser.parse();
		assertNull(ast);
		List<SyntaxException> exceptions = parser.getExceptionList();
		for(SyntaxException e: exceptions){
			System.out.println(e.getMessage());
		}
			assertEquals(expectedIncorrectTokenKind.length, exceptions.size());
			for (int i = 0; i < exceptions.size(); ++i){
			assertEquals(expectedIncorrectTokenKind[i], exceptions.get(i).t.kind); // class is the incorrect token
		}
	}
	

	@Test
	public void import3() throws SyntaxException {
		System.out.println("***********\nimport3");
		String input = "import class A { } "; // this input is wrong.
		System.out.println(input);
		Kind ExpectedIncorrectTokenKind = KW_CLASS;
		parseIncorrectInput(input, ExpectedIncorrectTokenKind);
	}


	@Test
	public void def_simple_type2() throws SyntaxException {
		System.out.println("***********\ndef_simple_type2");
		String input = "class A {def B:int; def C:boolean; def S: string} ";
		System.out.println(input);
		parseIncorrectInput(input, RCURLY);
	}
	
	@Test
	public void multiple_errors1() throws SyntaxException {
		System.out.println("***********\nmultiple_errors1");
		String input = "class A {def B:int; def C:boolean; def S: strings;  def F: sing} ";
		System.out.println(input);
		parseIncorrectInput(input, IDENT, IDENT);
	}
	

	@Test
	public void multiple_errors2() throws SyntaxException {
		System.out.println("***********\nmultiple_errors2");
		String input = "class A {def C={->x=&true; z = false; w =& true;};} ";
		System.out.println(input);
		parseIncorrectInput(input, AND, AND );
	}

	
	@Test
	public void factor7() throws SyntaxException {
		System.out.println("***********\nfactor7");
		String input = "class A {def C={->x= &y; z = !y;};} ";
		System.out.println(input);
		parseIncorrectInput(input,AND);
	}
	

}
