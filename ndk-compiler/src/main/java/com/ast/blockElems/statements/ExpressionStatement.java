package com.ast.blockElems.statements;

import com.TokenStream.Token;
import com.ast.visitor.ASTVisitor;
import com.ast.expressions.Expression;

public class ExpressionStatement extends Statement {
	public Expression expression;
	
	public ExpressionStatement(Token firstToken, Expression expression) {
		super(firstToken);
		this.expression = expression;
	}



	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitExpressionStatement(this,arg);
	}

}
