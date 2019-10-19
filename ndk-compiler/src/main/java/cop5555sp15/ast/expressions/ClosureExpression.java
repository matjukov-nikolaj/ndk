package cop5555sp15.ast.expressions;

import cop5555sp15.TokenStream.Token;
import cop5555sp15.ast.ASTVisitor;
import cop5555sp15.ast.Closure;

public class ClosureExpression extends Expression {
	public Closure closure;
	

	public ClosureExpression(Token firstToken, Closure closure) {
		super(firstToken);
		this.closure = closure;
	}


	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitClosureExpression(this,arg);
	}

}
