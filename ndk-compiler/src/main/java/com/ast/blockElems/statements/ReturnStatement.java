package com.ast.blockElems.statements;

import com.TokenStream.Token;
import com.ast.visitor.ASTVisitor;
import com.ast.expressions.Expression;

public class ReturnStatement extends Statement {
	public Expression expression;


	public ReturnStatement(Token firstToken, Expression expression) {
		super(firstToken);
		this.expression = expression;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitReturnStatement(this,arg);
	}

}
