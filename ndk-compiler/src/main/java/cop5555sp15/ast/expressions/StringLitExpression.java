package cop5555sp15.ast.expressions;

import cop5555sp15.TokenStream.Token;
import cop5555sp15.ast.ASTVisitor;

public class StringLitExpression extends Expression {
	public String value;
	
	public StringLitExpression(Token firstToken, String value) {
		super(firstToken);
		this.value = value;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitStringLitExpression(this,arg);
	}

}
