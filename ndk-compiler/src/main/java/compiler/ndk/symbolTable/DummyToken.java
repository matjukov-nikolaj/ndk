package compiler.ndk.symbolTable;

import compiler.ndk.lexer.TokenStream;
import static compiler.ndk.lexer.TokenStream.Kind.IDENTIFIER;
import compiler.ndk.lexer.TokenStream.Token;

public class DummyToken extends Token {

	static TokenStream stream = new TokenStream("");
	
	String text;
	
	public DummyToken(String id) {
		stream.super(IDENTIFIER, 0, 0, 0);
		this.text = id;
	}
	
	@Override
	public String getText(){
		return text;
	}

}
