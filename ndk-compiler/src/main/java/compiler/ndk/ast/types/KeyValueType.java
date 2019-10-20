package compiler.ndk.ast.types;

import compiler.ndk.lexer.TokenStream.Token;
import compiler.ndk.ast.visitor.ASTVisitor;

public class KeyValueType extends Type {
	public SimpleType keyType;
	public Type valueType;
	

	public KeyValueType(Token firstToken, SimpleType keyType, Type valueType) {
		super(firstToken);
		this.keyType = keyType;
		this.valueType = valueType;
	}


	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitKeyValueType(this,arg);
	}


	@Override
	String getJVMType() {
		String keyJVMType = keyType.getJVMType();
		String valueJVMType = valueType.getJVMType();
		return "Ljava/util/Map$Entry<"+keyJVMType+valueJVMType+">;";
	}

}
