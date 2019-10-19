package com.ast.blockElems.statements;

import com.TokenStream.Token;
import com.ast.visitor.ASTVisitor;
import com.ast.expressions.Expression;

public class PrintStatement extends Statement {
	public Expression expression;
	
	public PrintStatement(Token firstToken, Expression expression) {
		super(firstToken);
		this.expression = expression;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitPrintStatement(this,arg);
	}

}
