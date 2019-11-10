//package compiler.ndk.ast.expressions;
//
//import compiler.ndk.lexer.TokenStream.Token;
//import compiler.ndk.ast.visitor.ASTVisitor;
//
//public class SizeExpression extends Expression {
//	public Expression expression;
//
//	public SizeExpression(Token firstToken, Expression expression) {
//		super(firstToken);
//		this.expression = expression;
//	}
//
//	@Override
//	public Object visit(ASTVisitor v, Object arg) throws Exception {
//		return v.visitSizeExpression(this,arg);
//	}
//
//}
