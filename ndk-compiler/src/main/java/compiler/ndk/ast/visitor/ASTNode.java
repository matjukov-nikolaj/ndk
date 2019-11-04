package compiler.ndk.ast.visitor;

import compiler.ndk.lexer.TokenStream.Token;


public abstract class ASTNode {
	
	protected ASTNode(Token firstToken){
		this.firstToken=firstToken;
	}
	public Token firstToken;

	public abstract Object visit(ASTVisitor v, Object arg) throws Exception;

	@Override
	public String toString() {
		ToStringVisitor v = new ToStringVisitor();
		try {
			visit(v, "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return v.getString();
	}

}
