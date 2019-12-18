package compiler.ndk.ast.blockElems.statements;

import compiler.ndk.ast.blocks.Block;
import compiler.ndk.ast.expressions.Expression;
import compiler.ndk.ast.visitor.ASTVisitor;
import compiler.ndk.lexer.TokenStream.Token;

public class WhileStatement extends Statement {
	public Expression expression;
	public Block block;

	public WhileStatement(Token firstToken, Expression expression, Block block) {
		super(firstToken);
		this.expression = expression;
		this.block = block;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitWhileStatement(this,arg);
	}

}
