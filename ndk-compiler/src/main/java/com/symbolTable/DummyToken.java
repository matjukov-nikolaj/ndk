package com.symbolTable;

import com.TokenStream;
import static com.TokenStream.Kind.IDENT;
import com.TokenStream.Token;

public class DummyToken extends Token {

	static TokenStream stream = new TokenStream("");
	
	String text;
	
	public DummyToken(String id) {
		stream.super(IDENT, 0, 0, 0);
		this.text = id;
	}
	
	@Override
	public String getText(){
		return text;
	}

}
