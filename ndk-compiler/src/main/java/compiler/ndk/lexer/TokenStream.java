package compiler.ndk.lexer;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

public class TokenStream {
	char[] inputChars;
	public final ArrayList<Token> tokens = new ArrayList<Token>();

	public TokenStream(char[] inputChars) {
		this.inputChars = inputChars;
	}

	public TokenStream(Reader r) {
		this.inputChars = getChars(r);
	}

	public TokenStream(String inputString) {
		int length = inputString.length();
		inputChars = new char[length];
		inputString.getChars(0, length, inputChars, 0);
	}

	private char[] getChars(Reader r) {
		StringBuilder sb = new StringBuilder();
		try {
			int ch = r.read();
			while (ch != -1) {
				sb.append((char) ch);
				ch = r.read();
			}
		} catch (IOException e) {
			throw new RuntimeException("IOException");
		}
		char[] chars = new char[sb.length()];
		sb.getChars(0, sb.length(), chars, 0);
		return chars;
	}

	private int pos = 0;

	public Token nextToken() {
		return tokens.get(pos++);
	}

	public void reset() {
		pos = 0;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Token t : tokens) {
			sb.append(t.toString());
			sb.append('\n');
		}
		return sb.toString();
	}


	public static enum Kind {
		IDENTIFIER,
		/* reserved words */
		KEY_WORD_INT, KEY_WORD_STRING, KEY_WORD_BOOLEAN, KEY_WORD_CLASS, KEY_WORD_VAR,
		KEY_WORD_WHILE, KEY_WORD_IF, KEY_WORD_ELSE, KEY_WORD_PRINT, KEY_WORD_SIZE,
		/* boolean literals */
		BL_TRUE, BL_FALSE,
		/* null literal */
		NL_NULL,
		/* separators */
		SEMICOLON, // ;
		COMMA, // ,
		LEFT_BRACKET, // (
		RIGHT_BRACKET, // )
		LEFT_SQUARE, // [
		RIGHT_SQUARE, // ]
		LEFT_BRACE, // {
		RIGHT_BRACE, // }
		COLON, // :
		ASSIGN, // =
		BAR, // |
		AND, // &
		EQUAL, // ==
		NOTEQUAL, // !=
		LESS_THAN, // <
		GREATER_THAN, // >
		LESS_EQUAL, // <=
		GREATER_EQUAL, // >=
		PLUS, // +
		MINUS, // -
		MUL, // *
		DIV, // /
		NOT, // !
		LSHIFT, // <<
		RSHIFT, // >>
		INT_LIT, STRING_LIT,
		/* end of file */
		EOF,
		/* error tokens */
		ILLEGAL_CHAR,         //a character that cannot appear in that context
		UNTERMINATED_STRING,  //end of input is reached before the closing "
		UNTERMINATED_COMMENT  //end of input is reached before the closing */
	}

	public class Token {
		public final Kind kind;
		public final int beg;
		public final int end;
		public final int lineNumber;

		public Token(Kind kind, int beg, int end, int lineNumber) {
			this.kind = kind;
			this.beg = beg;
			this.end = end;
			this.lineNumber = lineNumber;
		}

		public int getIntVal() {
			assert kind == Kind.INT_LIT : "attempted to get value of non-number token";
			return Integer.valueOf(getText());
		}

		public boolean getBooleanVal() {
			assert (kind == Kind.BL_TRUE || kind == Kind.BL_FALSE) : "attempted to get boolean value of non-boolean token";
			return kind == Kind.BL_TRUE;
		}

		public int getLineNumber() {
			return lineNumber;
		}

		public String getText() {
			if (inputChars.length < end) {
				assert kind == Kind.EOF && beg == inputChars.length;
				return "";
			}
			if (kind == Kind.STRING_LIT) {
				StringBuilder sb = new StringBuilder();
				for (int i = beg+1; i < end-1; ++i) {
					char ch = inputChars[i];
					if (ch == '\\') {
						char nextChar = inputChars[i+1];
						if (nextChar == '"') {
							sb.append('"');
							i++;
						} else if (nextChar == 'n')  {
							sb.append('\n');
							i++;
						} else if (nextChar == 'r') {
							sb.append('\r');
							i++;
						} else if (nextChar == '\\') {
								sb.append('\\');
								i++;
						}
					} else {
						sb.append(ch);
					}	
				}
				return sb.toString();
			}
			return String.valueOf(inputChars, beg, end - beg);
		}

		public String toString() {
			return (new StringBuilder("<").append(kind).append(",")
					.append(getText()).append(",").append(beg).append(",")
					.append(end).append(",").append(lineNumber).append(">"))
					.toString();
		}

		public boolean equals(Object o) {
			if (!(o instanceof Token))
				return false;
			Token other = (Token) o;
			return kind == other.kind && beg == other.beg && end == other.end
					&& lineNumber == other.lineNumber;
		}
	}
}
