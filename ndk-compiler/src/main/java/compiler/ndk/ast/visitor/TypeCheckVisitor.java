package compiler.ndk.ast.visitor;

import compiler.ndk.lexer.TokenStream.Kind;
import compiler.ndk.codebuilder.TypeConstants;
import compiler.ndk.ast.blockElems.BlockElem;
import compiler.ndk.ast.blockElems.declarations.Declaration;
import compiler.ndk.ast.blockElems.declarations.VarDec;
import compiler.ndk.ast.expressions.*;
import compiler.ndk.ast.blocks.Block;
import compiler.ndk.ast.lValues.IdentLValue;
import compiler.ndk.ast.programs.Program;
import compiler.ndk.ast.blockElems.statements.*;
import compiler.ndk.ast.types.SimpleType;
import compiler.ndk.ast.types.UndeclaredType;
import compiler.ndk.symbolTable.SymbolTable;
import static compiler.ndk.lexer.TokenStream.Kind.*;

public class TypeCheckVisitor implements ASTVisitor, TypeConstants {

	@SuppressWarnings("serial")
	public static class TypeCheckException extends Exception {
		ASTNode node;

		TypeCheckException(String message, ASTNode node) {
			super(node.firstToken.getText() + " " + node.firstToken.lineNumber + ":" + message);
			this.node = node;
		}
	}

	private SymbolTable symbolTable;

	public TypeCheckVisitor(SymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}

	private boolean check(boolean condition, String message, ASTNode node)
			throws TypeCheckException {
		if (condition)
			return true;
		throw new TypeCheckException(message, node);
	}

	/**
	 * Ensure that types on left and right hand side are compatible.
	 */
	@Override
	public Object visitAssignmentStatement(
			AssignmentStatement assignmentStatement, Object arg)
			throws Exception {
		String lvType = (String) assignmentStatement.lvalue.visit(this, arg);
		String exprType = (String) assignmentStatement.expression.visit(this, arg);
		if (lvType.equals(intType) || lvType.equals(stringType)) {
			check(lvType.equals(exprType), "uncompatible assignment type", assignmentStatement);
		} else {
			throw new UnsupportedOperationException("Unsuppported type");
		}		
		return null;		
	}

	/**
	 * Ensure that both types are the same, save and return the result type
	 *		int (+ | - | * | /) int 				-> int
	 *      string + string         				-> string
	 *      int (== | != | < | <= | >= | >) int     -> boolean
	 *      string (== | !=) string       			-> boolean
	 */
	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression,
                                        Object arg) throws Exception {
		String expr0Type = (String) binaryExpression.expression0.visit(this,arg);
		String expr1Type = (String) binaryExpression.expression1.visit(this,arg);
		Kind op = binaryExpression.op.kind;
		check(expr0Type.equals(expr1Type), "uncompatible bianry expression", binaryExpression);
		switch(op) {
		case PLUS:
			check(expr0Type.equals(intType) || expr0Type == stringType, "operator " + op.toString() + " is not defined for " + expr0Type, binaryExpression);
			break;
		case MINUS:	case MUL:	case DIV:
			check(expr0Type.equals(intType), "operator " + op.toString() + " is not defined for " + expr0Type, binaryExpression);
			break;
		default:
			throw new TypeCheckException("operator " + op.toString() + " is not defined for " + expr0Type, binaryExpression);
		} 	
		binaryExpression.setType(expr0Type);
		return expr0Type;
	}

	/**
	 * Blocks define scopes. Check that the scope nesting level is the same at
	 * the end as at the beginning of block
	 */
	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		int numScopes = symbolTable.enterScope();
		// visit children
		for (BlockElem elem : block.elems) {
			elem.visit(this, arg);
		}
		int numScopesExit = symbolTable.leaveScope();
		check(numScopesExit > 0 && numScopesExit == numScopes,
				"unbalanced scopes", block);
		return null;
	}

	@Override
	public Object visitExpressionStatement(
			ExpressionStatement expressionStatement, Object arg)
			throws Exception {
		expressionStatement.expression.visit(this, arg);
		return null;
	}

	/**
	 * Check that name has been declared in scope Get its type from the
	 * declaration.
	 * 
	 */
	@Override
	public Object visitIdentExpression(IdentExpression identExpression,
									   Object arg) throws Exception {
		String ident = identExpression.identToken.getText();
		Declaration dec = symbolTable.lookup(ident);
		check(dec != null, "undeclare IdentExpression", identExpression);
//		Declaration dec = symbolTable.lookup(ident);
		if (dec instanceof VarDec) {
			VarDec vd = (VarDec) dec;
			String identType = (String) vd.type.visit(this, arg);
			identExpression.setType(identType);
			return identType;
		} else {
			throw new TypeCheckException(ident + " is not defined as a variable", identExpression);
		}		
	}

	@Override
	public Object visitIdentLValue(IdentLValue identLValue, Object arg)
			throws Exception {
		String ident = identLValue.identToken.getText();
		Declaration dec = symbolTable.lookup(ident);
		check(dec != null, "undeclare IdentExpression", identLValue);
//		Declaration dec = symbolTable.lookup(ident);
		if (dec instanceof VarDec) {
			VarDec vd = (VarDec)dec;
			String lvType = (String) vd.type.visit(this, arg);
			identLValue.setType(lvType);
			return lvType;
		} else {
			throw new TypeCheckException(ident + " is not defined as a variable", identLValue);
		}		
	}

	/**
	 * expression type is int
	 */
	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression,
			Object arg) throws Exception {
		intLitExpression.setType(intType);
		return intType;
	}

	@Override
	public Object visitPrintStatement(PrintStatement printStatement, Object arg)
			throws Exception {
		printStatement.expression.visit(this, null);
		return null;
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		if (arg == null) {
			program.JVMName = program.name;
		} else {
			program.JVMName = arg + "/" + program.name;
		}
		// ignore the import statement
		if (!symbolTable.insert(program.name, null)) {
			throw new TypeCheckException("name already in symbol table",
					program);
		}
		program.block.visit(this, true);
		return null;
	}

	/**
	 * Checks that both expressions have type int.
	 * 
	 * Note that in spite of the name, this is not in the Expression type
	 * hierarchy.
	 */

	@Override
	public Object visitSimpleType(SimpleType simpleType, Object arg)
			throws Exception {
		return simpleType.getJVMType();
	}

	@Override
	public Object visitStringLitExpression(
			StringLitExpression stringLitExpression, Object arg)
			throws Exception {
		stringLitExpression.setType(stringType);
		return stringType;
	}

	/**
	 * if ! and boolean, then boolean else if - and int, then int else error
	 */
	@Override
	public Object visitUnaryExpression(UnaryExpression unaryExpression,
			Object arg) throws Exception {
		String exprType = (String) unaryExpression.expression.visit(this, arg);
		if (unaryExpression.op.kind == MINUS) {
			if (!exprType.equals(intType)){
				throw new TypeCheckException("minus operator is undefined for " + exprType, unaryExpression);
			}
		} else {			
			throw new TypeCheckException("uncompatible unary expression", unaryExpression);
		}		
		unaryExpression.setType(exprType);
		return exprType;
	}

	@Override
	public Object visitUndeclaredType(UndeclaredType undeclaredType, Object arg)
			throws Exception {
		throw new UnsupportedOperationException(
				"undeclared types not supported");
	}

	/**
	 * check that this variable has not already been declared in the same scope.
	 */
	@Override
	public Object visitVarDec(VarDec varDec, Object arg) throws Exception {
		String ident = varDec.identToken.getText();
		check(symbolTable.insert(ident, varDec), "redeclare VarDec", varDec);
		return null;
	}

	/**
	 * All checking will be done in the children since grammar ensures that the
	 * rangeExpression is a rangeExpression.
	 */
}
