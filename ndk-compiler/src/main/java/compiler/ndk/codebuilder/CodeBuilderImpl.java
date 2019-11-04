package compiler.ndk.codebuilder;

import java.io.File;
import java.io.FileReader;

import compiler.ndk.ast.visitor.ASTNode;
import compiler.ndk.ast.visitor.CodeGeneratorVisitor;
import compiler.ndk.ast.programs.Program;
import compiler.ndk.ast.visitor.ToStringVisitor;
import compiler.ndk.lexer.Lexer;
import compiler.ndk.lexer.TokenStream;
import compiler.ndk.parser.Parser;

public class CodeBuilderImpl {
        public static class DynamicClassLoader extends ClassLoader {
            public DynamicClassLoader(ClassLoader parent) {
                super(parent);
            }

            public Class<?> define(String className, byte[] bytecode) {
                return super.defineClass(className, bytecode, 0, bytecode.length);
            }
        };

	public static CodeBuilder newInstance(File file) throws Exception {
		FileReader fr = new FileReader(file);
		ASTNode ast = parseInput(fr);
		if (ast != null) {
			byte[] bytecode = generateByteCode(ast);

			DynamicClassLoader loader = new DynamicClassLoader(Thread.currentThread().getContextClassLoader());
			Class<?> testClass = loader.define(((Program) ast).JVMName, bytecode);
			return (CodeBuilder) testClass.newInstance();
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
		CodeGeneratorVisitor v = new CodeGeneratorVisitor();
		byte[] bytecode = null;
		try {
			bytecode = (byte[]) ast.visit(v, null);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return bytecode;
	}

}