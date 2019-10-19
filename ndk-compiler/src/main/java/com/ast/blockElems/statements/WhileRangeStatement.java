package com.ast.blockElems.statements;

import com.TokenStream.Token;
import com.ast.visitor.ASTVisitor;
import com.ast.blocks.Block;
import com.ast.expressions.RangeExpression;

public class WhileRangeStatement extends Statement {
	public RangeExpression rangeExpression;
	public Block block;

	public WhileRangeStatement(Token firstToken,
			RangeExpression rangeExpression, Block block) {
		super(firstToken);
		this.rangeExpression = rangeExpression;
		this.block = block;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitWhileRangeStatement(this,arg);
	}

}
