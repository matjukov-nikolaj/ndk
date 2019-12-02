package compiler.ndk;

import static compiler.ndk.lexer.TokenStream.Kind.*;
import static compiler.ndk.lexer.TokenStream.Kind.IDENTIFIER;
import static compiler.ndk.lexer.TokenStream.Kind.LEFT_BRACE;
import static org.junit.Assert.*;

import compiler.ndk.lexer.Lexer;
import compiler.ndk.lexer.TokenStream;
import compiler.ndk.parser.Parser;
import org.junit.Test;
import compiler.ndk.parser.Parser.SyntaxException;
import compiler.ndk.ast.visitor.ASTNode;

import java.util.List;

public class TestParser {

	
	private ASTNode parseCorrectInput(String input) {
		TokenStream stream = new TokenStream(input);
		Lexer lexer = new Lexer(stream);
		lexer.scan();
		Parser parser = new Parser(stream);
		System.out.println();
		ASTNode ast = parser.parse();
		assertNotNull(ast);
		return ast;
	}

	private void parseIncorrectInput(String input, TokenStream.Kind... expectedIncorrectTokenKind) throws SyntaxException {
		TokenStream stream = new TokenStream(input);
		Lexer lexer = new Lexer(stream);
		lexer.scan();
		Parser parser = new Parser(stream);
		System.out.println();
		ASTNode ast = parser.parse();
		assertNull(ast);
		List<SyntaxException> exceptions = parser.getExceptionList();
		for(SyntaxException e: exceptions){
			System.out.println(e.getMessage());
			break;
		}
		assertEquals(expectedIncorrectTokenKind.length, exceptions.size());
		for (int i = 0; i < exceptions.size(); ++i){
			assertEquals(expectedIncorrectTokenKind[i], exceptions.get(i).t.kind);
		}
	}

	@Test
	public void emptyProgram() throws SyntaxException {
		System.out.println("emptyProgram");
		String input = "class A { } ";
		System.out.println(input);
		System.out.println(parseCorrectInput(input));
	}

	@Test
	public void typeDeclaration() throws SyntaxException {
		System.out.println("typeDeclaration");
		String input = "class A {var B:int; var C:boolean; var S: string;} ";
		System.out.println(input);
		System.out.println(parseCorrectInput(input));
	}
	
	@Test
	public void statements()throws SyntaxException {
		System.out.println("statements");
		String input = "class A {x = y; print a+b; print (x+y-z);} ";
		System.out.println(input);
		System.out.println(parseCorrectInput(input));
	}
	
	@Test
	public void ifStatement()throws SyntaxException {
		System.out.println("ifStatement");
		String input = "class A  {\n if (x) {}; } ";
		System.out.println(input);
		System.out.println(parseCorrectInput(input));
	} 
	
	@Test
	public void emptyStatement()throws SyntaxException {
		System.out.println("emptyStatement");
		String input = "class A  { ;;; } ";
		System.out.println(input);
		System.out.println(parseCorrectInput(input));
	}

	@Test
	public void equalsExpressions() throws SyntaxException {
		System.out.println("equalsExpressions");
		String input = "class A {var c: boolean; c = a<b; с = a>b; c = a<=b; с = a>=b;} ";
		System.out.println(input);
		System.out.println(parseCorrectInput(input));
	}

	@Test
	public void errorOrToken() throws SyntaxException {
		System.out.println("errorOrToken");
		String input = "class A {x=a||b;} ";
		System.out.println(input);
		parseIncorrectInput(input, OR);
	}

	@Test
	public void errorAndToken() throws SyntaxException {
		System.out.println("errorAndToken");
		String input = "class A {x=a&&b;} ";
		System.out.println(input);
		parseIncorrectInput(input, AND);
	}

	@Test
	public void equalsExpression() throws SyntaxException {
		System.out.println("equalsExpression");
		String input = "class A {x=a=b;} ";
		System.out.println(input);
		parseIncorrectInput(input, ASSIGN);
	}

	@Test
	public void expectedSemicolon() throws SyntaxException {
		System.out.println("expectedSemicolon");
		String input = "class A {var X:;} ";
		System.out.println(input);
		parseIncorrectInput(input, SEMICOLON);
	}

	@Test
	public void expectedRightBrace() throws SyntaxException {
		System.out.println("expectedRightBrace");
		String input = "class A  { if(  } ";
		System.out.println(input);
		parseIncorrectInput(input, RIGHT_BRACE);
	}

}
