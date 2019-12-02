package compiler.ndk.ast.blockElems.declarations;

import compiler.ndk.lexer.TokenStream.Token;
import compiler.ndk.ast.visitor.ASTVisitor;
import compiler.ndk.ast.types.Type;

public class VarDec extends Declaration {
    public Token identToken;
    public Type type;
    private int slot;


    public VarDec(Token firstToken, Token identToken, Type type) {
        super(firstToken);
        this.identToken = identToken;
        this.type = type;
        slot = 0;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }

    @Override
    public Object visit(ASTVisitor v, Object arg) throws Exception {
        return v.visitVarDec(this, arg);
    }

}
