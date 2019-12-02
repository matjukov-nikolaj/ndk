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

	@Override
	public Object visitAssignmentStatement(
			AssignmentStatement assignmentStatement, Object arg)
			throws Exception {
		String lvType = (String) assignmentStatement.lvalue.visit(this, arg);
		String exprType = (String) assignmentStatement.expression.visit(this, arg);
		if (lvType.equals(intType) || lvType.equals(stringType) || lvType.equals(booleanType)) {
			check(lvType.equals(exprType), "uncompatible assignment type", assignmentStatement);
		} else {
			throw new UnsupportedOperationException("Unsuppported type");
		}		
		return null;		
	}

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
		case EQUAL:	case NOTEQUAL:
			if (expr0Type.equals(booleanType) || expr0Type.equals(intType) ||expr0Type.equals(stringType)) {
				binaryExpression.setType(booleanType);
				return booleanType;
			} else {
				throw new TypeCheckException("operator " + op.toString() + " is not defined for " + expr0Type, binaryExpression);
			}
		case LESS_THAN: case GREATER_THAN: case LESS_EQUAL: case GREATER_EQUAL:
			if (expr0Type.equals(booleanType) || expr0Type.equals(intType)) {
				binaryExpression.setType(booleanType);
				return booleanType;
			} else {
				throw new TypeCheckException("operator " + op.toString() + " is not defined for " + expr0Type, binaryExpression);
			}
		default:
		throw new TypeCheckException("operator " + op.toString() + " is not defined for " + expr0Type, binaryExpression);
		} 	
		binaryExpression.setType(expr0Type);
		return expr0Type;
	}

	@Override
	public Object visitBooleanLitExpression(
			BooleanLitExpression booleanLitExpression, Object arg)
			throws Exception {
		booleanLitExpression.setType(booleanType);
		return booleanType;
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg)
			throws Exception {
		String condType = (String) ifStatement.expression.visit(this, arg);
		check(condType.equals(booleanType), "uncompatible If condition", ifStatement);
		ifStatement.block.visit(this, arg);
		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		int numScopes = symbolTable.enterScope();
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
			identExpression.dec = vd;
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
			identLValue.dec = vd;
			return lvType;
		} else {
			throw new TypeCheckException(ident + " is not defined as a variable", identLValue);
		}		
	}

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

	@Override
	public Object visitUnaryExpression(UnaryExpression unaryExpression,
			Object arg) throws Exception {
		String exprType = (String) unaryExpression.expression.visit(this, arg);
		if(unaryExpression.op.kind == NOT) {
			if(!exprType.equals(booleanType)) {
				throw new TypeCheckException("not operator is undefined for " + exprType, unaryExpression);
			}
		} else if (unaryExpression.op.kind == MINUS) {
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
	public Object visitVarDec(VarDec varDec, Object arg) throws Exception {
		String ident = varDec.identToken.getText();
		check(symbolTable.insert(ident, varDec), "redeclare VarDec", varDec);
		return null;
	}
}
