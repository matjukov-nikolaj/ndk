package com.ast.expressions;

import com.TokenStream.Token;
import com.ast.visitor.ASTNode;

public abstract class Expression extends ASTNode {
	
	String expressionType;

	public String getType() {
		return expressionType;
	}

	public void setType(String type) {
		this.expressionType = type;
	}

	Expression(Token firstToken) {
		super(firstToken);
	}

}
