package compiler.ndk.ast.visitor;

import compiler.ndk.ast.blockElems.declarations.VarDec;
import compiler.ndk.ast.expressions.*;
import compiler.ndk.ast.blocks.Block;
import compiler.ndk.ast.lValues.IdentLValue;
import compiler.ndk.ast.programs.Program;
import compiler.ndk.ast.blockElems.statements.*;
import compiler.ndk.ast.types.SimpleType;

public interface ASTVisitor {
    Object visitAssignmentStatement(AssignmentStatement assignmentStatement, Object arg) throws Exception;
    Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception;
    Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg)throws Exception;
    Object visitBlock(Block block, Object arg) throws Exception;
    Object visitExpressionStatement(ExpressionStatement expressionStatement, Object arg) throws Exception;
    Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception;
    Object visitIdentLValue(IdentLValue identLValue, Object arg) throws Exception;
    Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception;
    Object visitPrintStatement(PrintStatement printStatement, Object arg) throws Exception;
    Object visitProgram(Program program, Object arg) throws Exception;
    Object visitSimpleType(SimpleType simpleType, Object arg) throws Exception;
    Object visitStringLitExpression(StringLitExpression stringLitExpression, Object arg) throws Exception;
    Object visitUnaryExpression(UnaryExpression unaryExpression, Object arg) throws Exception;
    Object visitVarDec(VarDec varDec, Object arg) throws Exception;
    Object visitIfStatement(IfStatement ifStatement, Object arg)throws Exception;
}
