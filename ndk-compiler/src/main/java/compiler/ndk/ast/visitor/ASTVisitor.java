package compiler.ndk.ast.visitor;

import compiler.ndk.ast.expressions.*;
import compiler.ndk.ast.blocks.Block;
import compiler.ndk.ast.lValues.ExpressionLValue;
import compiler.ndk.ast.lValues.IdentLValue;
import compiler.ndk.ast.programs.Program;
import compiler.ndk.ast.qualifiedNames.QualifiedName;
import compiler.ndk.ast.blockElems.statements.*;

public interface ASTVisitor {

	Object visitAssignmentStatement(AssignmentStatement assignmentStatement,
									Object arg)throws Exception;
	Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg)throws Exception;
	Object visitBlock(Block block, Object arg) throws Exception;
	Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression,
									 Object arg)throws Exception;
	Object visitExpressionLValue(ExpressionLValue expressionLValue, Object arg)throws Exception;
	Object visitExpressionStatement(ExpressionStatement expressionStatement,
			Object arg)throws Exception;
	Object visitIdentExpression(IdentExpression identExpression, Object arg)throws Exception;
	Object visitIdentLValue(IdentLValue identLValue, Object arg)throws Exception;
	Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg)throws Exception;
	Object visitKeyExpression(KeyExpression keyExpression, Object arg)throws Exception;
	Object visitKeyValueExpression(KeyValueExpression keyValueExpression,
			Object arg)throws Exception;
	Object visitListExpression(ListExpression listExpression, Object arg)throws Exception;
	Object visitListElemExpression(
			ListElemExpression listElemExpression, Object arg)throws Exception;
	Object visitMapListExpression(MapListExpression mapListExpression,
			Object arg)throws Exception;
	Object visitPrintStatement(PrintStatement printStatement, Object arg)throws Exception;
	Object visitProgram(Program program, Object arg) throws Exception;
	Object visitQualifiedName(QualifiedName qualifiedName, Object arg);
	Object visitRangeExpression(RangeExpression rangeExpression, Object arg)throws Exception;
	Object visitSizeExpression(SizeExpression sizeExpression, Object arg)throws Exception;
	Object visitStringLitExpression(StringLitExpression stringLitExpression,
			Object arg)throws Exception;
	Object visitUnaryExpression(UnaryExpression unaryExpression, Object arg)throws Exception;
	Object visitValueExpression(ValueExpression valueExpression, Object arg) throws Exception;
}
