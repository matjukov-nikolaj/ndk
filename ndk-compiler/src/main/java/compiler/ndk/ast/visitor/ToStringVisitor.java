package compiler.ndk.ast.visitor;

import compiler.ndk.ast.blockElems.BlockElem;
import compiler.ndk.ast.blockElems.declarations.VarDec;
import compiler.ndk.ast.expressions.*;
import compiler.ndk.ast.blocks.Block;
import compiler.ndk.ast.lValues.IdentLValue;
import compiler.ndk.ast.programs.Program;
import compiler.ndk.ast.blockElems.statements.*;
import compiler.ndk.ast.types.SimpleType;

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
		String tabs = arg + "\t";
		sb.append(tabs + "{\n" +
				tabs + "\t\"title\":");
		sb.append("\"AssignmentStatement\",").append('\n');
		sb.append(tabs + "\t\"children\": [\n");
		String indent = tabs + "\t";
		assignmentStatement.lvalue.visit(this, indent);
		assignmentStatement.expression.visit(this, indent);
		sb.append(tabs + "\t]\n");
		sb.append(tabs + "},\n");
		return null;
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression,
										Object arg) throws Exception {
		String tabs = arg + "\t";
		sb.append(tabs + "{\n" +
				tabs + "\t\"title\":");
		sb.append("\"BinaryExpression\",").append('\n');
		sb.append(tabs + "\t\"children\": [\n");
		String indent = tabs + "\t";

		binaryExpression.expression0.visit(this, indent);
		sb.append("\n");
		sb.append(tabs + "\t\t{\n" +
				tabs + "\t\t\t\"title\":");
		sb.append("\"" + binaryExpression.op.getText() + "\",").append('\n');
		sb.append(tabs + "\t\t\t\"children\": []\n");
		sb.append(tabs + "\t\t},\n");

		binaryExpression.expression1.visit(this, indent);
		sb.append(tabs + "\t]\n");
		sb.append(tabs + "},");
		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		String tabs = arg + "\t";
		sb.append(tabs + "{\n" +
				tabs + "\t\"title\":");
		sb.append("\"Block\",").append('\n');
		sb.append(tabs + "\t\"children\": [\n");
		String indent = tabs + "\t";
		for (BlockElem elem : block.elems) {
			elem.visit(this, indent);
		}
		sb.append(tabs + "\t]\n");
		sb.append(tabs + "}\n");
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

		String tabs = arg + "\t";
		sb.append(tabs + "{\n" +
				tabs + "\t\"title\":");
		sb.append("\"IdentExpression\",").append('\n');
		sb.append(tabs + "\t\"children\": [\n");
		sb.append(tabs + "\t\t{\n" +
				tabs + "\t\t\t\"title\":");
		sb.append("\"" + identExpression.identToken.getText() + "\",").append('\n');
		sb.append(tabs + "\t\t\t\"children\": []\n");
		sb.append(tabs + "\t\t}\n");
		sb.append(tabs + "\t]\n");
		sb.append(tabs + "},\n");
		return null;


	}

	@Override
	public Object visitIdentLValue(IdentLValue identLValue, Object arg) {
		sb.append(arg + "\t{\n");
		String indent = arg + "\t\t";
		sb.append(indent + "\"title\":");
		sb.append("\"IdentLValue\",").append('\n');
		sb.append(indent + "\"children\": [\n");
		sb.append(indent + "\t{\n");
		indent = indent + "\t\t";
		sb.append(indent + "\"title\":");
		sb.append("\"" + identLValue.identToken.getText() + "\",").append('\n');
		sb.append(indent + "\"children\": []\n");
		sb.append(arg + "\t\t\t}\n");
		sb.append(arg + "\t\t]\n");
		sb.append(arg + "\t},\n");
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression,
										Object arg) {
		String tabs = arg + "\t";
		sb.append(tabs + "{\n" +
				tabs + "\t\"title\":");
		sb.append("\"IntLitExpression\",").append('\n');
		sb.append(tabs + "\t\"children\": [\n");
		sb.append(tabs + "\t\t{\n" +
				tabs + "\t\t\t\"title\":");
		sb.append("\"" + intLitExpression.value + "\",").append('\n');
		sb.append(tabs + "\t\t\t\"children\": []\n");
		sb.append(tabs + "\t\t}\n");
		sb.append(tabs + "\t]\n");
		sb.append(tabs + "},\n");
		return null;
	}

	@Override
	public Object visitPrintStatement(PrintStatement printStatement, Object arg)
			throws Exception {
		String tabs = arg + "\t";
		sb.append(tabs + "{\n" +
				tabs + "\t\"title\":");
		sb.append("\"PrintStatement\",").append('\n');
		sb.append(tabs + "\t\"children\": [\n");
		String indent = tabs + "\t";
		printStatement.expression.visit(this, indent);
		sb.append(tabs + "\t]\n");
		sb.append(tabs + "},\n");
		return null;
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		sb.append("{\"root\":\n" +
				"\t{\n" +
				"\t\t\"title\": \"Program ");
		String indent = "\t\t";
		sb.append("class ").append(program.name).append("\",\n");
		sb.append("\t\t\"children\": [\n");
		program.block.visit(this, indent);
		sb.append("\t\t]\n\t}\n}");
		sb.append('\n');
		return null;
	}

	@Override
	public Object visitSimpleType(SimpleType simpleType, Object arg) {
		sb.append(arg + "\t\t{\n");
		String indent = arg + "\t\t\t";
		sb.append(indent + "\"title\":");
		sb.append("\"SimpleType\",").append('\n');
		sb.append(indent + "\"children\": [\n");
		sb.append(indent + "\t{\n");
		indent = indent + "\t\t";
		sb.append(indent + "\"title\":");
		sb.append("\"" + simpleType.type.getText() + "\",").append('\n');
		sb.append(indent + "\"children\": []\n");
		sb.append(arg + "\t\t\t\t}\n");
		sb.append(arg + "\t\t\t]\n");
		sb.append(arg + "\t\t}\n");
		return null;
	}

	@Override
	public Object visitUnaryExpression(UnaryExpression unaryExpression,
									   Object arg) throws Exception {
		String tabs = arg + "\t";
		sb.append(tabs + "{\n" +
				tabs + "\t\"title\":");
		sb.append("\"UnaryExpression\",").append('\n');
		sb.append(tabs + "\t\"children\": [\n");
		sb.append(tabs + "\t\t{\n" +
				tabs + "\t\t\t\"title\":");
		sb.append("\"" + unaryExpression.op.getText() + "\",").append('\n');
		sb.append(tabs + "\t\t\t\"children\": []\n");
		sb.append(tabs + "\t\t},\n");
		String indent = arg + "\t";
		unaryExpression.expression.visit(this, indent);
		sb.append(tabs + "\t]\n");
		sb.append(tabs + "},\n");
		return null;
	}

	@Override
	public Object visitVarDec(VarDec varDec, Object arg) throws Exception {
		String tabs = arg + "\t";
		sb.append(tabs + "{\n" +
				tabs + "\t\"title\":");
		sb.append("\"VarDec\",").append('\n');
		sb.append(tabs + "\t\"children\": [\n");
		sb.append(tabs + "\t\t{\n" +
				tabs + "\t\t\t\"title\":");
		sb.append("\"" + varDec.identToken.getText() + "\",").append('\n');
		sb.append(tabs + "\t\t\t\"children\": []\n");
		sb.append(tabs + "\t\t},\n");
		String indent = arg + "\t";
		varDec.type.visit(this, indent);
		sb.append(tabs + "\t]\n");
		sb.append(tabs + "},\n");
		return null;
	}

	@Override
	public Object visitStringLitExpression(
			StringLitExpression stringLitExpression, Object arg) {
		String tabs = arg + "\t";
		sb.append(tabs + "{\n" +
				tabs + "\t\"title\":");
		sb.append("\"StringLitExpression\",").append('\n');
		sb.append(tabs + "\t\"children\": [\n");
		sb.append(tabs + "\t\t{\n" +
				tabs + "\t\t\t\"title\":");
		sb.append("\"" + stringLitExpression.value + "\",").append('\n');
		sb.append(tabs + "\t\t\t\"children\": []\n");
		sb.append(tabs + "\t\t}\n");
		sb.append(tabs + "\t]\n");
		sb.append(tabs + "},\n");
		return null;
	}

}
