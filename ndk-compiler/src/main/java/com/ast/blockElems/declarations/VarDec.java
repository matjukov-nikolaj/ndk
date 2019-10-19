package com.ast.blockElems.declarations;

import com.TokenStream.Token;
import com.ast.visitor.ASTVisitor;
import com.ast.types.Type;

public class VarDec extends Declaration {
	public Token identToken;
	public Type type;
	

	public VarDec(Token firstToken, Token identToken, Type type) {
		super(firstToken);
		this.identToken = identToken;
		this.type = type;
	}


	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitVarDec(this,arg);
	}

}
