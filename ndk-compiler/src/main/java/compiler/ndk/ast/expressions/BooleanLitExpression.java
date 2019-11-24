package compiler.ndk.ast.expressions;

import compiler.ndk.ast.visitor.ASTVisitor;
import compiler.ndk.lexer.TokenStream.Token;

public class BooleanLitExpression extends Expression {
	public boolean value;
	
	

	public BooleanLitExpression(Token firstToken, boolean value) {
		super(firstToken);
		this.value = value;
	}



	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitBooleanLitExpression(this,arg);
	}

}
