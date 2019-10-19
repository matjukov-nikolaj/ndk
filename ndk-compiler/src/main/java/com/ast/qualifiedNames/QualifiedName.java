package com.ast.qualifiedNames;

import com.TokenStream.Token;
import com.ast.visitor.ASTNode;
import com.ast.visitor.ASTVisitor;


public class QualifiedName extends ASTNode {
	
	public String name;

	public QualifiedName(Token firstToken, String name) {
		super(firstToken);
		this.name = name;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitQualifiedName(this, arg);
	}
	
}
