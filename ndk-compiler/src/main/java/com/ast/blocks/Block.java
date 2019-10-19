package com.ast.blocks;

import java.util.List;

import com.TokenStream.Token;
import com.ast.visitor.ASTNode;
import com.ast.visitor.ASTVisitor;
import com.ast.blockElems.BlockElem;

public class Block extends ASTNode {
	public List<BlockElem> elems;

	public Block(Token firstToken, List<BlockElem> elems) {
		super(firstToken);
		this.elems = elems;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitBlock(this, arg);
	}
}
