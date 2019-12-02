package compiler.ndk.ast.visitor;

import static compiler.ndk.lexer.TokenStream.Kind.*;

import compiler.ndk.lexer.TokenStream.Kind;
import compiler.ndk.codebuilder.TypeConstants;
import compiler.ndk.ast.blockElems.BlockElem;
import compiler.ndk.ast.blockElems.declarations.VarDec;
import compiler.ndk.ast.expressions.*;
import compiler.ndk.ast.blocks.Block;
import compiler.ndk.ast.lValues.IdentLValue;
import compiler.ndk.ast.programs.Program;
import compiler.ndk.ast.blockElems.statements.*;
import compiler.ndk.ast.types.SimpleType;
import org.objectweb.asm.*;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;

public class CodeGeneratorVisitor implements ASTVisitor, Opcodes, TypeConstants {

    private ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
//    private TraceClassVisitor cw = new TraceClassVisitor(new PrintWriter(System.out));
    private String className;
    int slot = 1;

    static class InheritedAttributes {
        InheritedAttributes(MethodVisitor mv) {
            super();
            this.mv = mv;
        }
        MethodVisitor mv;
        Label start;
        Label end;
    }

    @Override
    public Object visitVarDec(VarDec varDec, Object arg) throws Exception {
        MethodVisitor mv = ((InheritedAttributes) arg).mv;
        String varName = varDec.identToken.getText();
        String varType = (String) varDec.type.visit(this, arg);
        varDec.setSlot(slot);
        slot++;
        if (varType.equals(intType) || varType.equals(stringType)) {
            mv.visitLocalVariable(varName, varType, null, ((InheritedAttributes) arg).start, ((InheritedAttributes) arg).end, varDec.getSlot());
        }
        return null;
    }

    @Override
    public Object visitAssignmentStatement(
            AssignmentStatement assignmentStatement, Object arg)
            throws Exception {
        MethodVisitor mv = ((InheritedAttributes) arg).mv;
        String varName = (String) assignmentStatement.lvalue.visit(this, arg);
        String varType = assignmentStatement.lvalue.getType();
        assignmentStatement.expression.visit(this, arg);
        if (varType.equals(intType)) {
            if (assignmentStatement.lvalue instanceof IdentLValue) {
                mv.visitVarInsn(ISTORE, ((IdentLValue) assignmentStatement.lvalue).dec.getSlot());
            }
        }
        if (varType.equals(stringType)) {
            if (assignmentStatement.lvalue instanceof IdentLValue) {
                mv.visitVarInsn(ASTORE, ((IdentLValue) assignmentStatement.lvalue).dec.getSlot());
            }
        }
        return null;
    }

    @Override
    public Object visitIdentLValue(IdentLValue identLValue, Object arg)
            throws Exception {
        return identLValue.identToken.getText();
    }

    @Override
    public Object visitUnaryExpression(UnaryExpression unaryExpression,
                                       Object arg) throws Exception {
        MethodVisitor mv = ((InheritedAttributes) arg).mv;
        unaryExpression.expression.visit(this, arg);
        if (unaryExpression.op.kind == NOT) {
            Label l1 = new Label();
            mv.visitJumpInsn(IFEQ, l1);
            mv.visitInsn(ICONST_0);
            Label l2 = new Label();
            mv.visitJumpInsn(GOTO, l2);
            mv.visitLabel(l1);
            mv.visitInsn(ICONST_1);
            mv.visitLabel(l2);
        } else if (unaryExpression.op.kind == MINUS) {
            mv.visitInsn(INEG);
        }
        return null;
    }

    @Override
    public Object visitBinaryExpression(BinaryExpression binaryExpression,
                                        Object arg) throws Exception {
        MethodVisitor mv = ((InheritedAttributes) arg).mv;
        String exprType = binaryExpression.expression0.getType();
        Kind op = binaryExpression.op.kind;

        if (op == PLUS && exprType.equals(stringType)) {
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            binaryExpression.expression0.visit(this, arg);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false);
            binaryExpression.expression1.visit(this, arg);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        } else if ((op == PLUS || op == MINUS || op == MUL || op == DIV) && exprType.equals(intType)) {
            binaryExpression.expression0.visit(this, arg);
            binaryExpression.expression1.visit(this, arg);
            switch (op) {
                case PLUS:
                    mv.visitInsn(IADD);
                    break;
                case MINUS:
                    mv.visitInsn(ISUB);
                    break;
                case MUL:
                    mv.visitInsn(IMUL);
                    break;
                case DIV:
                    mv.visitInsn(IDIV);
                    break;
                default:
                    throw new UnsupportedOperationException("code generation not yet implemented");
            }
        } else {
            throw new UnsupportedOperationException("code generation not yet implemented");
        }
        return null;
    }

    @Override
    public Object visitBlock(Block block, Object arg) throws Exception {
        MethodVisitor mv = ((InheritedAttributes) arg).mv;
        Label start = new Label();
        Label end = new Label();
        ((InheritedAttributes) arg).start = start;
        ((InheritedAttributes) arg).end = end;

        //add all variables declaration first
        for (BlockElem elem : block.elems) {
            if (elem instanceof VarDec) {
                elem.visit(this, arg);
            }
        }

        mv.visitLabel(start);
        for (BlockElem elem : block.elems) {
            if (!(elem instanceof VarDec)) {
                elem.visit(this, arg);
            }
        }
        mv.visitLabel(end);
        return null;
    }

    @Override
    public Object visitSimpleType(SimpleType simpleType, Object arg)
            throws Exception {
        return simpleType.getJVMType();
    }

    @Override
    public Object visitIntLitExpression(IntLitExpression intLitExpression,
                                        Object arg) throws Exception {
        MethodVisitor mv = ((InheritedAttributes) arg).mv;
        mv.visitLdcInsn(intLitExpression.value);
        return null;
    }

    @Override
    public Object visitStringLitExpression(
            StringLitExpression stringLitExpression, Object arg)
            throws Exception {
        MethodVisitor mv = ((InheritedAttributes) arg).mv;
        mv.visitLdcInsn(stringLitExpression.value);
        return null;
    }

    @Override
    public Object visitExpressionStatement(
            ExpressionStatement expressionStatement, Object arg)
            throws Exception {
        expressionStatement.expression.visit(this, arg);

        throw new UnsupportedOperationException(
                "code generation not yet implemented");
    }

    @Override
    public Object visitProgram(Program program, Object arg) throws Exception {
        className = program.JVMName;
        String classDescriptor = 'L' + className + ';';
        cw.visit(52,
                ACC_PUBLIC + ACC_SUPER,
                className,
                null,
                "java/lang/Object",
                new String[]{"compiler/ndk/codebuilder/CodeBuilder"}
        );
        cw.visitSource(null, null);


        {
            MethodVisitor mv;
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(3, l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>",
                    "()V", false);
            mv.visitInsn(RETURN);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLocalVariable("this", classDescriptor, null, l0, l1, 0);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }

        MethodVisitor mv  = cw.visitMethod(ACC_PUBLIC,
                "main",
                "()V",
                null,
                null
        );
        mv.visitCode();
        Label lbeg = new Label();
        mv.visitLabel(lbeg);
        mv.visitLineNumber(program.firstToken.lineNumber, lbeg);
        program.block.visit(this, new InheritedAttributes(mv));
        mv.visitInsn(RETURN);
        Label lend = new Label();
        mv.visitLabel(lend);
        mv.visitLocalVariable("this", classDescriptor, null, lbeg, lend, 0);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
        cw.visitEnd();
        return cw.toByteArray();
    }

    @Override
    public Object visitIfStatement(IfStatement ifStatement, Object arg)
            throws Exception {
        MethodVisitor mv = ((InheritedAttributes) arg).mv;
        Label l1 = new Label();
        ifStatement.expression.visit(this, arg);
        mv.visitJumpInsn(IFEQ, l1);
        ifStatement.block.visit(this, arg);
        mv.visitLabel(l1);
        return null;
    }

    @Override
    public Object visitBooleanLitExpression(
            BooleanLitExpression booleanLitExpression, Object arg)
            throws Exception {
        MethodVisitor mv = ((InheritedAttributes) arg).mv;
        mv.visitInsn(ICONST_1);
        return null;
    }

    @Override
    public Object visitIdentExpression(IdentExpression identExpression,
                                       Object arg) throws Exception {
        String varName = identExpression.identToken.getText();
        String varType = identExpression.getType();
        MethodVisitor mv = ((InheritedAttributes) arg).mv;
//        mv.visitVarInsn(ALOAD, 0);
        if (varType.equals(intType)) {
            mv.visitVarInsn(ILOAD, identExpression.dec.getSlot());
        }
        if (varType.equals(stringType)) {
            mv.visitVarInsn(ALOAD, identExpression.dec.getSlot());
        }

        return null;
    }

    @Override
    public Object visitPrintStatement(PrintStatement printStatement, Object arg)
            throws Exception {
        MethodVisitor mv = ((InheritedAttributes) arg).mv;
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitLineNumber(printStatement.firstToken.getLineNumber(), l0);
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out",
                "Ljava/io/PrintStream;");
        printStatement.expression.visit(this, arg);
        String etype = printStatement.expression.getType();
        if (etype.equals("I") || etype.equals("Ljava/lang/String;")) {
            String desc = "(" + etype + ")V";
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                    desc, false);
        }
        return null;
    }

}
