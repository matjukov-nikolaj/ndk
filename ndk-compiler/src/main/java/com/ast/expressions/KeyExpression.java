package com.ast.expressions;

import com.TokenStream.Token;
import com.ast.visitor.ASTVisitor;

public class KeyExpression extends Expression {
	public Expression expression;
	

	public KeyExpression(Token firstToken, Expression expression) {
		super(firstToken);
		this.expression = expression;
	}


	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitKeyExpression(this,arg);
	}

}
