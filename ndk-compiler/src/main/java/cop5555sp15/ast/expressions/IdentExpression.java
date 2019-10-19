package cop5555sp15.ast.expressions;

import cop5555sp15.TokenStream.Token;
import cop5555sp15.ast.ASTVisitor;

public class IdentExpression extends Expression {
	public Token identToken;
	

	public IdentExpression(Token firstToken, Token identToken) {
		super(firstToken);
		this.identToken = identToken;
	}


	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitIdentExpression(this,arg);
	}

}
