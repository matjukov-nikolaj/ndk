package compiler.ndk;

import static org.junit.Assert.*;

import compiler.ndk.lexer.Lexer;
import compiler.ndk.lexer.TokenStream;
import org.junit.Test;

import compiler.ndk.lexer.TokenStream.Kind;
import static compiler.ndk.lexer.TokenStream.Kind.*;

public class TestLexer {

	private void checkExpectedInput(String input, Kind[] expectedKinds, String[] expectedTexts) {
		System.out.println(input);
		TokenStream stream = new TokenStream(input);
		Lexer lexer = new Lexer(stream);
		lexer.scan();
		System.out.println(stream);
		assertArrayEquals(expectedKinds, makeKindArray(stream));
		assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	}

	@Test
	public void identifiers() {
		System.out.println("Identifier");
		String input = "A a $a _a 1a2";
		Kind[] expectedKinds = { IDENTIFIER, IDENTIFIER, IDENTIFIER, IDENTIFIER, INT_LIT, IDENTIFIER, EOF };
		String[] expectedTexts = { "A", "a", "$a", "_a", "1", "a2", "" };
		checkExpectedInput(input, expectedKinds, expectedTexts);
	}

	@Test
	public void assignTokenKind() {
		System.out.println("assignTokenKind");
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
	public void minusTokenKind() {
		System.out.println("minusTokenKind");
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
	public void lessTokenKind() {
		System.out.println("lessTokenKind");
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
		String input = "\"abc";
		Kind[] expectedKinds = { UNTERMINATED_STRING, EOF };
		String[] expectedTexts = { "\"abc", "" };
		checkExpectedInput(input, expectedKinds, expectedTexts);
	}

	@Test
	public void unterminatedComment() {
		System.out.println("unterminatedComment");
		String input = "/*";
		Kind[] expectedKinds = { UNTERMINATED_COMMENT, EOF };
		String[] expectedTexts = { "/*", "" };
		checkExpectedInput(input, expectedKinds, expectedTexts);
	}

	@Test
	public void nestedComment() {
		System.out.println("nestedComment");
		String input = "/*/**/*/ ";
		Kind[] expectedKinds = { MUL, DIV, EOF };
		String[] expectedTexts = { "*", "/", "" };
		checkExpectedInput(input, expectedKinds, expectedTexts);
	}

	@Test
	public void nestedCommentAndString() {
		System.out.println("nestedCommentAndString");
		String input = "/*\" \"*/  ";
		Kind[] expectedKinds = { EOF };
		String[] expectedTexts = { "" };
		checkExpectedInput(input, expectedKinds, expectedTexts);
	}

	@Test
	public void nestedCommentAndString2() {
		System.out.println("nestedCommentAndString2");
		String input = "\"/**/\"";
		Kind[] expectedKinds = { STRING_LIT, EOF };
		String[] expectedTexts = { "/**/", "" };
		checkExpectedInput(input, expectedKinds, expectedTexts);
	}

	@Test
	public void nestedCommentAndString3() {
		System.out.println("nestedCommentAndString3");
		String input = "\"/*\"*/";
		Kind[] expectedKinds = { STRING_LIT, MUL, DIV, EOF };
		String[] expectedTexts = { "/*", "*", "/", "" };
		checkExpectedInput(input, expectedKinds, expectedTexts);
	}

	@Test
	public void separators() {
		System.out.println("separators");
		String input = "; , : ? ( ) { } [ ]";
		Kind[] expectedKinds = { SEMICOLON, COMMA, COLON, ILLEGAL_CHAR, LEFT_BRACKET, RIGHT_BRACKET, LEFT_BRACE, RIGHT_BRACE, ILLEGAL_CHAR, ILLEGAL_CHAR, EOF };
		String[] expectedTexts = { ";", ",", ":", "?", "(", ")", "{", "}", "[", "]", "" };
		checkExpectedInput(input, expectedKinds, expectedTexts);
	}

	@Test
	public void otherOperators() {
		System.out.println("otherOperators");
		String input = "+ - * / !";
		Kind[] expectedKinds = { PLUS, MINUS, MUL, DIV, NOT, EOF };
		String[] expectedTexts = { "+", "-", "*", "/", "!", "" };
		checkExpectedInput(input, expectedKinds, expectedTexts);
	}

	@Test
	public void boolOperators() {
		System.out.println("otherOperators");
		String input = "| &";
		Kind[] expectedKinds = { OR, AND, EOF };
		String[] expectedTexts = { "|", "&", "" };
		checkExpectedInput(input, expectedKinds, expectedTexts);
	}

	@Test
	public void equalsOperators() {
		System.out.println("equalsOperators");
		String input = "= == < > <= >= !=";
		Kind[] expectedKinds = { ASSIGN, EQUAL, LESS_THAN, GREATER_THAN, LESS_EQUAL, GREATER_EQUAL, NOTEQUAL, EOF };
		String[] expectedTexts = { "=", "==", "<", ">", "<=", ">=", "!=", "" };
		checkExpectedInput(input, expectedKinds, expectedTexts);
	}

	@Test
	public void onlyQuotationMarks() {   //the grammar doesn't support nested comment
		System.out.println("onlyQuotationMarks");
		String input = "\"\"";
		Kind[] expectedKinds = { STRING_LIT, EOF };
		String[] expectedTexts = { "", "" };
		checkExpectedInput(input, expectedKinds, expectedTexts);
	}

	@Test
	public void stringLiterals() {
		System.out.println("stringLiterals");
		String input = " \"abc\" \"var\" \"hello\" \"123\" \"&^%$\" ";
		Kind[] expectedKinds = { STRING_LIT, STRING_LIT, STRING_LIT,
				STRING_LIT, STRING_LIT, EOF };
		String[] expectedTexts = { "abc", "var", "hello", "123", "&^%$", "" };
		checkExpectedInput(input, expectedKinds, expectedTexts);
	}

	@Test
	public void keywords() {
		System.out.println("keywords");
		String input = " int  string  boolean class var  while if  else print aaa";
		Kind[] expectedKinds = { KEY_WORD_INT, KEY_WORD_STRING, KEY_WORD_BOOLEAN,
				KEY_WORD_CLASS, KEY_WORD_VAR, KEY_WORD_WHILE, KEY_WORD_IF, KEY_WORD_ELSE,
				KEY_WORD_PRINT, IDENTIFIER, EOF };
		String[] expectedTexts = { "int", "string", "boolean",
				"class", "var", "while", "if", "else", "print", "aaa", "" };
		checkExpectedInput(input, expectedKinds, expectedTexts);
	}

	@Test
	public void boolAndNullLiterals() {
		System.out.println("boolAndNullLiterals");
		String input = " true false\n null";
		Kind[] expectedKinds = { BL_TRUE, BL_FALSE, NL_NULL, EOF };
		String[] expectedTexts = { "true", "false", "null", "" };
		checkExpectedInput(input, expectedKinds, expectedTexts);
	}

	@Test
	public void multiLineString() {
		System.out.println("multiLineString");
		String input = " \"true false\n null\" ";
		Kind[] expectedKinds = { STRING_LIT, EOF };
		String[] expectedTexts = { "true false\n null", "" };
		checkExpectedInput(input, expectedKinds, expectedTexts);
	}

	@Test
	public void escapeCharacters() {
		System.out.println("escapeCharacters");
		String input = "\\n \n \\r \r";
		Kind[] expectedKinds = { ILLEGAL_CHAR, IDENTIFIER, ILLEGAL_CHAR, IDENTIFIER, EOF };
		String[] expectedTexts = { "\\", "n", "\\", "r", "" };
		checkExpectedInput(input, expectedKinds, expectedTexts);
	}

	@Test
	public void intliteralWithZero() {
		System.out.println("intliteralWithZero");
		String input = "01 20 540 0032";
		Kind[] expectedKinds = { INT_LIT, INT_LIT, INT_LIT, INT_LIT, INT_LIT, INT_LIT, INT_LIT, EOF };
		String[] expectedTexts = { "0", "1", "20", "540", "0", "0", "32", "" };
		checkExpectedInput(input, expectedKinds, expectedTexts);
	}

	Kind[] makeKindArray(TokenStream stream) {
		Kind[] kinds = new Kind[stream.tokens.size()];
		for (int i = 0; i < stream.tokens.size(); ++i) {
			kinds[i] = stream.tokens.get(i).kind;
		}
		return kinds;

	}

	String[] makeTokenTextArray(TokenStream stream) {
		String[] kinds = new String[stream.tokens.size()];
		for (int i = 0; i < stream.tokens.size(); ++i) {
			kinds[i] = stream.tokens.get(i).getText();
		}
		return kinds;
	}

}