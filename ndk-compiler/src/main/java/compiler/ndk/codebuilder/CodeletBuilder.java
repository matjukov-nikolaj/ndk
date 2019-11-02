package compiler.ndk.codebuilder;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.List;

import compiler.ndk.ast.visitor.ASTNode;
import compiler.ndk.ast.visitor.CodeGenVisitor;
import compiler.ndk.ast.programs.Program;
import compiler.ndk.ast.visitor.ToStringVisitor;
import compiler.ndk.lexer.Lexer;
import compiler.ndk.lexer.TokenStream;
import compiler.ndk.parser.Parser;
import compiler.ndk.symbolTable.SymbolTable;

public class CodeletBuilder {
        public static class DynamicClassLoader extends ClassLoader {
            public DynamicClassLoader(ClassLoader parent) {
                super(parent);
            }

            public Class<?> define(String className, byte[] bytecode) {
                return super.defineClass(className, bytecode, 0, bytecode.length);
            }
        };

	public static Codelet newInstance(File file) throws Exception {
		FileReader fr = new FileReader(file);
		ASTNode ast = parseInput(fr);
		if (ast != null) {
			byte[] bytecode = generateByteCode(ast);
			DynamicClassLoader loader = new DynamicClassLoader(Thread.currentThread().getContextClassLoader());
			Class<?> testClass = loader.define(((Program) ast).JVMName, bytecode);
			return (Codelet) testClass.newInstance();
		} else {
			return null;
		}
	}

	private static ASTNode parseInput(FileReader fr) {
		TokenStream stream = new TokenStream(fr);
		Lexer lexer = new Lexer(stream);
		lexer.scan();
		Parser parser = new Parser(stream);
		System.out.println();
		ASTNode ast = parser.parse();
		ToStringVisitor v = new ToStringVisitor();
		try {
			ast.visit(v, null);
			System.out.println(v.getString());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		if (ast == null) {
			System.out.println("errors " + parser.getErrors());
		}
		return ast;
	}

	private static byte[] generateByteCode(ASTNode ast) {
		CodeGenVisitor v = new CodeGenVisitor();
		byte[] bytecode = null;
		try {
			bytecode = (byte[]) ast.visit(v, null);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return bytecode;
	}

	@SuppressWarnings("rawtypes")
	public static List getList(Codelet codelet, String name) throws Exception{
		Class<? extends Codelet> codeletClass = codelet.getClass();
		Field Field = codeletClass.getDeclaredField(name);
		Field.setAccessible(true);
		List l = (List) Field.get(codelet);
		return l;
	}

	public static int getInt(Codelet codelet, String name) throws Exception{
		Class<? extends Codelet> codeletClass = codelet.getClass();
		Field Field = codeletClass.getDeclaredField(name);
		Field.setAccessible(true);
		int i = (int) Field.get(codelet);
		return i;
	}

	public static void setInt(Codelet codelet, String name, int value) throws Exception{
		Class<? extends Codelet> codeletClass = codelet.getClass();
		Field Field = codeletClass.getDeclaredField(name);
		Field.setAccessible(true);
		Field.set(codelet, value);
	}

	public static String getString(Codelet codelet, String name) throws Exception{
		Class<? extends Codelet> codeletClass = codelet.getClass();
		Field Field = codeletClass.getDeclaredField(name);
		Field.setAccessible(true);
		String s = (String) Field.get(codelet);
		return s;
	}


	public static void setString(Codelet codelet, String name, String value) throws Exception{
		Class<? extends Codelet> codeletClass = codelet.getClass();
		Field Field = codeletClass.getDeclaredField(name);
		Field.setAccessible(true);
		Field.set(codelet, value);
	}

	public static boolean getBoolean(Codelet codelet, String name) throws Exception{
		Class<? extends Codelet> codeletClass = codelet.getClass();
		Field Field = codeletClass.getDeclaredField(name);
		Field.setAccessible(true);
		boolean b = (boolean) Field.get(codelet);
		return b;
	}

	public static void setBoolean(Codelet codelet, String name, boolean value) throws Exception{
		Class<? extends Codelet> codeletClass = codelet.getClass();
		Field Field = codeletClass.getDeclaredField(name);
		Field.setAccessible(true);
		Field.set(codelet, value);
	}
}