package cop5555sp15.ast.expressions;

import cop5555sp15.TokenStream.Token;
import cop5555sp15.ast.ASTVisitor;

public class KeyExpression extends Expression {
	public Expression expression;
	

	public KeyExpression(Token firstToken, Expression expression) {
		super(firstToken);
		this.expression = expression;
	}


	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitKeyExpression(this,arg);
	}

}
