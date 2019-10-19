package cop5555sp15.ast.expressions;

import java.util.List;

import cop5555sp15.TokenStream.Token;
import cop5555sp15.ast.ASTVisitor;

public class ClosureEvalExpression extends Expression {
	public Token identToken;
	public List<Expression> expressionList;
	
	public ClosureEvalExpression(Token firstToken, Token identToken,
			List<Expression> expressionList) {
		super(firstToken);
		this.identToken = identToken;
		this.expressionList = expressionList;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitClosureEvalExpression(this,arg);
	}

}
