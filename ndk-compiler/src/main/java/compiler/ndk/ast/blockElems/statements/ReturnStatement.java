package compiler.ndk.ast.blockElems.statements;

import compiler.ndk.lexer.TokenStream.Token;
import compiler.ndk.ast.visitor.ASTVisitor;
import compiler.ndk.ast.expressions.Expression;

public class ReturnStatement extends Statement {
	public Expression expression;


	public ReturnStatement(Token firstToken, Expression expression) {
		super(firstToken);
		this.expression = expression;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitReturnStatement(this,arg);
	}

}
