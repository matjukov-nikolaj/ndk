package compiler.ndk.lexer;

import compiler.ndk.lexer.TokenStream.Kind;
import compiler.ndk.lexer.TokenStream.Token;
import static compiler.ndk.lexer.TokenStream.Kind.*;

import java.util.HashMap;

public class Lexer {
	
	private enum State {
		START, 
		GOT_EQUALS, 
		GOT_DOT,
		GOT_EXCLAM,
		GOT_LANGLE,
		GOT_RANGLE,
		GOT_HYPHEN,
		
		GOT_SLASH, 				//got '/'
		GOT_SLASHSTAR,			//got '/*'
		GOT_SLASH2STAR,			//got '/*......*'
		GOT_BSLASHR,			//got '\r'
		
		IDENT_PART, 
		INT_PART, 
		STRING_PART,
		EOF
	}
	//the current state of the DFA 
	private State state;
	
	//the index of (next) char to process during scanning, or if none, past the end of the array
	private int index = 0; 
	
	//the char to process during scanning
	private char ch;

	//the current line number
	private int line = 1;	
	
    //local references to TokenStream objects for convenience
      //set in constructor
	final TokenStream stream;

	//the keywords hash map
	final HashMap<String, Kind> reservedWord = new HashMap<String, Kind>();			
    
	public void initReservedWord(){
		this.reservedWord.put("int", Kind.KEY_WORD_INT);
		this.reservedWord.put("string", Kind.KEY_WORD_STRING);
		this.reservedWord.put("boolean", Kind.KEY_WORD_BOOLEAN);
		this.reservedWord.put("class", Kind.KEY_WORD_CLASS);
		this.reservedWord.put("var", Kind.KEY_WORD_VAR);
		this.reservedWord.put("while", Kind.KEY_WORD_WHILE);
		this.reservedWord.put("if", Kind.KEY_WORD_IF);
		this.reservedWord.put("else", Kind.KEY_WORD_ELSE);
		this.reservedWord.put("print", Kind.KEY_WORD_PRINT);
		this.reservedWord.put("true", Kind.BL_TRUE);
		this.reservedWord.put("false", Kind.BL_FALSE);
		this.reservedWord.put("null", Kind.NL_NULL);
	}
	

	
	public Lexer(TokenStream stream) {
		//initialize input stream
		this.stream = stream;
		//initialize reserved words Hashmap
		this.initReservedWord();
	}


	
	private char getch(){
		
		if(index < stream.inputChars.length){
			return stream.inputChars[index++];
		}else{//deal with EOF
			index++;
			return (char)-1;
		}
		
		
	}

	// Fills in the stream.tokens list with recognized tokens from the input
	public void scan() {
		Token t = null; 
		do{
		//System.out.println("perform next()");
			t = next();
			stream.tokens.add(t);		
		}while(!t.kind.equals(EOF));	
	}
	
	private Token next(){
		state = State.START;
		Token t = null;
		int begOffset = 0;		

		do{
			ch = getch();
			switch(state){
			case START:
				begOffset = index - 1;
				switch(ch){
				case '.':
					state = State.GOT_DOT;
					break;
				case ';':
					t= stream.new Token(SEMICOLON, begOffset, index, line);
					break;
				case ',':
					t= stream.new Token(COMMA, begOffset, index, line);
					break;
				case '(':
					t= stream.new Token(LEFT_BRACKET, begOffset, index, line);
					break;
				case ')':
					t= stream.new Token(RIGHT_BRACKET, begOffset, index, line);
					break;
				case '[':
					t= stream.new Token(LEFT_SQUARE, begOffset, index, line);
					break;
				case ']':
					t= stream.new Token(RIGHT_SQUARE, begOffset, index, line);
					break;
				case '{':
					t= stream.new Token(LEFT_BRACE, begOffset, index, line);
					break;
				case '}':
					t= stream.new Token(RIGHT_BRACE, begOffset, index, line);
					break;
				case ':':
					t= stream.new Token(COLON, begOffset, index, line);
					break;
				case '=':
					state = State.GOT_EQUALS;
					break;
				case '|':
					t= stream.new Token(BAR, begOffset, index, line);
					break;
				case '&':
					t= stream.new Token(AND, begOffset, index, line);
					break;
				case '<':
					state = State.GOT_LANGLE;
					break;
				case '>':
					state = State.GOT_RANGLE;
					break;
				case '+':
					t= stream.new Token(PLUS, begOffset, index, line);
					break;
				case '-':
					state = State.GOT_HYPHEN;
					break;
				case '*':
					t= stream.new Token(MUL, begOffset, index, line);
					break;
				case '/':
					state = State.GOT_SLASH;
					break;
				case '!':
					state = State.GOT_EXCLAM;
					break;
				case '"':
					state = State.STRING_PART;
					break;
				case '\n':
					line++;
					break;
				case '\r':
					state =State.GOT_BSLASHR;
					break;
				case (char) -1:
					state = State.EOF;
					break;
				default:
					if(Character.isDigit(ch)){
						if(ch == '0'){
							t= stream.new Token(INT_LIT, begOffset, index, line);
						}else{
							state = State.INT_PART;
						}						
					}else if(Character.isJavaIdentifierStart(ch)){
						state = State.IDENT_PART;
					}else if(Character.isWhitespace(ch)){
						state = State.START;
					}else{
						t= stream.new Token(ILLEGAL_CHAR, begOffset, index, line);
					}
				}
				break;
			case GOT_EQUALS:
				switch(ch){
				case '=':
					t= stream.new Token(EQUAL, begOffset, index, line);
					break;
				default:
					t= stream.new Token(ASSIGN, begOffset, --index, line);
				}
				break;
			case GOT_LANGLE:
				switch(ch){
				case '=':
					t= stream.new Token(LESS_EQUAL, begOffset, index, line);
					break;
				default:
					t= stream.new Token(LESS_THAN, begOffset, --index, line);
				}
				break;
			case GOT_RANGLE:
				switch(ch){
				case '=':
					t= stream.new Token(GREATER_EQUAL, begOffset, index, line);
					break;
				default:
					t= stream.new Token(GREATER_THAN, begOffset, --index, line);
				}
				break;
			case GOT_HYPHEN:
				switch(ch){
				default:
					t= stream.new Token(MINUS, begOffset, --index, line);
				}
				break;
			case GOT_EXCLAM:
				switch(ch){
				case '=':
					t= stream.new Token(NOTEQUAL, begOffset, index, line);
					break;
				default:
					t= stream.new Token(NOT, begOffset, --index, line);
				}
				break;
			case GOT_SLASH:
				switch(ch){
				case '*':
					state = State.GOT_SLASHSTAR;
					break;
				default:
					t= stream.new Token(DIV, begOffset, --index, line);
				}
				break;
			case GOT_SLASHSTAR:
				switch(ch){
				case '*':
					state = State.GOT_SLASH2STAR;
					break;
				case (char) -1:
					t= stream.new Token(UNTERMINATED_COMMENT, begOffset, --index, line);
					break;
				default:
				}
				break;
			case GOT_SLASH2STAR:
				switch(ch){
				case '/':
					state = State.START;
					break;
				case '*':
					break;
				case (char) -1:
					t= stream.new Token(UNTERMINATED_COMMENT, begOffset, --index, line);
					break;
				default:
					state = State.GOT_SLASHSTAR;
				}
				break;
			case GOT_BSLASHR:
				if(ch != '\n'){
					index--;
				}
				line++;
				state = State.START;
				break;
			case IDENT_PART:
				if(Character.isJavaIdentifierPart(ch)){
					state = State.IDENT_PART;
				}else{
					int len = index - begOffset -1;
					char[] temp = new char[len];
					for(int i=0; i<len; i++){
						temp[i] = stream.inputChars[begOffset + i];
					}
					String s = String.valueOf(temp);
					if(reservedWord.containsKey(s)){
						t= stream.new Token(reservedWord.get(s), begOffset, --index, line);
					}else{
						t= stream.new Token(IDENTIFIER, begOffset, --index, line);
					}
				}
				break;
			case INT_PART:
				if(Character.isDigit(ch)){
					state = State.INT_PART;
				}else{
					t= stream.new Token(INT_LIT, begOffset, --index,line);
				}		
				break;
			case STRING_PART:
				switch(ch){
				case '"':
					t= stream.new Token(STRING_LIT, begOffset, index, line);
					break;
				case '\\':
					if(getch() == '"' || getch() == 'n' || getch() == 'r'){
						break;
					}else{
						index--;
					}
					break;
				case (char) -1:
					t= stream.new Token(UNTERMINATED_STRING, begOffset, --index, line);
					break;
				default:
					state = State.STRING_PART;
				}
				break;
			case EOF:
				//take two iteration to end
				t= stream.new Token(EOF, begOffset, index-2, line);
				break;
			default:
				assert false:"should not reach here";
			}			
		}while(t == null);
		return t;		
	}

}
