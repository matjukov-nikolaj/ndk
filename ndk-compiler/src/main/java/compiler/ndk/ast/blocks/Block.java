package compiler.ndk.ast.blocks;

import java.util.List;

import compiler.ndk.lexer.TokenStream.Token;
import compiler.ndk.ast.visitor.ASTNode;
import compiler.ndk.ast.visitor.ASTVisitor;
import compiler.ndk.ast.blockElems.BlockElem;

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
