package compiler.ndk.ast.types;

import compiler.ndk.ast.visitor.ASTVisitor;
import compiler.ndk.lexer.TokenStream;

public class ListType extends Type {
    public Type type;
    @Override
    public Object visit(ASTVisitor v, Object arg) throws Exception {
        return v.visitListType(this,arg);
    }
    public ListType(TokenStream.Token firstToken, Type type) {
        super(firstToken);
        this.type = type;
    }
    @Override
    public String getJVMType() {
        String elementType = type.getJVMType();
        return "Ljava/util/ArrayList<"+elementType+">;";
    }

    public static String prefix(){
        return "Ljava/util/ArrayList";
    }
}