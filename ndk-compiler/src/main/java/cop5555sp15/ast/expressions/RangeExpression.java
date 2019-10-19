package cop5555sp15.ast.expressions;

import cop5555sp15.TokenStream.Token;
import cop5555sp15.ast.ASTNode;
import cop5555sp15.ast.ASTVisitor;

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
