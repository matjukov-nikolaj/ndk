package compiler.ndk.ast.visitor;

import compiler.ndk.ast.blockElems.BlockElem;
import compiler.ndk.ast.expressions.*;
import compiler.ndk.ast.blocks.Block;
import compiler.ndk.ast.programs.Program;
import compiler.ndk.ast.blockElems.statements.*;

public class ToStringVisitor implements ASTVisitor {

    private StringBuilder sb;

    public ToStringVisitor() {
        sb = new StringBuilder();
    }

    public String getString() {
        return sb.toString();
    }

    @Override
    public Object visitBinaryExpression(BinaryExpression binaryExpression,
                                        Object arg) throws Exception {
        String tabs = arg + "\t";
        sb.append(tabs + "{\n" +
                tabs + "\t\"title\":");
        sb.append("\"BinaryExpression\",").append('\n');
        sb.append(tabs + "\t\"children\": [\n");
        String indent = tabs + "\t";

        binaryExpression.expression0.visit(this, indent);

        sb.append(",\n");
        sb.append(tabs + "\t\t{\n" +
                tabs + "\t\t\t\"title\":");
        sb.append("\"" + binaryExpression.op.getText() + "\",").append('\n');
        sb.append(tabs + "\t\t\t\"children\": []\n");
        sb.append(tabs + "\t\t},\n");

        binaryExpression.expression1.visit(this, indent);
        sb.append("\n");
        sb.append(tabs + "\t]\n");
        sb.append(tabs + "}");
        return null;
    }

    @Override
    public Object visitBlock(Block block, Object arg) throws Exception {
        String tabs = arg + "\t";
        sb.append(tabs + "{\n" +
                tabs + "\t\"title\":");
        sb.append("\"Block\",").append('\n');
        sb.append(tabs + "\t\"children\": [\n");
        String indent = tabs + "\t";
        for (BlockElem elem : block.elems) {
            elem.visit(this, indent);
        }
        sb.append(tabs + "\t]\n");
        sb.append(tabs + "}\n");
        return null;
    }

    @Override
    public Object visitExpressionStatement(
            ExpressionStatement expressionStatement, Object arg)
            throws Exception {
        sb.append(arg).append("ExpressionStatement").append('\n');
        String indent = arg + "  ";
        expressionStatement.expression.visit(this, indent);
        return null;
    }

    @Override
    public Object visitIntLitExpression(IntLitExpression intLitExpression,
                                        Object arg) {
        String tabs = arg + "\t";
        sb.append(tabs + "{\n" +
                tabs + "\t\"title\":");
        sb.append("\"IntLitExpression\",").append('\n');
        sb.append(tabs + "\t\"children\": [\n");
        sb.append(tabs + "\t\t{\n" +
                tabs + "\t\t\t\"title\":");
        sb.append("\"" + intLitExpression.value + "\",").append('\n');
        sb.append(tabs + "\t\t\t\"children\": []\n");
        sb.append(tabs + "\t\t}\n");
        sb.append(tabs + "\t]\n");
        sb.append(tabs + "}");
        return null;
    }

    @Override
    public Object visitPrintStatement(PrintStatement printStatement, Object arg)
            throws Exception {
        String tabs = arg + "\t";
        sb.append(tabs + "{\n" +
                tabs + "\t\"title\":");
        sb.append("\"PrintStatement\",").append('\n');
        sb.append(tabs + "\t\"children\": [\n");
        String indent = tabs + "\t";
        printStatement.expression.visit(this, indent);
        sb.append("\n");
        sb.append(tabs + "\t]\n");
        sb.append(tabs + "}\n");
        return null;
    }

    @Override
    public Object visitProgram(Program program, Object arg) throws Exception {
        sb.append("{\"root\":\n" +
                "\t{\n" +
                "\t\t\"title\": \"Program ");
        String indent = "\t\t";
        sb.append("class ").append(program.name).append("\",\n");
        sb.append("\t\t\"children\": [\n");
        program.block.visit(this, indent);
        sb.append("\t\t]\n\t}\n}");
        sb.append('\n');
        return null;
    }

}
