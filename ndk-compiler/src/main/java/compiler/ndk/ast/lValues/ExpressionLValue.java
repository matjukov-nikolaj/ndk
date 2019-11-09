package compiler.ndk.ast.lValues;

import compiler.ndk.lexer.TokenStream.Token;
import compiler.ndk.ast.visitor.ASTVisitor;
import compiler.ndk.ast.expressions.Expression;

public class ExpressionLValue extends LValue {
	public Token identToken;
	public Expression expression;

	public ExpressionLValue(Token firstToken, Token identToken,
			Expression expression) {
		super(firstToken);
		this.identToken = identToken;
		this.expression = expression;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitExpressionLValue(this,arg);
	}

}
