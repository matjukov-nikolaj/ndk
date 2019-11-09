package compiler.ndk.ast.types;

import compiler.ndk.lexer.TokenStream.Token;
import compiler.ndk.ast.visitor.ASTVisitor;

public class ListType extends Type {
	public Type type;
	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitListType(this,arg);
	}
	public ListType(Token firstToken, Type type) {
		super(firstToken);
		this.type = type;
	}
	@Override
	public String getJVMType() {
		String elementType = type.getJVMType();
		return "Ljava/util/List<"+elementType+">;";	
	}
	
	public static String prefix(){
		return "Ljava/util/List";
	}


}
