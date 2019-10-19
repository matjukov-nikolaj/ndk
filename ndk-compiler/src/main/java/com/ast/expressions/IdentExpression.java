package com.ast.expressions;

import com.TokenStream.Token;
import com.ast.visitor.ASTVisitor;

public class IdentExpression extends Expression {
	public Token identToken;
	

	public IdentExpression(Token firstToken, Token identToken) {
		super(firstToken);
		this.identToken = identToken;
	}


	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitIdentExpression(this,arg);
	}

}
