package com.ast.expressions;

import com.TokenStream.Token;
import com.ast.visitor.ASTVisitor;

public class KeyValueExpression extends Expression {
	public Expression key;
	public Expression value;
	

	public KeyValueExpression(Token firstToken, Expression key, Expression value) {
		super(firstToken);
		this.key = key;
		this.value = value;
	}


	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitKeyValueExpression(this,arg);
	}
	
	public String keyType(){
		return key.expressionType;
	}
	
	public String valueType(){
		return value.expressionType;
	}

}
