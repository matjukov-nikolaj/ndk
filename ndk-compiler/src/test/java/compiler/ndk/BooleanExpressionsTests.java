package compiler.ndk;

import static org.junit.Assert.*;
import compiler.ndk.ast.programs.Program;
import compiler.ndk.ast.visitor.ASTNode;
import compiler.ndk.ast.visitor.CodeGeneratorVisitor;
import compiler.ndk.ast.visitor.TypeCheckVisitor;
import compiler.ndk.ast.visitor.TypeCheckVisitor.TypeCheckException;
import compiler.ndk.codebuilder.CodeBuilder;
import compiler.ndk.lexer.Lexer;
import compiler.ndk.lexer.TokenStream;
import compiler.ndk.parser.Parser;
import compiler.ndk.parser.Parser.SyntaxException;
import compiler.ndk.symbolTable.SymbolTable;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;

import static compiler.ndk.lexer.TokenStream.Kind.ASSIGN;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class BooleanExpressionsTests {

    public static class DynamicClassLoader extends ClassLoader {
        public DynamicClassLoader(ClassLoader parent) {
            super(parent);
        }

        public Class<?> define(String className, byte[] bytecode) {
            return super.defineClass(className, bytecode, 0, bytecode.length);
        }
    }

    ;

    public void dumpBytecode(byte[] bytecode) {
        int flags = ClassReader.SKIP_DEBUG;
        ClassReader cr;
        cr = new ClassReader(bytecode);
        cr.accept(new TraceClassVisitor(new PrintWriter(System.out)), flags);
    }

    private ASTNode parseCorrectInput(String input) throws SyntaxException {
        TokenStream stream = new TokenStream(input);
        Lexer lexer = new Lexer(stream);
        lexer.scan();
        Parser parser = new Parser(stream);
        System.out.println();
        ASTNode ast = parser.parse();
        if (ast == null) {
            System.out.println("errors " + parser.getErrors());
        }
        assertNotNull(ast);
        return ast;
    }

    private ASTNode typeCheckCorrectAST(ASTNode ast) throws Exception {
        SymbolTable symbolTable = new SymbolTable();
        TypeCheckVisitor v = new TypeCheckVisitor(symbolTable);
        try {
            ast.visit(v, null);
        } catch (TypeCheckException e) {
            System.out.println(e.getMessage());
            fail("no errors expected");
        }
        return ast;
    }

    private void typeCheckIncorrectAST(ASTNode ast) throws Exception {
        SymbolTable symbolTable = new SymbolTable();
        TypeCheckVisitor v = new TypeCheckVisitor(symbolTable);
        try {
            ast.visit(v, null);
            fail("expected error");
        } catch (TypeCheckException e) {
            System.out.println(e.getMessage());
        }
    }

    private byte[] generateByteCode(ASTNode ast) throws Exception {
        CodeGeneratorVisitor v = new CodeGeneratorVisitor();
        byte[] bytecode = (byte[]) ast.visit(v, null);
        dumpBytecode(bytecode);
        return bytecode;
    }

    private void printResult(String input) throws Exception {
        System.out.println(input);
        Program program = (Program) parseCorrectInput(input);
        assertNotNull(program);
        typeCheckCorrectAST(program);
        byte[] bytecode = generateByteCode(program);
        assertNotNull(bytecode);
        System.out.println("\nexecuting bytecode:");
        executeByteCode(program.JVMName, bytecode);
        //assertEquals("correctInput", executeByteCode(program.JVMName, bytecode));
    }

    public void executeByteCode(String name, byte[] bytecode) throws InstantiationException, IllegalAccessException {
        DynamicClassLoader loader = new DynamicClassLoader(Thread
                .currentThread().getContextClassLoader());
        Class<?> testClass = loader.define(name, bytecode);
        CodeBuilder codelet = (CodeBuilder) testClass.newInstance();
        codelet.main();

        System.out.println();
    }


    @Test
    public void printIntLiteral() throws Exception {
        System.out.println("printIntLiteral");
        String input = "class A {\n var a: int; var b: int; a = 1; b = 2; if (a<b){print(\"correct input\");}; \n}";
        printResult(input);
    }


    @Test
    public void printBooleanLiteral() throws Exception {
        System.out.println("printIntLiteral");
        String input = "class A {\n print true; \n}";
        printResult(input);
    }


    @Test
    public void printStringLiteral() throws Exception {
        System.out.println("printStringLiteral");
        String input = "class A {\n print \"hello world\" ; \n}";
        printResult(input);
    }

    @Test
    public void intBinaryOps() throws Exception {
        System.out.println("intBinaryOps");
        String input = "class A {\n print 3+5;\n print 7*2;\n  print 5-4;\n  print 6/2;\n}";
        printResult(input);
    }

    @Test
    public void intBinaryOps2() throws Exception {
        System.out.println("intBinaryOps2");
        String input = "class A {\n print (3+5)/2;\n print (7*2)+(6/3);\n  print 5-4-6;\n  print 6/(2+1);\n}";
        printResult(input);
    }

    @Test
    public void stringConcat() throws Exception {
        System.out.println("stringConcat");
        String input = "class A {\n print \"hello\" + \"***\" + \"world\";\n}";
        printResult(input);
    }

    /*@Test
    public void and() throws Exception {
        System.out.println("and");
        String input = "class A {\n print false & false;\n print false & true;\n  print true & false;\n  print true & true;\n}";
        printResult(input);
    }

    @Test
    public void or() throws Exception {
        System.out.println("or");
        String input = "class A {\n print false | false;\n print false | true;\n  print true | false;\n  print true | true;\n}";
        printResult(input);
    }*/

    @Test
    public void intEqual() throws Exception {
        System.out.println("intEqual");
        String input = "class A {\n print 3 == (1+2); print 3 == 4; \n}";
        printResult(input);
    }

    @Test
    public void intNotEqual() throws Exception {
        System.out.println("intEqual");
        String input = "class A {\n print 3 != (1+2); print 3 != 4; \n}";
        printResult(input);
    }

    @Test
    public void booleanRelational() throws Exception {
        System.out.println("booleanRelational");
        String input = "class A {\n print false != false; print false != true; print false == true; print true == true; \n}";
        printResult(input);
    }

    @Test
    public void stringRelational() throws Exception {
        System.out.println("stringRelational");
        String input = "class A {\n print \"hello\" != \"hello\";\n   print \"hello\" != \"goodbye\";\n   print \"good\" == \"good\";\n    print \"bad\" == \"good\"; \n }";
        printResult(input);
    }

    @Test
    public void intRelational1() throws Exception {
        System.out.println("intRelational1");
        String input = "class A {\n print 3 < 3;\n   print 3 <= 3;\n   print 3>3;\n    print 3>=3; \n }";
        printResult(input);
    }

    @Test
    public void intRelational2() throws Exception {
        System.out.println("intRelational2");
        String input = "class A {\n print 2 < 3;\n   print 4 <= 3;\n   print 4>3;\n    print 2>=3; \n }";
        printResult(input);
    }

    /*@Test
    public void binaryExpressionFail1() throws Exception {
        System.out.println("binaryExpressionFail1");
        String input = "class A {\n  print true + 1;}";
        System.out.println(input);
        Program program = (Program) parseCorrectInput(input);
        typeCheckIncorrectAST(program);
    }

    @Test
    public void binaryExpressionFail2() throws Exception {
        System.out.println("binaryExpressionFail2");
        String input = "class A {\n  print true + \"hello\";}";
        System.out.println(input);
        Program program = (Program) parseCorrectInput(input);
        typeCheckIncorrectAST(program);
    }

    @Test
    public void binaryExpressionFail3() throws Exception {
        System.out.println("binaryExpressionFail2");
        String input = "class A {\n  print 2 & 3;}";
        System.out.println(input);
        Program program = (Program) parseCorrectInput(input);
        typeCheckIncorrectAST(program);
    }*/


    @Test
    public void intVariable() throws Exception {
        System.out.println("intVariable");
        String input = "class A {var x: int; \n x = 3;\n  print x;}";
        printResult(input);
    }

    @Test
    public void booleanVariable() throws Exception {
        System.out.println("booleanVariable");
        String input = "class A {var x: boolean; \n x = false;\n  print x;}";
        printResult(input);
    }

    @Test
    public void expressionsAndVars() throws Exception {
        System.out.println("expressionsAndVars");
        String input = "class A {var x: int; \n var b: boolean;";
        input = input + "x = 42;\n   x = x + 1;\n   /*b = (x > 100) | (x == 43);*/\n  ";
        input = input + "print \"x=\";\n  print x;\n /*print \"b=\"; print b;*/}";
        printResult(input);
    }

}

