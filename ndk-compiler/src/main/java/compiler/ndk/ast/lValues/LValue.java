package compiler.ndk.ast.lValues;

import compiler.ndk.lexer.TokenStream.Token;
import compiler.ndk.ast.visitor.ASTNode;

public abstract class LValue extends ASTNode {

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    LValue(Token firstToken) {
        super(firstToken);
    }

}