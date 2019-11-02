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

	// Because we used the COMPUTE_FRAMES flag, we do not need to
	// insert the mv.visitFrame calls that you will see in some of the
	// asmifier examples. ASM will insert those for us.
	// FYI, the purpose of those instructions is to provide information
	// about what is on the stack just before each branch target in order
	// to speed up class verification.
	private FieldVisitor fv;
	private String className;
	private String classDescriptor;
	// This class holds all attributes that need to be passed downwards as the
	// AST is traversed. Initially, it only holds the current MethodVisitor.
	// Later, we may add more attributes.
	static class InheritedAttributes {
		InheritedAttributes(MethodVisitor mv, String varName) {
			super();
			this.mv = mv;
			this.listName = varName;
		}

		MethodVisitor mv;
		String listName;
	}
	
	private String getElementType(String elementType) {
		if (elementType.equals(intType)) {
			return "Ljava/lang/Integer;";
		} else if (elementType.equals(booleanType)) {
			return "Ljava/lang/Boolean;";	
		} else if (elementType.equals(stringType)) {
			return "Ljava/lang/String;";		
		} else if (elementType.substring(0, elementType.indexOf("<")).equals(listType)){
			String innerType = elementType.substring(elementType.indexOf("<") + 1, elementType.lastIndexOf(">"));
			return "Ljava/util/List<" + getElementType(innerType) + ">;";
		} else {
			throw new UnsupportedOperationException("code generation not yet implemented");
		}
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
		// this should be the first statement of all visit methods that generate instructions
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
		cw.visit(52, // version
				ACC_PUBLIC + ACC_SUPER, // access codes
				className, // fully qualified classname
				null, // signature
				"java/lang/Object", // superclass
				new String[] { "compiler/ndk/codebuilder/Codelet" } // implemented interfaces
		);
		cw.visitSource(null, null); // maybe replace first argument with source
									// file name

		// create init method
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

		// generate the execute method
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "execute", // name of top
																	// level
																	// method
				"()V", // descriptor: this method is parameterless with no
						// return value
				null, // signature.  This is null for us, it has to do with generic types
				null // array of strings containing exceptions
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
		mv.visitMaxs(0, 0);  //this is required just before the end of a method. 
		                     //It causes asm to calculate information about the
		                     //stack usage of this method.
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
		printStatement.expression.visit(this, arg); // adds code to leave value
													// of expression on top of
													// stack.
													// Unless there is a good
													// reason to do otherwise,
													// pass arg down the tree
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
