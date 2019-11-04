package compiler.ndk.ast.expressions;

import compiler.ndk.lexer.TokenStream.Token;
import compiler.ndk.ast.visitor.ASTVisitor;

public class IntLitExpression extends Expression {
	public int value;

	public IntLitExpression(Token firstToken, int value) {
		super(firstToken);
		this.value = value;
	}



	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitIntLitExpression(this,arg);
	}

}
