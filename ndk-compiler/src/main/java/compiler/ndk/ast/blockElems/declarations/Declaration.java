package compiler.ndk.ast.blockElems.declarations;

import compiler.ndk.lexer.TokenStream.Token;
import compiler.ndk.ast.blockElems.BlockElem;


public abstract class Declaration extends BlockElem {
	
	public boolean globalScope;

	Declaration(Token firstToken) {
		super(firstToken);
	}
	
}
