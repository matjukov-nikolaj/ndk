package compiler.ndk.ast.blockElems;

import compiler.ndk.lexer.TokenStream.Token;
import compiler.ndk.ast.visitor.ASTNode;

public abstract class BlockElem extends ASTNode {

	protected BlockElem(Token firstToken) {
		super(firstToken);
	}

}
