package compiler.ndk.parser;

import java.util.ArrayList;
import java.util.List;

import compiler.ndk.lexer.TokenStream;
import compiler.ndk.lexer.TokenStream.Kind;
import compiler.ndk.lexer.TokenStream.Token;

import static compiler.ndk.lexer.TokenStream.Kind.*;

import compiler.ndk.ast.blockElems.BlockElem;
import compiler.ndk.ast.expressions.*;
import compiler.ndk.ast.blocks.Block;
import compiler.ndk.ast.programs.Program;
import compiler.ndk.ast.blockElems.statements.*;


public class Parser {

    @SuppressWarnings("serial")
    public class SyntaxException extends Exception {
        public Token t;
        public Kind[] expected;
        public String msg;

        SyntaxException(Token t, Kind expected) {
            this.t = t;
            msg = "";
            this.expected = new Kind[1];
            this.expected[0] = expected;

        }

        public SyntaxException(Token t, String msg) {
            this.t = t;
            this.msg = msg;
        }

        public SyntaxException(Token t, Kind[] expected) {
            this.t = t;
            msg = "";
            this.expected = expected;
        }

        public String getMessage() {
            StringBuilder sb = new StringBuilder();
            sb.append(" error at token ").append(t.toString()).append(" ")
                    .append(msg);
            sb.append(". Expected: ");
            for (Kind kind : expected) {
                sb.append(kind).append(" ");
            }
            return sb.toString();
        }
    }

    public String getErrors() {
        StringBuilder sb = new StringBuilder();
        for (SyntaxException e : exceptionList) {
            sb.append(e.getMessage() + "\n");
        }
        return sb.toString();
    }

    private Token t;

    final TokenStream tokens;

    public Parser(TokenStream tokens) {
        this.tokens = tokens;
        t = tokens.nextToken();
    }

    private Kind match(Kind kind) throws SyntaxException {
        if (isKind(kind)) {
            consume();
            return kind;
        }
        throw new SyntaxException(t, kind);
    }

    private Kind match(Kind... kinds) throws SyntaxException {
        Kind kind = t.kind;
        if (isKind(kinds)) {
            consume();
            return kind;
        }
        StringBuilder sb = new StringBuilder();
        for (Kind kind1 : kinds) {
            sb.append(kind1).append(kind1).append(" ");
        }
        throw new SyntaxException(t, "expected one of " + sb.toString());
    }

    private boolean isKind(Kind kind) {
        return (t.kind == kind);
    }

    private void consume() {
        if (t.kind != EOF)
            t = tokens.nextToken();
    }

    private boolean isKind(Kind... kinds) {
        for (Kind kind : kinds) {
            if (t.kind == kind)
                return true;
        }
        return false;
    }

    static final Kind[] WEAK_OPS = {PLUS, MINUS};
    static final Kind[] STRONG_OPS = {MUL, DIV};
    static final Kind[] STATEMENT_FIRST = { KEY_WORD_PRINT };
    static final Kind[] EXPRESSION_FIRST = { INT_LIT };
    List<SyntaxException> exceptionList = new ArrayList<SyntaxException>();

    public List<SyntaxException> getExceptionList() {
        return exceptionList;
    }

    public Program parse() {
        Program p = null;
        try {
            p = program();
            if (p != null) {
                match(EOF);
            }
        } catch (SyntaxException e) {
            exceptionList.add(e);
        }
        if (exceptionList.isEmpty()) {
            return p;
        } else {
            return null;
        }
    }

    // <Program> -> class IDENTIFIER <Block>
    private Program program() throws SyntaxException {
        Token first = t;
        Program p = null;
        String name = null;
        try {
            match(KEY_WORD_CLASS);
            name = t.getText();
            match(IDENTIFIER);
        } catch (SyntaxException e) {
            exceptionList.add(e);
            while (!isKind(LEFT_BRACE)) {
                if (isKind(EOF)) {
                    throw new SyntaxException(t, t.kind);
                } else {
                    consume();
                }
            }
        }
        Block block = block();
        p = new Program(first, name, block);
        p.JVMName = p.name;
        return p;
    }

//    <BlockContent> -> <Declarations> <Statements>
//    <Declarations> -> <Declaration>
//    <Declarations> -> <Declaration> <Declarations>
//    <Statements> -> <Statement>
//    <Statements> -> <Statement> <Statements>
    private Block block() throws SyntaxException {
        List<BlockElem> elems = new ArrayList<BlockElem>();
        BlockElem blockelem = null;
        Token first = t;
        try {
            match(LEFT_BRACE);
        } catch (SyntaxException e) {
            exceptionList.add(e);
            while (!isKind(KEY_WORD_VAR) && !isKind(STATEMENT_FIRST) && !isKind(SEMICOLON)) {
                if (isKind(EOF)) {
                    throw new SyntaxException(t, t.kind);
                } else if (isKind(RIGHT_BRACE)) {
                    break;
                } else {
                    consume();
                }
            }
        }
        while (isKind(KEY_WORD_VAR) || isKind(STATEMENT_FIRST) || isKind(SEMICOLON)) {
            if (isKind(KEY_WORD_VAR)) {
                //TODO parse declaration
            } else if (isKind(STATEMENT_FIRST)) {
                blockelem = statement();
            } else {
                consume();
                continue;
            }
            if (blockelem != null) {
                match(SEMICOLON);
                elems.add(blockelem);
            }
        }
        match(RIGHT_BRACE);
        return new Block(first, elems);
    }

    // <Statement> -> print <Expression>;
    private Statement statement() throws SyntaxException {
        Statement s = null;
        Token first = t;
        try {
            switch (t.kind) {
                case KEY_WORD_PRINT:
                    consume();
                    s = new PrintStatement(first, expression());
                    break;
                default:
                    throw new SyntaxException(t, STATEMENT_FIRST);
            }
        } catch (SyntaxException e1) {
            exceptionList.add(e1);
            while (true) {
                if (isKind(RIGHT_BRACE)) {
                    return null;
                } else if (isKind(SEMICOLON)) {
                    match(SEMICOLON);
                    return null;
                } else if (isKind(EOF)) {
                    throw new SyntaxException(t, t.kind);
                } else {
                    consume();
                }
            }
        }
        return s;
    }

    // <Expression> -> <NumberExpression>
    // <NumberExpressions> -> <NumberExpression>
    // <NumberExpressions> -> <NumberExpressions><WeakOperation><NumberExpression>
    private Expression expression() throws SyntaxException {
        Expression e1 = null;
        Expression e2 = null;
        Token first = t;
        e1 = term();
        while (isKind(WEAK_OPS)) {
            Token op = t;
            match(WEAK_OPS);
            e2 = term();
            e1 = new BinaryExpression(first, e1, op, e2);
        }
        return e1;
    }

    // <NumberExpression> -> <NumberExpression><StrongOperation><NumberElem>
    private Expression term() throws SyntaxException {
        Expression e1 = null;
        Expression e2 = null;
        Token first = t;
        e1 = element();
        while (isKind(STRONG_OPS)) {
            Token op = t;
            match(STRONG_OPS);
            e2 = element();
            e1 = new BinaryExpression(first, e1, op, e2);
        }
        return e1;
    }

    // <NumberExpression> -> <NumberElem>
    // <NumberElem> -> (<NumberExpressions>)
    // <NumberElem> -> INT_LIT
    private Expression element() throws SyntaxException {
        Expression e = null;
        switch (t.kind) {
            case INT_LIT:
                e = new IntLitExpression(t, t.getIntVal());
                consume();
                break;
            case LEFT_BRACKET:
                consume();
                e = expression();
                match(RIGHT_BRACKET);
                break;
            default:
                throw new SyntaxException(t, EXPRESSION_FIRST);
        }
        return e;
    }

}
