package compiler.ndk.ast.visitor;

import static compiler.ndk.lexer.TokenStream.Kind.*;

import compiler.ndk.lexer.TokenStream.Kind;
import compiler.ndk.codebuilder.TypeConstants;
import compiler.ndk.ast.blockElems.BlockElem;
import compiler.ndk.ast.expressions.*;
import compiler.ndk.ast.blocks.Block;
import compiler.ndk.ast.lValues.ExpressionLValue;
import compiler.ndk.ast.lValues.IdentLValue;
import compiler.ndk.ast.programs.Program;
import compiler.ndk.ast.qualifiedNames.QualifiedName;
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
		if (varType.equals(intType) || varType.equals(booleanType) || varType.equals(stringType)) {
			if(assignmentStatement.lvalue instanceof ExpressionLValue) {
				assignmentStatement.expression.visit(this, arg);
				if (varType.equals(intType)) {
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
				} else if(varType.equals(booleanType)){
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
				} else if(varType.equals(stringType)) {
					// nothing to do
				} 
				mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(ILjava/lang/Object;)V", true);
			} else if (assignmentStatement.lvalue instanceof IdentLValue){
				mv.visitVarInsn(ALOAD, 0);
				assignmentStatement.expression.visit(this, arg);	
				mv.visitFieldInsn(PUTFIELD, className, varName, varType);
			}			
		}  else if (varType.substring(0, varType.indexOf("<")).equals("Ljava/util/List")) {
			mv.visitVarInsn(ALOAD, 0);
			mv.visitTypeInsn(NEW, "java/util/ArrayList");
			mv.visitInsn(DUP);
			mv.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
			mv.visitFieldInsn(PUTFIELD, className, varName, "Ljava/util/List;");
			((InheritedAttributes) arg).listName = varName;	
			if(assignmentStatement.expression instanceof ListElemExpression) {
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
	public Object visitUnaryExpression(UnaryExpression unaryExpression,
			Object arg) throws Exception {
		MethodVisitor mv = ((InheritedAttributes) arg).mv;
		unaryExpression.expression.visit(this, arg);
		if(unaryExpression.op.kind == NOT) {
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
	public Object visitStringLitExpression(
			StringLitExpression stringLitExpression, Object arg)
			throws Exception {
		MethodVisitor mv = ((InheritedAttributes) arg).mv; 
		mv.visitLdcInsn(stringLitExpression.value);
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
	public Object visitExpressionStatement(
			ExpressionStatement expressionStatement, Object arg)
			throws Exception {
		expressionStatement.expression.visit(this, arg);
		
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}	

	@Override
	public Object visitKeyExpression(KeyExpression keyExpression, Object arg)
			throws Exception {
		keyExpression.expression.visit(this, arg);
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitKeyValueExpression(
			KeyValueExpression keyValueExpression, Object arg) throws Exception {
		keyValueExpression.key.visit(this, arg);
		keyValueExpression.value.visit(this, arg);
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
	public Object visitQualifiedName(QualifiedName qualifiedName, Object arg) {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitRangeExpression(RangeExpression rangeExpression,
			Object arg) throws Exception {
		rangeExpression.lower.visit(this, arg);
		rangeExpression.upper.visit(this, arg);
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitValueExpression(ValueExpression valueExpression,
			Object arg) throws Exception {
		valueExpression.expression.visit(this, arg);
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression,
									   Object arg) throws Exception {
		String varName = identExpression.identToken.getText();	
		String varType = identExpression.getType();
		MethodVisitor mv = ((InheritedAttributes) arg).mv;	
		mv.visitVarInsn(ALOAD, 0);
		if (varType.equals(intType) || varType.equals(booleanType) || varType.equals(stringType)){
			mv.visitFieldInsn(GETFIELD, className, varName, varType);
		} else if (varType.substring(0, varType.indexOf("<")).equals("Ljava/util/List")) {
			mv.visitFieldInsn(GETFIELD, className, varName, "Ljava/util/List;");
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
	public Object visitMapListExpression(MapListExpression mapListExpression,
			Object arg) throws Exception {
		for (Expression e : mapListExpression.mapList) {
			e.visit(this, arg);
		}
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}
	
	@Override
	public Object visitSizeExpression(SizeExpression sizeExpression, Object arg)
			throws Exception {
		MethodVisitor mv = ((InheritedAttributes) arg).mv;
		sizeExpression.expression.visit(this, arg);			
		String varType = sizeExpression.expression.getType();
		if (varType.substring(0, varType.indexOf("<")).equals("Ljava/util/List")) {
			mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "size", "()I", true);
		} else {
			throw new UnsupportedOperationException("Map size not support yet");
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
