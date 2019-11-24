package compiler.ndk;

import static org.junit.Assert.*;

import java.util.List;

import compiler.ndk.lexer.Lexer;
import compiler.ndk.lexer.TokenStream;
import compiler.ndk.parser.Parser;
import org.junit.Test;

import compiler.ndk.ast.visitor.ASTNode;
import compiler.ndk.ast.blockElems.statements.AssignmentStatement;
import compiler.ndk.ast.blockElems.BlockElem;
import compiler.ndk.ast.expressions.Expression;
import compiler.ndk.ast.programs.Program;
import compiler.ndk.ast.blockElems.statements.Statement;
import compiler.ndk.ast.visitor.TypeCheckVisitor;
import compiler.ndk.symbolTable.SymbolTable;
import compiler.ndk.ast.visitor.TypeCheckVisitor.TypeCheckException;

public class TestTypeChecker {

    private ASTNode parseCorrectInput(String input) {
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

    private void typeCheckCorrectAST(ASTNode ast) throws Exception {
        SymbolTable symbolTable = new SymbolTable();
        TypeCheckVisitor v = new TypeCheckVisitor(symbolTable);
        try {
            ast.visit(v, null);
        } catch (TypeCheckException e) {
            System.out.println(e.getMessage());
            fail("no errors expected");
        }
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

    @Test
    public void emptyProgram() throws Exception {
        System.out.println("smallest");
        String input = "class A { } ";
        System.out.println(input);
        typeCheckCorrectAST(parseCorrectInput(input));
    }

    @Test
    public void block0() throws Exception {
        System.out.println("block0");
        String input = "class A { var B:int; var C:boolean; } ";
        System.out.println(input);
        typeCheckCorrectAST(parseCorrectInput(input));
    }

    @Test
    public void block1() throws Exception {
        System.out.println("block1");
        String input = "class A { var B:int; var B:boolean; } ";
        System.out.println(input);
        typeCheckIncorrectAST(parseCorrectInput(input));
    }

    @Test
    public void ifStatement() throws Exception {
        System.out.println("ifStatement");
        String input = "class A { var B:int; var C:string; if (C) {var B: boolean;}; }";
        System.out.println(input);
        typeCheckIncorrectAST(parseCorrectInput(input));
    }

    @Test
    public void simpleAssignments() throws Exception {
        System.out.println("simpleAssignments");
        String input = "class A { var B:int;  var C:boolean; var D:string;  B = 5; D = \"hello\"; }";
        System.out.println(input);
        typeCheckCorrectAST(parseCorrectInput(input));
    }

    @Test
    public void nestedScopes1() throws Exception {
        System.out.println("nestedScopes1");
        String input = "class A {\n  var B:int;\n  var C: boolean;\n  if (true) {\n    var B: boolean;\n   if (true) {\n    var C: int;\n    C = 42;\n   };\n };\n}";
        System.out.println(input);
        typeCheckCorrectAST(parseCorrectInput(input));
    }

    @Test
    public void nestedScopes2() throws Exception {
        System.out.println("nestedScopes2");
        String input = "class A {\n  var B:int;\n  var C: boolean;\n  if (C) {\n    var B: boolean;\n   if (B) {\n    \n    C = 42;\n   };\n };\n}";
        System.out.println(input);
        typeCheckIncorrectAST(parseCorrectInput(input));
    }

    @Test
    public void unaryExpression1() throws Exception {
        System.out.println("unaryExpression1");
        String input = "class A {\n  var B:int;\n  var C: boolean;\n  if (true) {\n    var B: boolean;\n   if (true) {\n    var C: int;\n    C = -42;\n   /*B = !B;*/\n  };\n };\n}";
        System.out.println(input);
        typeCheckCorrectAST(parseCorrectInput(input));
    }

    @Test
    public void unaryExpression2() throws Exception {
        System.out.println("unaryExpression2");
        String input = "class A {\n  var B:int;\n  var C: boolean;\n  if (true) {\n    var B: boolean;\n   if (B) {\n    var C: int;\n    C = !42;\n   B = !B;\n  };\n };\n}";
        System.out.println(input);
        typeCheckIncorrectAST(parseCorrectInput(input));
    }

    @Test
    public void unaryExpression3() throws Exception {
        System.out.println("unaryExpression3");
        String input = "class A {\n  var B:int;\n  var C: boolean;\n  if (C) {\n    var B: boolean;\n   if (B) {\n    var C: int;\n    C = -42;\n   B = -B;\n  };\n };\n}";
        System.out.println(input);
        typeCheckIncorrectAST(parseCorrectInput(input));
    }

    @Test
    public void binaryExpression1() throws Exception {
        System.out.println("binaryExpression1");
        String input = "class A {\n  var B:int;\n  var C: int;\n  B = B + C;\n  B = B * C;\n  C = C / B;\n  C = B - C; \n}";
        System.out.println(input);
        Program program = (Program) parseCorrectInput(input);
        typeCheckCorrectAST(program);
        String t0, t1, t2, t3;
        List<BlockElem> elems = program.block.elems;
        Statement s0 = (Statement) elems.get(3);
        Expression e0 = ((AssignmentStatement) s0).expression;
        t0 = e0.getType();
        assertEquals("I", t0);
        t1 = ((AssignmentStatement) elems.get(3)).expression.getType();
        assertEquals("I", t1);
        t2 = ((AssignmentStatement) elems.get(4)).expression.getType();
        assertEquals("I", t2);
        t3 = ((AssignmentStatement) elems.get(5)).expression.getType();
        assertEquals("I", t3);
    }


    @Test
    public void binaryExpressionFail() throws Exception {
        System.out.println("binaryExpressionFail");
        String input = "class A {\n  var B:int;\n  var C: boolean;\n  B = B + C;\n  B = B * C;\n  C = C / B;\n  C = B - C; \n}";
        System.out.println(input);
        Program program = (Program) parseCorrectInput(input);
        typeCheckIncorrectAST(program);
    }

}