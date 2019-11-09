package compiler.ndk.ast.types;

import compiler.ndk.lexer.TokenStream.Token;
import compiler.ndk.ast.visitor.ASTVisitor;

public class UndeclaredType extends Type {

	public UndeclaredType(Token firstToken) {
		super(firstToken);
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitUndeclaredType(this, arg);
	}

	@Override
	String getJVMType() {
		// TODO Auto-generated method stub
		return null;
	}


	
	

}
