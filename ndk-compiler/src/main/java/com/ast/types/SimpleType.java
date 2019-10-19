package com.ast.types;

import com.TokenStream.Token;
import com.ast.visitor.ASTVisitor;

public class SimpleType extends Type {
	public Token type;
	

	public SimpleType(Token firstToken, Token type) {
		super(firstToken);
		this.type = type;
	}


	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitSimpleType(this,arg);
	}


	@Override
	public String getJVMType() {
		if(type.getText().equals("int")) return "I";
		else if (type.getText().equals("boolean")) return "Z";
		else return "Ljava/lang/String;";
	}

}
