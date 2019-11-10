//package compiler.ndk.ast.blockElems.statements;
//
//import compiler.ndk.lexer.TokenStream.Token;
//import compiler.ndk.ast.visitor.ASTVisitor;
//import compiler.ndk.ast.blocks.Block;
//import compiler.ndk.ast.expressions.Expression;
//
//public class IfElseStatement extends Statement {
//	public Expression expression;
//	public Block ifBlock;
//	public Block elseBlock;
//
//	public IfElseStatement(Token firstToken, Expression expression, Block ifBlock, Block elseBlock) {
//		super(firstToken);
//		this.expression = expression;
//		this.ifBlock = ifBlock;
//		this.elseBlock = elseBlock;
//	}
//
//	@Override
//	public Object visit(ASTVisitor v, Object arg) throws Exception {
//		return v.visitIfElseStatement(this, arg);
//	}
//
//}
