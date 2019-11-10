package compiler.ndk.ast.expressions;

import compiler.ndk.lexer.TokenStream.Token;
import compiler.ndk.ast.visitor.ASTVisitor;
import compiler.ndk.ast.closures.Closure;

public class ClosureExpression extends Expression {
	public Closure closure;
	

	public ClosureExpression(Token firstToken, Closure closure) {
		super(firstToken);
		this.closure = closure;
	}


	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitClosureExpression(this,arg);
	}

}
