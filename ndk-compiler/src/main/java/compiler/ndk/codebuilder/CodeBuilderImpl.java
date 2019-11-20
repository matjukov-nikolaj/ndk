package compiler.ndk.codebuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;
import compiler.ndk.ast.visitor.ASTNode;
import compiler.ndk.ast.visitor.CodeGeneratorVisitor;
import compiler.ndk.ast.programs.Program;
import compiler.ndk.ast.visitor.MindMapTreeVisitor;
import compiler.ndk.ast.visitor.TypeCheckVisitor;
import compiler.ndk.lexer.Lexer;
import compiler.ndk.lexer.TokenStream;
import compiler.ndk.mindMapTree.Tree;
import compiler.ndk.parser.Parser;
import compiler.ndk.symbolTable.SymbolTable;

public class CodeBuilderImpl {

    public static class DynamicClassLoader extends ClassLoader {
        DynamicClassLoader(ClassLoader parent) {
            super(parent);
        }

        Class<?> define(String className, byte[] bytecode) {
            return super.defineClass(className, bytecode, 0, bytecode.length);
        }
    }

    public static CodeBuilder newInstance(File file) throws Exception {
        FileReader fr = new FileReader(file);
        ASTNode ast = parseInput(fr);
        if (ast != null) {
            checkType(ast);
//            byte[] bytecode = generateByteCode(ast);
//            DynamicClassLoader loader = new DynamicClassLoader(Thread.currentThread().getContextClassLoader());
//            Class<?> testClass = loader.define(((Program) ast).JVMName, bytecode);
//            return (CodeBuilder) testClass.newInstance();
            return null;
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
        MindMapTreeVisitor v = new MindMapTreeVisitor();
        try {
            ast.visit(v, null);
            Tree tree = v.getTree();
            Gson gson = new Gson();
            Path currentRelativePath = Paths.get("");
            String s = currentRelativePath.toAbsolutePath().toString();
            File file = new File(s + "\\prog.json");
            String jsonInString = gson.toJson(tree);
            try (
            FileWriter fw = new FileWriter(s + "\\prog.json");) {
                fw.write(jsonInString);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        if (ast == null) {
            System.out.println("errors " + parser.getErrors());
        }
        return ast;
    }

    private static void checkType(ASTNode ast) {
        SymbolTable symbolTable = new SymbolTable();
        TypeCheckVisitor v = new TypeCheckVisitor(symbolTable);
        try {
            ast.visit(v, null);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static byte[] generateByteCode(ASTNode ast) {
        CodeGeneratorVisitor v = new CodeGeneratorVisitor();
        byte[] bytecode = null;
        try {
            bytecode = (byte[]) ast.visit(v, null);
            Path currentRelativePath = Paths.get("");
            String s = currentRelativePath.toAbsolutePath().toString();
            try (FileOutputStream fos = new FileOutputStream(s + "\\prog.class")) {
                fos.write(bytecode);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return bytecode;
    }
}