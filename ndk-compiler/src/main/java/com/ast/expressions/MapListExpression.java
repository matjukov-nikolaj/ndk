package com.ast.expressions;

import java.util.List;

import com.TokenStream.Token;
import com.ast.visitor.ASTVisitor;

public class MapListExpression extends Expression {
	public List<KeyValueExpression> mapList;
	
	public MapListExpression(Token firstToken, List<KeyValueExpression> mapList) {
		super(firstToken);
		this.mapList = mapList;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitMapListExpression(this,arg);
	}

}
