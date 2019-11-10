package compiler.ndk.ast.visitor;

import compiler.ndk.ast.blockElems.BlockElem;
import compiler.ndk.ast.blockElems.declarations.ClosureDec;
import compiler.ndk.ast.blockElems.declarations.VarDec;
import compiler.ndk.ast.expressions.*;
import compiler.ndk.ast.blocks.Block;
import compiler.ndk.ast.closures.Closure;
import compiler.ndk.ast.lValues.ExpressionLValue;
import compiler.ndk.ast.lValues.IdentLValue;
import compiler.ndk.ast.programs.Program;
import compiler.ndk.ast.qualifiedNames.QualifiedName;
import compiler.ndk.ast.blockElems.statements.*;
import compiler.ndk.ast.types.SimpleType;
import compiler.ndk.ast.types.UndeclaredType;

import java.util.Iterator;

public class ToStringVisitor implements ASTVisitor {

	private StringBuilder sb;

	ToStringVisitor() {
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
	public Object visitClosure(Closure closure, Object arg) throws Exception {
		sb.append(arg).append("Closure").append('\n');
		String indent = arg + "  ";
		for (VarDec dec : closure.formalArgList) {
			dec.visit(this, indent);
		}
		for (Statement statement : closure.statementList) {
			statement.visit(this, indent);
		}
		return null;
	}

	@Override
	public Object visitClosureDec(ClosureDec closureDeclaration, Object arg)
			throws Exception {
		sb.append(arg).append("ClosureDec").append('\n');
		String indent = arg + "  ";
		sb.append(indent).append(closureDeclaration.identToken.getText())
				.append('\n');
		;
		closureDeclaration.closure.visit(this, indent);
		return null;
	}

	@Override
	public Object visitClosureEvalExpression(
			ClosureEvalExpression closureExpression, Object arg)
			throws Exception {
		sb.append(arg).append("ClosureEvalExpression").append('\n');
		String indent = arg + "  ";
		sb.append(indent).append(closureExpression.identToken.getText())
				.append('\n');
		for (Expression e : closureExpression.expressionList) {
			e.visit(this, indent);
		}
		return null;
	}

	@Override
	public Object visitClosureExpression(ClosureExpression closureExpression,
										 Object arg) throws Exception {
		sb.append(arg).append("ClosureExpression").append('\n');
		String indent = arg + "  ";
		closureExpression.closure.visit(this, indent);
		return null;
	}

	@Override
	public Object visitExpressionLValue(ExpressionLValue expressionLValue,
										Object arg) throws Exception {
		sb.append(arg).append("ExpressionLValue").append('\n');
		String indent = arg + "  ";
		sb.append(indent).append(expressionLValue.identToken.getText())
				.append('\n');
		expressionLValue.expression.visit(this, indent);
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
		Iterator<QualifiedName> it = program.imports.iterator();
		while (it.hasNext()) {
			sb.append('\n').append(indent).append(it.next().name);
		}
		sb.append('\n');
		sb.append("class ").append(program.name).append('\n');
		program.block.visit(this, indent);
		sb.append('\n');
		return null;
	}

	@Override
	public Object visitQualifiedName(QualifiedName qualifiedName, Object arg) {
		assert false;
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
	public Object visitValueExpression(ValueExpression valueExpression,
			Object arg) throws Exception {
		sb.append(arg).append("ValueExpression").append('\n');
		String indent = arg + "  ";
		valueExpression.expression.visit(this, indent);
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
