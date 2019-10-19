package com.ast.blockElems.statements;

import com.TokenStream.Token;
import com.ast.visitor.ASTVisitor;
import com.ast.blocks.Block;
import com.ast.expressions.Expression;

public class WhileStarStatement extends Statement {
	public Expression expression;
	public Block block;

	public WhileStarStatement(Token firstToken, Expression expression,
			Block block) {
		super(firstToken);
		this.expression = expression;
		this.block = block;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitWhileStarStatement(this,arg);
	}

}
