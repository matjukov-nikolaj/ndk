package com.ast.blockElems.declarations;

import com.TokenStream.Token;
import com.ast.blockElems.BlockElem;


public abstract class Declaration extends BlockElem {
	
	public boolean globalScope;

	Declaration(Token firstToken) {
		super(firstToken);
	}
	
}
