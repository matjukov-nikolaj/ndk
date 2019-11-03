package compiler.ndk.ast.visitor;

import compiler.ndk.ast.expressions.*;
import compiler.ndk.ast.blocks.Block;
import compiler.ndk.ast.programs.Program;
import compiler.ndk.ast.blockElems.statements.*;

public interface ASTVisitor {

	Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg)throws Exception;
	Object visitBlock(Block block, Object arg) throws Exception;
	Object visitExpressionStatement(ExpressionStatement expressionStatement,
			Object arg)throws Exception;
	Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg)throws Exception;
	Object visitPrintStatement(PrintStatement printStatement, Object arg)throws Exception;
	Object visitProgram(Program program, Object arg) throws Exception;
}
