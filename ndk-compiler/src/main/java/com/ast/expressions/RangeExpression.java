package com.ast.expressions;

import com.TokenStream.Token;
import com.ast.visitor.ASTNode;
import com.ast.visitor.ASTVisitor;

public class RangeExpression extends ASTNode {
	public Expression lower;
	public Expression upper;

	public RangeExpression(Token firstToken, Expression lower, Expression upper) {
		super(firstToken);
		this.lower = lower;
		this.upper = upper;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitRangeExpression(this,arg);
	}

}
