package compiler.ndk.ast.visitor;

import compiler.ndk.ast.blockElems.BlockElem;
import compiler.ndk.ast.blockElems.declarations.VarDec;
import compiler.ndk.ast.expressions.*;
import compiler.ndk.ast.blocks.Block;
import compiler.ndk.ast.lValues.IdentLValue;
import compiler.ndk.ast.programs.Program;
import compiler.ndk.ast.blockElems.statements.*;
import compiler.ndk.ast.types.SimpleType;
import compiler.ndk.mindMapTree.Node;
import compiler.ndk.mindMapTree.Tree;

public class MindMapTreeVisitor implements ASTVisitor {

	private Tree tree;

	public MindMapTreeVisitor() {
	}

	public Tree getTree() {
		return this.tree;
	}

	@Override
	public Object visitAssignmentStatement(
			AssignmentStatement assignmentStatement, Object arg)
			throws Exception {
		Node node = (Node) arg;
		Node newNode =new Node("AssignmentStatement");
				node.appendChild(newNode);
		assignmentStatement.lvalue.visit(this, newNode);
		assignmentStatement.expression.visit(this, newNode);
		return null;
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression,
										Object arg) throws Exception {
		Node node = (Node) arg;
		Node newNode = new Node("BinaryExpression");
		node.appendChild(newNode);
		binaryExpression.expression0.visit(this, newNode);
		newNode.appendChild(new Node(binaryExpression.op.getText()));
		binaryExpression.expression1.visit(this, newNode);
		return null;
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg)
			throws Exception {
		Node node = (Node) arg;
		Node newNode = new Node("IfStatement");
		node.appendChild(newNode);
		ifStatement.block.visit(this, newNode);
		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		Node node = (Node) arg;
		Node newNode = new Node("Block");
		node.appendChild(newNode);
		for (BlockElem elem : block.elems) {
			elem.visit(this, newNode);
		}
		return null;
	}

	@Override
	public Object visitExpressionStatement(
			ExpressionStatement expressionStatement, Object arg)
			throws Exception {
		Node node = (Node) arg;
		Node newNode = new Node("ExpressionStatement");
				node.appendChild(newNode);
		expressionStatement.expression.visit(this, newNode);
		return null;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression,
									   Object arg) {
		Node node = (Node) arg;
		node.appendChild(new Node("IdentExpression: " + identExpression.identToken.getText()));
		return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identLValue, Object arg) {
		Node node = (Node) arg;
		node.appendChild(new Node("IdentLValue: " + identLValue.identToken.getText()));
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression,
										Object arg) {
		Node node = (Node) arg;
		node.appendChild(new Node("IntLitExpression: " + intLitExpression.value));
		return null;
	}

	@Override
	public Object visitPrintStatement(PrintStatement printStatement, Object arg)
			throws Exception {
		Node node = (Node) arg;
		Node newNode = new Node("PrintStatement");
		node.appendChild(newNode);
		printStatement.expression.visit(this, newNode);
		return null;
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		this.tree = new Tree("Program class " + program.name);
		program.block.visit(this, this.tree.getRoot());
		return null;
	}

	@Override
	public Object visitSimpleType(SimpleType simpleType, Object arg) {
		Node node = (Node) arg;
		node.appendChild(new Node("SimpleType: " + simpleType.type.getText()));
		return null;
	}

	@Override
	public Object visitStringLitExpression(
			StringLitExpression stringLitExpression, Object arg) {
		Node node = (Node) arg;
		node.appendChild(new Node("StringLitExpression: " + stringLitExpression.value));
		return null;
	}

	@Override
	public Object visitUnaryExpression(UnaryExpression unaryExpression,
									   Object arg) throws Exception {
		Node node = (Node) arg;
		Node newNode = new Node("UnaryExpression: " + unaryExpression.op.getText());
		node.appendChild(newNode);
		unaryExpression.expression.visit(this, newNode);
		return null;
	}

	@Override
	public Object visitVarDec(VarDec varDec, Object arg) throws Exception {
		Node node = (Node) arg;
		Node newNode = new Node("VarDec: " + varDec.identToken.getText());
		node.appendChild(newNode);
		varDec.type.visit(this, newNode);
		return null;
	}

}
