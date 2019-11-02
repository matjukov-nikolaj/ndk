package compiler.ndk.ast.expressions;

import compiler.ndk.lexer.TokenStream.Token;
import compiler.ndk.ast.visitor.ASTNode;
import compiler.ndk.ast.visitor.ASTVisitor;

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
