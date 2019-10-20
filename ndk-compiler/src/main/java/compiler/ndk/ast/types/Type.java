package compiler.ndk.ast.types;

import compiler.ndk.lexer.TokenStream.Token;
import compiler.ndk.ast.visitor.ASTNode;

public abstract class Type extends ASTNode {

	Type(Token firstToken) {
		super(firstToken);
	}
	
	abstract String getJVMType();

}
