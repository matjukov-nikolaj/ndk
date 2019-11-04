package compiler.ndk.ast.expressions;

import compiler.ndk.lexer.TokenStream.Token;
import compiler.ndk.ast.visitor.ASTNode;

public abstract class Expression extends ASTNode {
	
	String expressionType;


	Expression(Token firstToken) {
		super(firstToken);
	}

}
