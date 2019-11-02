package compiler.ndk.ast.expressions;

import compiler.ndk.lexer.TokenStream.Token;
import compiler.ndk.ast.visitor.ASTNode;

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
