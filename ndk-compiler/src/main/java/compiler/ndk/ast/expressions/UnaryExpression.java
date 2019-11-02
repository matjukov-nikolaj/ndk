package compiler.ndk.ast.expressions;

import compiler.ndk.lexer.TokenStream.Token;
import compiler.ndk.ast.visitor.ASTVisitor;

public class UnaryExpression extends Expression {
	public Token op;
	public Expression expression;
	

	public UnaryExpression(Token firstToken, Token op,
			Expression expression) {
		super(firstToken);
		this.op = op;
		this.expression = expression;
	}


	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitUnaryExpression(this,arg);
	}

}
