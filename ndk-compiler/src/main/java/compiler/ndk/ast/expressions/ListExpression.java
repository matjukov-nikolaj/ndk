//package compiler.ndk.ast.expressions;
//
//import java.util.List;
//
//import compiler.ndk.lexer.TokenStream.Token;
//import compiler.ndk.ast.visitor.ASTVisitor;
//
//public class ListExpression extends Expression {
//	public List<Expression> expressionList;
//
//	public ListExpression(Token firstToken, List<Expression> expressionList) {
//		super(firstToken);
//		this.expressionList = expressionList;
//	}
//
//	@Override
//	public Object visit(ASTVisitor v, Object arg) throws Exception {
//		return v.visitListExpression(this,arg);
//	}
//
//}
