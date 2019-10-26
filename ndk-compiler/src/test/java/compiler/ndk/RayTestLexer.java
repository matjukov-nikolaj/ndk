package compiler.ndk;

import static org.junit.Assert.*;

import compiler.ndk.lexer.Lexer;
import compiler.ndk.lexer.TokenStream;
import org.junit.Test;

import compiler.ndk.lexer.TokenStream.Kind;
import compiler.ndk.lexer.TokenStream.Token;
import static compiler.ndk.lexer.TokenStream.Kind.*;

public class RayTestLexer {

	/*
	 * The following are
	 * Rui Zhang's test cases
	 */
	@Test
	public void IdentifierStart() {
		System.out.println("IdentifierStart");
		String input = "Abc abc $abc _abc 12abc34";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Lexer lexer = new Lexer(stream);
		lexer.scan();
		System.out.println(stream);
		Kind[] expectedKinds = { IDENTIFIER, IDENTIFIER, IDENTIFIER, IDENTIFIER, INT_LIT, IDENTIFIER, EOF };
		String[] expectedTexts = { "Abc", "abc", "$abc", "_abc", "12", "abc34", "" }; // need empty string for eof
		assertArrayEquals(expectedKinds, makeKindArray(stream));
		assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	}
	
	@Test
	public void firstPartAtEndOfInput2() {
		System.out.println("firstPartATEndOfInput2");
		String input = "=";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Lexer lexer = new Lexer(stream);
		lexer.scan();
		System.out.println(stream);
		assertEquals(ASSIGN, stream.nextToken().kind);
		assertEquals(EOF, stream.nextToken().kind);
	}
	
	@Test
	public void firstPartAtEndOfInput3() {
		System.out.println("firstPartATEndOfInput3");
		String input = "-";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Lexer lexer = new Lexer(stream);
		lexer.scan();
		System.out.println(stream);
		assertEquals(MINUS, stream.nextToken().kind);
		assertEquals(EOF, stream.nextToken().kind);
	}
	
	@Test
	public void firstPartAtEndOfInput4() {
		System.out.println("firstPartATEndOfInput4");
		String input = ".";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Lexer lexer = new Lexer(stream);
		lexer.scan();
		System.out.println(stream);
		//assertEquals(DOT, stream.nextToken().kind);
		assertEquals(EOF, stream.nextToken().kind);
	}
	
	@Test
	public void firstPartAtEndOfInput5() {
		System.out.println("firstPartATEndOfInput5");
		String input = "<";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Lexer lexer = new Lexer(stream);
		lexer.scan();
		System.out.println(stream);
		assertEquals(LESS_THAN, stream.nextToken().kind);
		assertEquals(EOF, stream.nextToken().kind);
	}
	
	@Test
	public void unterminatedString() {
		System.out.println("unterminatedString");
		String input = "abc \"abc";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Lexer lexer = new Lexer(stream);
		lexer.scan();
		System.out.println(stream);
		Kind[] expectedKinds = { IDENTIFIER, UNTERMINATED_STRING, EOF };
		String[] expectedTexts = { "abc", "\"abc", "" }; // need empty string for eof
		assertArrayEquals(expectedKinds, makeKindArray(stream));
		assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	}
	
	@Test
	public void unterminatedComment() {   //the grammar doesn't support nested comment
		System.out.println("unterminatedComment");
		String input = "/**";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Lexer lexer = new Lexer(stream);
		lexer.scan();
		System.out.println(stream);
		Kind[] expectedKinds = { UNTERMINATED_COMMENT, EOF };
		String[] expectedTexts = { "/**", "" }; // need empty string for eof
		assertArrayEquals(expectedKinds, makeKindArray(stream));
		assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	}
	
	@Test
	public void nestedComment() {   //the grammar doesn't support nested comment
		System.out.println("nestedComment");
		String input = "/*/**/*/ ";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Lexer lexer = new Lexer(stream);
		lexer.scan();
		System.out.println(stream);
		Token t = stream.nextToken();
		Kind[] expectedKinds = { MUL, DIV, EOF };
		String[] expectedTexts = { "*", "/", "" }; // need empty string for eof
		assertEquals(6, t.beg);
		assertArrayEquals(expectedKinds, makeKindArray(stream));
		assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	}
	
	@Test
	public void nestedCommentAndString() {   //the grammar doesn't support nested comment
		System.out.println("nestedCommentAndString");
		String input = "/*\" \"*/  ";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Lexer lexer = new Lexer(stream);
		lexer.scan();
		System.out.println(stream);
		Kind[] expectedKinds = { EOF };
		String[] expectedTexts = { "" }; // need empty string for eof
		assertArrayEquals(expectedKinds, makeKindArray(stream));
		assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	}
	
	@Test
	public void nestedCommentAndString2() {   //the grammar doesn't support nested comment
		System.out.println("nestedCommentAndString2");
		String input = "\"/**/\"";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Lexer lexer = new Lexer(stream);
		lexer.scan();
		System.out.println(stream);
		Kind[] expectedKinds = { STRING_LIT, EOF };
		String[] expectedTexts = { "/**/", "" }; // need empty string for eof
		assertArrayEquals(expectedKinds, makeKindArray(stream));
		assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	}
	
	@Test
	public void nestedCommentAndString3() {   //the grammar doesn't support nested comment
		System.out.println("nestedCommentAndString3");
		String input = "\"/*\"*/";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Lexer lexer = new Lexer(stream);
		lexer.scan();
		System.out.println(stream);
		Kind[] expectedKinds = { STRING_LIT, MUL, DIV, EOF };
		String[] expectedTexts = { "/*", "*", "/", "" }; // need empty string for eof
		assertArrayEquals(expectedKinds, makeKindArray(stream));
		assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	}
		
	@Test
	public void seperators() {   //the grammar doesn't support nested comment
		System.out.println("seperators");
		String input = "; , : ? ( ) { } [ ]";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Lexer lexer = new Lexer(stream);
		lexer.scan();
		System.out.println(stream);
		Kind[] expectedKinds = { SEMICOLON, COMMA, COLON, LEFT_BRACKET, RIGHT_BRACKET, LEFT_BRACE, RIGHT_BRACE, LEFT_SQUARE, RIGHT_SQUARE, EOF };
		String[] expectedTexts = { ";", ",", ":", "?", "(", ")", "{", "}", "[", "]", "" }; // need empty string for eof
		assertArrayEquals(expectedKinds, makeKindArray(stream));
		assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	}	
	
	@Test
	public void otherOperators() {   //the grammar doesn't support nested comment
		System.out.println("otherOperators");
		String input = "| & + - * /";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Lexer lexer = new Lexer(stream);
		lexer.scan();
		System.out.println(stream);
		Kind[] expectedKinds = { BAR, AND, PLUS, MINUS, MUL, DIV, EOF };
		String[] expectedTexts = { "|", "&", "+", "-", "*", "/", "" }; // need empty string for eof
		assertArrayEquals(expectedKinds, makeKindArray(stream));
		assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	}
	
	@Test
	public void onlyQuotationMarks() {   //the grammar doesn't support nested comment
		System.out.println("onlyQuotationMarks");
		String input = "\"\"";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Lexer lexer = new Lexer(stream);
		lexer.scan();
		System.out.println(stream);
		Token t = stream.tokens.get(1);
		Kind[] expectedKinds = { STRING_LIT, EOF };
		String[] expectedTexts = { "", "" }; // need empty string for eof
		assertEquals(2, t.beg); //string includes the quotation marks as part of the token
		assertArrayEquals(expectedKinds, makeKindArray(stream));
		assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	}
		
	@Test
	public void escapeCharacters() {
		System.out.println("escapeCharacters");
		String input = "\\n \n \\r \r";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Lexer lexer = new Lexer(stream);
		lexer.scan();
		System.out.println(stream);
		Token t = stream.tokens.get(3);
		Kind[] expectedKinds = { ILLEGAL_CHAR, IDENTIFIER, ILLEGAL_CHAR, IDENTIFIER, EOF };
		String[] expectedTexts = { "\\", "n", "\\", "r", "" }; // need empty string for eof
		assertEquals(2, t.lineNumber);
		assertArrayEquals(expectedKinds, makeKindArray(stream));
		assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	}
	
	@Test
	public void intliteralWithZero() {
		System.out.println("intliteralWithZero");
		String input = "0123 1020 5400 00031";
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Lexer lexer = new Lexer(stream);
		lexer.scan();
		System.out.println(stream);
		Kind[] expectedKinds = { INT_LIT, INT_LIT, INT_LIT, INT_LIT, INT_LIT, INT_LIT, INT_LIT, INT_LIT, EOF };
		String[] expectedTexts = { "0", "123", "1020", "5400", "0", "0", "0", "31", "" }; // need empty string for eof
		assertArrayEquals(expectedKinds, makeKindArray(stream));
		assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	}
	
	
	
	// Creates an array containing the kinds of the tokens in the token list
	Kind[] makeKindArray(TokenStream stream) {
		Kind[] kinds = new Kind[stream.tokens.size()];
		for (int i = 0; i < stream.tokens.size(); ++i) {
			kinds[i] = stream.tokens.get(i).kind;
		}
		return kinds;
		
	}

	// Creates an array containing the texts of the tokens in the token list
	String[] makeTokenTextArray(TokenStream stream) {
		String[] kinds = new String[stream.tokens.size()];
		for (int i = 0; i < stream.tokens.size(); ++i) {
			kinds[i] = stream.tokens.get(i).getText();
		}
		return kinds;
	}

	
}