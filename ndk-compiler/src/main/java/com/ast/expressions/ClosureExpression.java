package com.ast.expressions;

import com.TokenStream.Token;
import com.ast.visitor.ASTVisitor;
import com.ast.closures.Closure;

public class ClosureExpression extends Expression {
	public Closure closure;
	

	public ClosureExpression(Token firstToken, Closure closure) {
		super(firstToken);
		this.closure = closure;
	}


	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitClosureExpression(this,arg);
	}

}
