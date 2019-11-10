//package compiler.ndk.ast.expressions;
//
//import compiler.ndk.lexer.TokenStream.Token;
//import compiler.ndk.ast.visitor.ASTVisitor;
//
//public class ListElemExpression extends Expression {
//public Token identToken;
//public Expression expression;
//
//
//	public ListElemExpression(Token firstToken, Token identToken,
//		Expression expression) {
//	super(firstToken);
//	this.identToken = identToken;
//	this.expression = expression;
//}
//
//
//	@Override
//	public Object visit(ASTVisitor v, Object arg) throws Exception {
//		return v.visitListElemExpression(this,arg);
//	}
//
//}
