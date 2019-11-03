package compiler.ndk.ast.visitor;

import static compiler.ndk.lexer.TokenStream.Kind.*;

import compiler.ndk.lexer.TokenStream.Kind;
import compiler.ndk.codebuilder.TypeConstants;
import compiler.ndk.ast.blockElems.BlockElem;
import compiler.ndk.ast.expressions.*;
import compiler.ndk.ast.blocks.Block;
import compiler.ndk.ast.programs.Program;
import compiler.ndk.ast.blockElems.statements.*;
import org.objectweb.asm.*;

public class CodeGenVisitor implements ASTVisitor, Opcodes, TypeConstants {

	private ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
	//private TraceClassVisitor cw = new TraceClassVisitor(new PrintWriter(System.out));

	private FieldVisitor fv;
	private String className;
	private String classDescriptor;

	static class InheritedAttributes {
		InheritedAttributes(MethodVisitor mv, String varName) {
			super();
			this.mv = mv;
			this.listName = varName;
		}

		MethodVisitor mv;
		String listName;
	}
	
	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression,
                                        Object arg) throws Exception {
		MethodVisitor mv = ((InheritedAttributes) arg).mv;
		Kind op = binaryExpression.op.kind;		

		if ((op == PLUS || op == MINUS || op == MUL || op == DIV)) {
			binaryExpression.expression0.visit(this,arg);
			binaryExpression.expression1.visit(this,arg);
			switch(op) {
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
		}
		else {
			throw new UnsupportedOperationException("code generation not yet implemented");
		}
		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		for (BlockElem elem : block.elems) {
			elem.visit(this, arg);
		}
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression,
			Object arg) throws Exception {
		MethodVisitor mv = ((InheritedAttributes) arg).mv; 
		mv.visitLdcInsn(intLitExpression.value);
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
		classDescriptor = 'L' + className + ';';
		cw.visit(52,
				ACC_PUBLIC + ACC_SUPER,
				className,
				null,
				"java/lang/Object",
				new String[] { "compiler/ndk/codebuilder/Codelet" }
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

		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "execute",
				"()V",
				null,
				null
				);
		mv.visitCode();
		Label lbeg = new Label();
		mv.visitLabel(lbeg);
		mv.visitLineNumber(program.firstToken.lineNumber, lbeg);
		String listName = null;
		program.block.visit(this, new InheritedAttributes(mv, listName));

		mv.visitInsn(RETURN);
		Label lend = new Label();
		mv.visitLabel(lend);
		mv.visitLocalVariable("this", classDescriptor, null, lbeg, lend, 0);
		mv.visitMaxs(0, 0);
		mv.visitEnd();		
		cw.visitEnd();
		return cw.toByteArray();
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
		String etype = "I";
		if (etype.equals("I")) {
			String desc = "(" + etype + ")V";
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
					desc, false);
		} else
			throw new UnsupportedOperationException(
					"printing list or map not yet implemented");
		return null;
	}

}
