package cop5555sp15.ast.expressions;

import cop5555sp15.TokenStream.Token;
import cop5555sp15.ast.ASTNode;

public abstract class Expression extends ASTNode {
	
	String expressionType;

	public String getType() {
		return expressionType;
	}

	public void setType(String type) {
		this.expressionType = type;
	}

	Expression(Token firstToken) {
		super(firstToken);
	}

}
