package com.ast.blockElems.statements;

import com.TokenStream.Token;
import com.ast.visitor.ASTVisitor;
import com.ast.expressions.Expression;
import com.ast.lValues.LValue;

public class AssignmentStatement extends Statement {
	
	public LValue lvalue;
	public Expression expression;

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitAssignmentStatement(this,arg);
	}

	public AssignmentStatement(Token firstToken, LValue lvalue,
			Expression expression) {
		super(firstToken);
		this.lvalue = lvalue;
		this.expression = expression;
	}

}
