package compiler.ndk.ast.blockElems.statements;

import compiler.ndk.lexer.TokenStream.Token;
import compiler.ndk.ast.visitor.ASTVisitor;
import compiler.ndk.ast.expressions.Expression;
import compiler.ndk.ast.lValues.LValue;

public class AssignmentStatement extends Statement {
	
	public LValue lvalue;
	public Expression expression;

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitAssignmentStatement(this,arg);
	}

	public AssignmentStatement(Token firstToken, LValue lvalue,
			Expression expression) {
		super(firstToken);
		this.lvalue = lvalue;
		this.expression = expression;
	}

}
