package compiler.ndk.ast.lValues;

import compiler.ndk.lexer.TokenStream.Token;
import compiler.ndk.ast.visitor.ASTVisitor;

public class IdentLValue extends LValue {
	public Token identToken;
	

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitIdentLValue(this,arg);
	}


	public IdentLValue(Token firstToken, Token identToken) {
		super(firstToken);
		this.identToken = identToken;
	}

}
