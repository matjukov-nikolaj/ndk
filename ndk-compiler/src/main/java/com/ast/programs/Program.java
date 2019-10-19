package com.ast.programs;

import java.util.List;

import com.TokenStream.Token;
import com.ast.visitor.ASTNode;
import com.ast.visitor.ASTVisitor;
import com.ast.qualifiedNames.QualifiedName;
import com.ast.blocks.Block;

public class Program extends ASTNode {
	public List<QualifiedName> imports;
	public String name;
	public Block block;
	
	public String JVMName;

	public Program(Token firstToken, List<QualifiedName> imports, String name, Block block) {
		super(firstToken);
		this.imports = imports;
		this.name = name;
		this.block = block;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitProgram(this,arg);
	}

	
}
