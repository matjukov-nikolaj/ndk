package com.ast.lValues;

import com.TokenStream.Token;
import com.ast.visitor.ASTNode;

public abstract class LValue extends ASTNode {
	
	String type;
	
	

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


	public LValue(Token firstToken) {
		super(firstToken);
	}


}
