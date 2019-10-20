package compiler.ndk.ast.qualifiedNames;

import compiler.ndk.lexer.TokenStream.Token;
import compiler.ndk.ast.visitor.ASTNode;
import compiler.ndk.ast.visitor.ASTVisitor;


public class QualifiedName extends ASTNode {
	
	public String name;

	public QualifiedName(Token firstToken, String name) {
		super(firstToken);
		this.name = name;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitQualifiedName(this, arg);
	}
	
}
