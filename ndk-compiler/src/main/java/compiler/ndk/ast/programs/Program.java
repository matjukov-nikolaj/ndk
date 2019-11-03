package compiler.ndk.ast.programs;

import compiler.ndk.lexer.TokenStream.Token;
import compiler.ndk.ast.visitor.ASTNode;
import compiler.ndk.ast.visitor.ASTVisitor;
import compiler.ndk.ast.blocks.Block;

public class Program extends ASTNode {
	public String name;
	public Block block;
	
	public String JVMName;

	public Program(Token firstToken,String name, Block block) {
		super(firstToken);
		this.name = name;
		this.block = block;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitProgram(this,arg);
	}

	
}
