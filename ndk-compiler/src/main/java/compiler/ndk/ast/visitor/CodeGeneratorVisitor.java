package compiler.ndk.ast.visitor;

import static compiler.ndk.lexer.TokenStream.Kind.*;

import compiler.ndk.ast.lValues.ExpressionLValue;
import compiler.ndk.ast.types.ListType;
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
        String listName;
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
        if (varType.equals(intType) || varType.equals(stringType) || varType.equals(booleanType)) {
            mv.visitLocalVariable(varName, varType, null, ((InheritedAttributes) arg).start, ((InheritedAttributes) arg).end, varDec.getSlot());
        } else if (varType.substring(0, varType.indexOf("<")).equals("Ljava/util/List")) {
            String listType = null;
            if (varType == emptyList) {
                listType = "Ljava/util/List;";
            } else {
                listType = getElementType(varType);
            }
            {
                mv.visitLocalVariable(varName, listType, null, ((InheritedAttributes) arg).start, ((InheritedAttributes) arg).end, varDec.getSlot());
                mv.visitEnd();
            }
        }
        return null;
    }

    private String getElementType(String elementType) {
        if (elementType.equals(intType)) {
            return "Ljava/lang/Integer;";
        } else if (elementType.equals(booleanType)) {
            return "Ljava/lang/Boolean;";
        } else if (elementType.equals(stringType)) {
            return "Ljava/lang/String;";
        } else if (elementType.substring(0, elementType.indexOf("<")).equals("Ljava/util/List")){
            String innerType = elementType.substring(elementType.indexOf("<") + 1, elementType.lastIndexOf(">"));
            return "Ljava/util/List<" + getElementType(innerType) + ">;";
        } else {
            throw new UnsupportedOperationException("code generation not yet implemented");
        }
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
        if (varType.equals(booleanType)) {
            if (assignmentStatement.lvalue instanceof IdentLValue) {
                mv.visitVarInsn(ISTORE, ((IdentLValue) assignmentStatement.lvalue).dec.getSlot());
            }
        }
        if (varType.equals(stringType)) {
            if (assignmentStatement.lvalue instanceof IdentLValue) {
                mv.visitVarInsn(ASTORE, ((IdentLValue) assignmentStatement.lvalue).dec.getSlot());
            }
        }
        if (varType.substring(0, varType.indexOf("<")).equals("Ljava/util/List")) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitTypeInsn(NEW, "java/util/ArrayList");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
            mv.visitFieldInsn(PUTFIELD, className, varName, "Ljava/util/List;");
            ((InheritedAttributes) arg).listName = varName;
            if (assignmentStatement.expression instanceof ListElemExpression) {
                mv.visitVarInsn(ALOAD, 0);
                assignmentStatement.expression.visit(this, arg);
                mv.visitFieldInsn(PUTFIELD, className, varName, "Ljava/util/List;");
            } else {
                assignmentStatement.expression.visit(this, arg);
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
    public Object visitWhileStatement(WhileStatement whileStatement, Object arg)
            throws Exception {
        MethodVisitor mv = ((InheritedAttributes) arg).mv;
        Label l1 = new Label();
        mv.visitLabel(l1);
        whileStatement.expression.visit(this, arg);
        Label l2 = new Label();
        mv.visitJumpInsn(IFEQ, l2);
        whileStatement.block.visit(this, arg);
        mv.visitJumpInsn(GOTO, l1);
        mv.visitLabel(l2);
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
        } else if (op == EQUAL) {
            binaryExpression.expression0.visit(this,arg);
            binaryExpression.expression1.visit(this,arg);
            Label le1 = new Label();
            if(exprType.equals(booleanType) || exprType.equals(intType)) {
                mv.visitJumpInsn(IF_ICMPNE, le1);
            }
            else if(exprType.equals(stringType)) {
                mv.visitJumpInsn(IF_ACMPNE, le1);
            }
            mv.visitInsn(ICONST_1);
            Label le2 = new Label();
            mv.visitJumpInsn(GOTO, le2);
            mv.visitLabel(le1);
            mv.visitInsn(ICONST_0);
            mv.visitLabel(le2);
        } else if (op == NOTEQUAL) {
            binaryExpression.expression0.visit(this,arg);
            binaryExpression.expression1.visit(this,arg);
            Label l1 = new Label();
            if(exprType.equals(booleanType) || exprType.equals(intType)) {
                mv.visitJumpInsn(IF_ICMPEQ, l1);
            }
            else if(exprType.equals(stringType)) {
                mv.visitJumpInsn(IF_ACMPEQ, l1);
            }
            mv.visitInsn(ICONST_1);
            Label l2 = new Label();
            mv.visitJumpInsn(GOTO, l2);
            mv.visitLabel(l1);
            mv.visitInsn(ICONST_0);
            mv.visitLabel(l2);
        } else if (op ==  LESS_THAN || op == GREATER_THAN || op == LESS_EQUAL || op == GREATER_EQUAL) {
            binaryExpression.expression0.visit(this,arg);
            binaryExpression.expression1.visit(this,arg);
            Label l1 = new Label();
            switch(op) {
                case LESS_THAN:
                    mv.visitJumpInsn(IF_ICMPGE, l1);
                    break;
                case GREATER_THAN:
                    mv.visitJumpInsn(IF_ICMPLE, l1);
                    break;
                case LESS_EQUAL:
                    mv.visitJumpInsn(IF_ICMPGT, l1);
                    break;
                case GREATER_EQUAL:
                    mv.visitJumpInsn(IF_ICMPLT, l1);
                    break;
                default:
                    throw new UnsupportedOperationException("code generation not yet implemented");
            }
            mv.visitInsn(ICONST_1);
            Label l2 = new Label();
            mv.visitJumpInsn(GOTO, l2);
            mv.visitLabel(l1);
            mv.visitInsn(ICONST_0);
            mv.visitLabel(l2);
        } else if (op == AND) {
            binaryExpression.expression0.visit(this, arg);
            Label l1 = new Label();
            mv.visitJumpInsn(IFEQ, l1);
            binaryExpression.expression1.visit(this,arg);
            mv.visitJumpInsn(IFEQ, l1);
            mv.visitInsn(ICONST_1);
            Label l2 = new Label();
            mv.visitJumpInsn(GOTO, l2);
            mv.visitLabel(l1);
            mv.visitInsn(ICONST_0);
            mv.visitLabel(l2);
        } else if (op == OR) {
            binaryExpression.expression0.visit(this, arg);
            Label l1 = new Label();
            mv.visitJumpInsn(IFNE, l1);
            binaryExpression.expression1.visit(this,arg);
            mv.visitJumpInsn(IFNE, l1);
            mv.visitInsn(ICONST_0);
            Label l2 = new Label();
            mv.visitJumpInsn(GOTO, l2);
            mv.visitLabel(l1);
            mv.visitInsn(ICONST_1);
            mv.visitLabel(l2);
        }
        else {
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
        // Обьявление класса
        cw.visit(52,
                ACC_PUBLIC + ACC_SUPER,
                className,
                null,
                "java/lang/Object",
                new String[]{"compiler/ndk/codebuilder/CodeBuilder"}
        );
        cw.visitSource(null, null);

        // Обьявление контсруктора класса
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

        //Создание метода маин
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
        //Посещение тела главного метода, т е блока
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
    public Object visitIfElseStatement(IfElseStatement ifElseStatement, Object arg) throws Exception {
        MethodVisitor mv = ((InheritedAttributes) arg).mv;
        Label l1 = new Label();
        ifElseStatement.expression.visit(this, arg);
        mv.visitJumpInsn(IFEQ, l1);
        ifElseStatement.ifBlock.visit(this, arg);
        Label l2 = new Label();
        mv.visitJumpInsn(GOTO, l2);
        mv.visitLabel(l1);
        ifElseStatement.elseBlock.visit(this, arg);
        mv.visitLabel(l2);
        return null;
    }

    @Override
    public Object visitBooleanLitExpression(
            BooleanLitExpression booleanLitExpression, Object arg)
            throws Exception {
        MethodVisitor mv = ((InheritedAttributes) arg).mv;
        if (booleanLitExpression.value == true) {
            mv.visitInsn(ICONST_1);
        } else {
            mv.visitInsn(ICONST_0);
        }
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
        if (varType.equals(booleanType)) {
            mv.visitVarInsn(ILOAD, identExpression.dec.getSlot());
        }
        if (varType.equals(stringType)) {
            mv.visitVarInsn(ALOAD, identExpression.dec.getSlot());
        }

        if (varType.substring(0, varType.indexOf("<")).equals("Ljava/util/List")) {
            mv.visitFieldInsn(GETFIELD, className, varName, "Ljava/util/List;");
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
        if (etype.equals("I") || etype.equals("Ljava/lang/String;") || etype.equals("Z")) {
            String desc = "(" + etype + ")V";
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                    desc, false);
        }
        return null;
    }

    @Override
    public Object visitExpressionLValue(ExpressionLValue expressionLValue,
                                        Object arg) throws Exception {
        MethodVisitor mv = ((InheritedAttributes) arg).mv;
        String varName = expressionLValue.identToken.getText();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, className, varName, "Ljava/util/List;");
        expressionLValue.expression.visit(this, arg);

        return null;
    }

    @Override
    public Object visitListType(ListType listType, Object arg) throws Exception {
        return listType.getJVMType();
    }

    @Override
    public Object visitListElemExpression(
            ListElemExpression listOrMapElemExpression, Object arg)
            throws Exception {
        MethodVisitor mv = ((InheritedAttributes) arg).mv;
        String varName = listOrMapElemExpression.identToken.getText();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, className, varName, "Ljava/util/List;");
        listOrMapElemExpression.expression.visit(this, arg);
        String elementType = listOrMapElemExpression.getType();
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "get", "(I)Ljava/lang/Object;", true);
        if (elementType.equals(intType)) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
        } else if (elementType.equals(booleanType))	{
            mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
        } else if (elementType.equals(stringType)) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/String");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toString", "()Ljava/lang/String;", false);
        } else if (elementType.substring(0, elementType.indexOf("<")).equals("Ljava/util/List")) {
            mv.visitTypeInsn(CHECKCAST, "java/util/List");
        }
        return null;
    }

    @Override
    public Object visitListExpression(ListExpression listExpression, Object arg)
            throws Exception {
        MethodVisitor mv = ((InheritedAttributes) arg).mv;
        String listName = ((InheritedAttributes) arg).listName;
        String elementType = null;
        if (!listExpression.expressionList.isEmpty()) {
            String listType = listExpression.getType();
            elementType = listType.substring(listType.indexOf("<") + 1, listType.indexOf(">"));
        }
        for (Expression e : listExpression.expressionList) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, className, listName, "Ljava/util/List;");
            e.visit(this, arg);
            if (elementType.equals(intType)) {
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            } else if (elementType.equals(booleanType))	{
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
            } else if (elementType.equals(stringType)) {
                // nothing to do
            } else if (elementType.substring(0, elementType.indexOf("<")).equals("Ljava/util/List")) {
//				mv.visitVarInsn(ALOAD, 1);
            }
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
            mv.visitInsn(POP);
        }
        return null;
    }
}
