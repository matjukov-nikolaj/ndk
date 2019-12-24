package compiler.ndk.ast.lValues;

import compiler.ndk.ast.expressions.Expression;
import compiler.ndk.ast.visitor.ASTVisitor;
import compiler.ndk.lexer.TokenStream;

public class ExpressionLValue extends LValue {
    public TokenStream.Token identToken;
    public Expression expression;

    public ExpressionLValue(TokenStream.Token firstToken, TokenStream.Token identToken,
                            Expression expression) {
        super(firstToken);
        this.identToken = identToken;
        this.expression = expression;
    }

    @Override
    public Object visit(ASTVisitor v, Object arg) throws Exception {
        return v.visitExpressionLValue(this,arg);
    }

}