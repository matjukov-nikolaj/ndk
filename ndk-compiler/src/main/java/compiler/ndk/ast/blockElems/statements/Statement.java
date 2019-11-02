package compiler.ndk.ast.blockElems.statements;

import compiler.ndk.lexer.TokenStream.Token;
import compiler.ndk.ast.blockElems.BlockElem;

public abstract class Statement extends BlockElem {

	Statement(Token firstToken) {
		super(firstToken);
	}

}
