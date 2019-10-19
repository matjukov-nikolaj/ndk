package com.ast.expressions;

import com.TokenStream.Token;
import com.ast.visitor.ASTVisitor;

public class ListOrMapElemExpression extends Expression {
public Token identToken;
public Expression expression;


	public ListOrMapElemExpression(Token firstToken, Token identToken,
		Expression expression) {
	super(firstToken);
	this.identToken = identToken;
	this.expression = expression;
}


	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitListOrMapElemExpression(this,arg);
	}

}
