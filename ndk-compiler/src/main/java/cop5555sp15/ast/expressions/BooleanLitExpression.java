package cop5555sp15.ast.expressions;

import cop5555sp15.TokenStream.Token;
import cop5555sp15.ast.ASTVisitor;

public class BooleanLitExpression extends Expression {
	public boolean value;
	
	

	public BooleanLitExpression(Token firstToken, boolean value) {
		super(firstToken);
		this.value = value;
	}



	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitBooleanLitExpression(this,arg);
	}

}
