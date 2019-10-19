package com.ast.blockElems.statements;

import com.TokenStream.Token;
import com.ast.visitor.ASTVisitor;
import com.ast.blocks.Block;
import com.ast.expressions.Expression;

public class IfElseStatement extends Statement {
	public Expression expression;
	public Block ifBlock;
	public Block elseBlock;
	
	public IfElseStatement(Token firstToken, Expression expression, Block ifBlock, Block elseBlock) {
		super(firstToken);
		this.expression = expression;
		this.ifBlock = ifBlock;
		this.elseBlock = elseBlock;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitIfElseStatement(this, arg);
	}

}
