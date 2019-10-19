package cop5555sp15.ast.expressions;

import java.util.List;

import cop5555sp15.TokenStream.Token;
import cop5555sp15.ast.ASTVisitor;

public class ListExpression extends Expression {
	public List<Expression> expressionList;

	public ListExpression(Token firstToken, List<Expression> expressionList) {
		super(firstToken);
		this.expressionList = expressionList;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitListExpression(this,arg);
	}

}
