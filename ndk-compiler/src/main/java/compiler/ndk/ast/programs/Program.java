package compiler.ndk.ast.programs;

import java.util.List;

import compiler.ndk.lexer.TokenStream.Token;
import compiler.ndk.ast.visitor.ASTNode;
import compiler.ndk.ast.visitor.ASTVisitor;
import compiler.ndk.ast.qualifiedNames.QualifiedName;
import compiler.ndk.ast.blocks.Block;

public class Program extends ASTNode {
	public List<QualifiedName> imports;
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
