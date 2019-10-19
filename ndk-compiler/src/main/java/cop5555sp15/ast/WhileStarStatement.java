package cop5555sp15.ast;

import cop5555sp15.TokenStream.Token;
import cop5555sp15.ast.expressions.Expression;

public class WhileStarStatement extends Statement {
	Expression expression;
	Block block;

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
