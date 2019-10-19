package com.ast.blockElems;

import com.TokenStream.Token;
import com.ast.visitor.ASTNode;

public abstract class BlockElem extends ASTNode {

	protected BlockElem(Token firstToken) {
		super(firstToken);
	}

}
