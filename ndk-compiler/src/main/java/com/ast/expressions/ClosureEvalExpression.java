package com.ast.expressions;

import java.util.List;

import com.TokenStream.Token;
import com.ast.visitor.ASTVisitor;

public class ClosureEvalExpression extends Expression {
	public Token identToken;
	public List<Expression> expressionList;
	
	public ClosureEvalExpression(Token firstToken, Token identToken,
			List<Expression> expressionList) {
		super(firstToken);
		this.identToken = identToken;
		this.expressionList = expressionList;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitClosureEvalExpression(this,arg);
	}

}
