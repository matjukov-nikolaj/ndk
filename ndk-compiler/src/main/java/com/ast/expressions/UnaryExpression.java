package com.ast.expressions;

import com.TokenStream.Token;
import com.ast.visitor.ASTVisitor;

public class UnaryExpression extends Expression {
	public Token op;
	public Expression expression;
	

	public UnaryExpression(Token firstToken, Token op,
			Expression expression) {
		super(firstToken);
		this.op = op;
		this.expression = expression;
	}


	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitUnaryExpression(this,arg);
	}

}
