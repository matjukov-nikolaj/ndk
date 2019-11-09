package compiler.ndk.ast.blockElems.statements;

import compiler.ndk.lexer.TokenStream.Token;
import compiler.ndk.ast.visitor.ASTVisitor;
import compiler.ndk.ast.blocks.Block;
import compiler.ndk.ast.expressions.RangeExpression;

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
