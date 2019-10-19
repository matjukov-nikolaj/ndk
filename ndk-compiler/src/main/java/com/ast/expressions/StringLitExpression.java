package com.ast.expressions;

import com.TokenStream.Token;
import com.ast.visitor.ASTVisitor;

public class StringLitExpression extends Expression {
	public String value;
	
	public StringLitExpression(Token firstToken, String value) {
		super(firstToken);
		this.value = value;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitStringLitExpression(this,arg);
	}

}
