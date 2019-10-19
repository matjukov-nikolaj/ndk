package com.ast.blockElems.statements;

import com.TokenStream.Token;
import com.ast.blockElems.BlockElem;

public abstract class Statement extends BlockElem {

	Statement(Token firstToken) {
		super(firstToken);
	}

}
