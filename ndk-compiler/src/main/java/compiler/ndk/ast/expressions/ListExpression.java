package compiler.ndk.ast.expressions;

import compiler.ndk.ast.blockElems.declarations.VarDec;
import compiler.ndk.ast.visitor.ASTVisitor;
import compiler.ndk.lexer.TokenStream;

import java.util.List;

public class ListExpression extends Expression {
    public List<Expression> expressionList;
    public VarDec dec;

    public ListExpression(TokenStream.Token firstToken, List<Expression> expressionList) {
        super(firstToken);
        this.expressionList = expressionList;
    }

    @Override
    public Object visit(ASTVisitor v, Object arg) throws Exception {
        return v.visitListExpression(this,arg);
    }
}

