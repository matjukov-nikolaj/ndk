package compiler.ndk.ast.visitor;

import compiler.ndk.ast.blockElems.BlockElem;
import compiler.ndk.ast.blockElems.declarations.VarDec;
import compiler.ndk.ast.expressions.*;
import compiler.ndk.ast.blocks.Block;
import compiler.ndk.ast.lValues.IdentLValue;
import compiler.ndk.ast.programs.Program;
import compiler.ndk.ast.blockElems.statements.*;
import compiler.ndk.ast.types.SimpleType;
import compiler.ndk.ast.types.UndeclaredType;

public class ToStringVisitor implements ASTVisitor {

	private StringBuilder sb;

	public ToStringVisitor() {
		sb = new StringBuilder();
	}

	public String getString() {
		return sb.toString();
	}

	@Override
	public Object visitAssignmentStatement(
			AssignmentStatement assignmentStatement, Object arg)
			throws Exception {
		sb.append(arg).append("AssignmentStatement").append('\n');
		String indent = arg + "  ";
		assignmentStatement.lvalue.visit(this, indent);
		assignmentStatement.expression.visit(this, indent);
		return null;
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression,
                                        Object arg) throws Exception {
		sb.append(arg).append("BinaryExpression").append('\n');
		String indent = arg + "  ";
		binaryExpression.expression0.visit(this, indent);
		sb.append(indent).append(binaryExpression.op.getText()).append('\n');
		binaryExpression.expression1.visit(this, indent);
		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		sb.append(arg).append("Block").append('\n');
		String indent = arg + "  ";
		for (BlockElem elem : block.elems) {
			elem.visit(this, indent);
		}
		return null;
	}

	@Override
	public Object visitExpressionStatement(
			ExpressionStatement expressionStatement, Object arg)
			throws Exception {
		sb.append(arg).append("ExpressionStatement").append('\n');
		String indent = arg + "  ";
		expressionStatement.expression.visit(this, indent);
		return null;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression,
									   Object arg) {
		sb.append(arg).append("IdentExpression").append('\n');
		String indent = arg + "  ";
		sb.append(indent).append(identExpression.identToken.getText())
				.append('\n');
		return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identLValue, Object arg) {
		sb.append(arg).append("IdentLValue").append('\n');
		String indent = arg + "  ";
		sb.append(indent).append(identLValue.identToken.getText()).append('\n');
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression,
			Object arg) {
		sb.append(arg).append("IntLitExpression").append('\n');
		String indent = arg + "  ";
		sb.append(indent).append(intLitExpression.value).append('\n');
		return null;
	}

	@Override
	public Object visitPrintStatement(PrintStatement printStatement, Object arg)
			throws Exception {
		sb.append(arg).append("PrintStatement").append('\n');
		String indent = arg + "  ";
		printStatement.expression.visit(this, indent);
		return null;
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		sb.append("Program\n");
		String indent = "  ";
		sb.append("class ").append(program.name).append('\n');
		program.block.visit(this, indent);
		sb.append('\n');
		return null;
	}

	@Override
	public Object visitSimpleType(SimpleType simpleType, Object arg) {
		sb.append(arg).append("SimpleType").append('\n');
		String indent = arg + "  ";
		sb.append(indent).append(simpleType.type.getText()).append('\n');
		return null;
	}

	@Override
	public Object visitStringLitExpression(
			StringLitExpression stringLitExpression, Object arg) {
		sb.append(arg).append("StringLitExpression").append('\n');
		String indent = arg + "  ";
		sb.append(indent).append(stringLitExpression.value).append('\n');
		return null;
	}

	@Override
	public Object visitUnaryExpression(UnaryExpression unaryExpression,
			Object arg) throws Exception {
		sb.append(arg).append("UnaryExpression").append('\n');
		String indent = arg + "  ";
		sb.append(indent).append(unaryExpression.op.getText()).append('\n');
		unaryExpression.expression.visit(this, indent);
		return null;
	}

	@Override
	public Object visitUndeclaredType(UndeclaredType undeclaredType, Object arg)
			throws Exception {
		return null;
	}

	@Override
	public Object visitVarDec(VarDec varDec, Object arg) throws Exception {
		sb.append(arg).append("VarDec").append('\n');
		String indent = arg + "  ";
		sb.append(indent).append(varDec.identToken.getText()).append('\n');
		varDec.type.visit(this, indent);
		return null;
	}
}
