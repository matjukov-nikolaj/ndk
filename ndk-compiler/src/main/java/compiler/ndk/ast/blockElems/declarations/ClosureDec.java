package compiler.ndk.ast.blockElems.declarations;

import compiler.ndk.lexer.TokenStream.Token;
import compiler.ndk.ast.visitor.ASTVisitor;
import compiler.ndk.ast.closures.Closure;

public class ClosureDec extends Declaration {
	public Token identToken;
	public Closure closure;

	public ClosureDec(Token firstToken, Token identToken, Closure closure) {
		super(firstToken);
		this.identToken = identToken;
		this.closure = closure;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitClosureDec(this,arg);
	}

}
