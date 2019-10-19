package com.ast.types;

import com.TokenStream.Token;
import com.ast.visitor.ASTNode;

public abstract class Type extends ASTNode {

	Type(Token firstToken) {
		super(firstToken);
	}
	
	abstract String getJVMType();

}
