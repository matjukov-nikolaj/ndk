package compiler.ndk.ast.expressions;

import compiler.ndk.ast.blockElems.declarations.VarDec;
import compiler.ndk.ast.visitor.ASTVisitor;
import compiler.ndk.lexer.TokenStream;

public class ListElemExpression extends Expression {
    public TokenStream.Token identToken;
    public Expression expression;
    public VarDec dec;

    public ListElemExpression(TokenStream.Token firstToken, TokenStream.Token identToken,
                              Expression expression) {
        super(firstToken);
        this.identToken = identToken;
        this.expression = expression;
    }


    @Override
    public Object visit(ASTVisitor v, Object arg) throws Exception {
        return v.visitListElemExpression(this,arg);
    }

}
