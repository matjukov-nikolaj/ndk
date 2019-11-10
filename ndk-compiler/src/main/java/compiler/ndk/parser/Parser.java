package compiler.ndk.parser;

import java.util.ArrayList;
import java.util.List;

import compiler.ndk.lexer.TokenStream;
import compiler.ndk.lexer.TokenStream.Kind;
import compiler.ndk.lexer.TokenStream.Token;

import static compiler.ndk.lexer.TokenStream.Kind.*;

import compiler.ndk.ast.blockElems.BlockElem;
import compiler.ndk.ast.blockElems.declarations.VarDec;
import compiler.ndk.ast.expressions.*;
import compiler.ndk.ast.blocks.Block;
import compiler.ndk.ast.lValues.ExpressionLValue;
import compiler.ndk.ast.lValues.IdentLValue;
import compiler.ndk.ast.lValues.LValue;
import compiler.ndk.ast.programs.Program;
import compiler.ndk.ast.blockElems.statements.*;
import compiler.ndk.ast.types.*;


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
    static final Kind[] VERY_STRONG_OPS = {LSHIFT, RSHIFT};
    static final Kind[] SIMPLE_TYPE = {KEY_WORD_INT, KEY_WORD_STRING};
    static final Kind[] STATEMENT_FIRST = {IDENTIFIER, KEY_WORD_PRINT};
    static final Kind[] EXPRESSION_FIRST = {IDENTIFIER, INT_LIT, STRING_LIT, NL_NULL, LEFT_BRACKET, NOT, MINUS, LEFT_BRACE};
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

    //<Program> ::= <ImportList> class IDENTIFIER <Block>
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
        return p;
    }

    //<Block> ::= { (<Declaration> ; | <Statement> ; )* }
    private Block block() throws SyntaxException {
        List<BlockElem> elems = new ArrayList<BlockElem>();
        BlockElem blockelem = null;
        Token first = t;
        //try-catch deal with missing first "{" of block
        try {
            match(LEFT_BRACE);
        } catch (SyntaxException e) {
            exceptionList.add(e);
            //if "VAR" or statement_FIRST or ";" occur, start to parse block
            while (!isKind(KEY_WORD_VAR) && !isKind(STATEMENT_FIRST) && !isKind(SEMICOLON)) {
                if (isKind(EOF)) {
                    throw new SyntaxException(t, t.kind);
                } else if (isKind(RIGHT_BRACE)) {
                    //if "}" occur, exit loop
                    break;
                } else {
                    consume();
                }
            }
        }
        while (isKind(KEY_WORD_VAR) || isKind(STATEMENT_FIRST) || isKind(SEMICOLON)) {
            if (isKind(KEY_WORD_VAR)) {
                blockelem = declaration();
            } else if (isKind(STATEMENT_FIRST)) {
                blockelem = statement();
            } else {
                //statement may be empty
                consume();
                continue;
            }
            //if there is no exception in this block element
            if (blockelem != null) {
                match(SEMICOLON);
                elems.add(blockelem);
            }
        }
        match(RIGHT_BRACE);
        return new Block(first, elems);
    }

    //<Declaration> ::= VAR <VarDec> | VAR <ClosureDec>
    private BlockElem declaration() throws SyntaxException {
        BlockElem d = null;
        Token first = t;
        //try-catch deal with declaration exception
        try {
            match(KEY_WORD_VAR);
            Token identToken = t;
            match(IDENTIFIER);
            if (isKind(COLON) || isKind(SEMICOLON)) {
                //<VarDec> ::= IDENT ( : <Type> | empty ) ;
                if (isKind(COLON)) {
                    consume();
                    d = new VarDec(first, identToken, type());
                } else {
                    d = new VarDec(first, identToken, new UndeclaredType(identToken));
                }
            } else {
                throw new SyntaxException(t, t.kind);
            }
        } catch (SyntaxException e) {
            exceptionList.add(e);
            while (true) {
                if (isKind(SEMICOLON)) {
                    //prevent match ";" two times in block loop
                    match(SEMICOLON);
                    return null;
                } else if (isKind(RIGHT_BRACE)) {
                    //if "}" occur, return
                    return null;
                } else if (isKind(EOF)) {
                    //if EOF occur, rethrow exception
                    throw new SyntaxException(t, t.kind);
                } else {
                    consume();
                }
            }
        }
        return d;
    }

    //<VarDec> ::= IDENT ( : <Type> | empty ) ;
    private VarDec varDec() throws SyntaxException {
        VarDec v = null;
        Token first = t;
        Token identToken = t;
        match(IDENTIFIER);
        if (isKind(COLON)) {
            consume();
            v = new VarDec(first, identToken, type());
        } else {
            v = new VarDec(first, identToken, new UndeclaredType(identToken));
        }
        return v;
    }

    //<Type> ::= <SimpleType> | <KeyValueType> | <ListType>
    private Type type() throws SyntaxException {
        Token first = t;
        Type type = null;
        if (isKind(SIMPLE_TYPE)) {
            Token typeToken = t;
            consume();
            type = new SimpleType(first, typeToken);
        } else if (isKind(LEFT_SQUARE)) {
            consume();
            //type = new ListType(first, type());
            match(RIGHT_SQUARE);
        } else {
            Kind[] type_set = {KEY_WORD_INT, KEY_WORD_STRING/*, AT */};
            throw new SyntaxException(t, type_set);
        }
        return type;
    }

    /*
    <Statement> ::= <LValue> = <Expression>
    | print <Expression>
    | while (<Expression>) <Block>
    | while* ( <Expression> ) <Block>
    | while* (<RangeExpression>) < Block>
    | if (<Expression> ) <Block>
    | if (<Expression>) <Block> else <Block>
    | %<Expression>
    | return <Expression>
    | empty
    */
    private Statement statement() throws SyntaxException {
        Statement s = null;
        Token first = t;
        Expression e = null;
        Block block = null;
        try {
            switch (t.kind) {
                case IDENTIFIER:
                    LValue lvalue = lValue();
                    match(ASSIGN);
                    s = new AssignmentStatement(first, lvalue, expression());
                    break;
                case KEY_WORD_PRINT:
                    consume();
                    s = new PrintStatement(first, expression());
                    break;
                case KEY_WORD_WHILE:
                    consume();
                    //try-catch deal with while exceptions
                    try {
                        if (isKind(MUL)) {
                            consume();
                            match(LEFT_BRACKET);
                            e = expression();
                            //<RangeExpression> :: <Expression> .. <Expression>
						/*if(isKind(RANGE)){
							consume();
							RangeExpression re= new RangeExpression(first, e, expression());
							match(RBRACKET);
							s = new WhileRangeStatement(first, re, block());
							break;
						}*/
                            match(RIGHT_BRACKET);
                            //s = new WhileStarStatement(first, e, block());
                        } else if (isKind(LEFT_BRACKET)) {
                            consume();
                            e = expression();
                            match(RIGHT_BRACKET);
                            //s = new WhileStatement(first, e, block());
                        } else {
                            Kind[] while_set = {MUL, LEFT_BRACKET};
                            throw new SyntaxException(t, while_set);
                        }
                    } catch (SyntaxException whileException) {
                        exceptionList.add(whileException);
                        //throw token until meet "}"
                        while (!isKind(RIGHT_BRACE)) {
                            if (isKind(EOF)) {
                                throw new SyntaxException(t, t.kind);
                            }
                            consume();
                        }
                        //closure itself end with "}", consume it
                        match(RIGHT_BRACE);
                    }
                    break;
                case KEY_WORD_IF:
                    consume();
                    //try-catch deal with if exceptions
                    try {
                        match(LEFT_BRACKET);
                        e = expression();
                        match(RIGHT_BRACKET);
                        block = block();
                        if (isKind(KEY_WORD_ELSE)) {
                            consume();
                            //s = new IfElseStatement(first, e, block, block());
                            break;
                        }
                        //s = new IfStatement(first, e, block);
                    } catch (SyntaxException ifException) {
                        exceptionList.add(ifException);
                        //throw token until meet "}"
                        while (!isKind(RIGHT_BRACE)) {
                            if (isKind(EOF)) {
                                throw new SyntaxException(t, t.kind);
                            }
                            consume();
                        }
                        //closure itself end with "}", consume it
                        match(RIGHT_BRACE);
                    }
                    break;
                default:
                    throw new SyntaxException(t, STATEMENT_FIRST);
            }
        } catch (SyntaxException e1) {
            exceptionList.add(e1);
            while (true) {
                //if "}" occur, return
                if (isKind(RIGHT_BRACE)) {
                    return null;
                } else if (isKind(SEMICOLON)) {
                    //prevent match ";" two times in block loop
                    match(SEMICOLON);
                    return null;
                } else if (isKind(EOF)) {
                    //if EOF occur, rethrow exception
                    throw new SyntaxException(t, t.kind);
                } else {
                    consume();
                }
            }
        }
        return s;
    }

    //<LValue> ::= IDENT | IDENT [ <Expression> ]
    private LValue lValue() throws SyntaxException {
        LValue l = null;
        Token first = t;
        if (isKind(IDENTIFIER)) {
            Token identToken = t;
            consume();
            if (isKind(LEFT_SQUARE)) {
                consume();
                l = new ExpressionLValue(first, identToken, expression());
                match(RIGHT_SQUARE);
            } else {
                l = new IdentLValue(first, identToken);
            }
        }
        return l;
    }

    //<ExpressionList> ::= empty | <Expression> ( , <Expression> )*!!!!!!!!!
    private List<Expression> expressionList() throws SyntaxException {
        List<Expression> expressions = new ArrayList<Expression>();
        if (isKind(EXPRESSION_FIRST)) {
            Expression prev = expression();
            expressions.add(prev);
            while (isKind(COMMA)) {
                match(COMMA);
                Expression curr = expression();
                expressions.add(curr);
                if (prev.firstToken.kind != curr.firstToken.kind) {
                    throw new SyntaxException(curr.firstToken, prev.firstToken.kind);
                }
                prev = curr;
            }
        }
        return expressions;
    }

    //<Expression> ::= <Term> (<RelOp> <Term>)*
    private Expression expression() throws SyntaxException {
        Expression e1 = null;
        Expression e2 = null;
        Token first = t;
        e1 = term();
//        while (isKind(REL_OPS)) {
//            Token op = t;
//            match(REL_OPS);
//            e2 = term();
//            e1 = new BinaryExpression(first, e1, op, e2);
//        }
        return e1;
    }

    //<Term> ::= <Elem> (<WeakOp> <Elem>)*
    private Expression term() throws SyntaxException {
        Expression e1 = null;
        Expression e2 = null;
        Token first = t;
        e1 = element();
        while (isKind(WEAK_OPS)) {
            Token op = t;
            match(WEAK_OPS);
            e2 = element();
            if ((e2.firstToken.kind == BL_TRUE || e1.firstToken.kind == BL_TRUE || e2.firstToken.kind == BL_FALSE || e1.firstToken.kind == BL_FALSE) ||
                    (e1.firstToken.kind != IDENTIFIER && e2.firstToken.kind != IDENTIFIER) && (e1.firstToken.kind != e2.firstToken.kind)) {
                throw new SyntaxException(e2.firstToken, e1.firstToken.kind);
            }
            e1 = new BinaryExpression(first, e1, op, e2);
        }
        return e1;
    }

    //<Elem> ::= <Thing> ( <StrongOp> <Thing>)*
    private Expression element() throws SyntaxException {
        Expression e1 = null;
        Expression e2 = null;
        Token first = t;
        e1 = thing();
        while (isKind(STRONG_OPS)) {
            Token op = t;
            match(STRONG_OPS);
            e2 = thing();
            e1 = new BinaryExpression(first, e1, op, e2);
        }
        return e1;
    }

    //<Thing> ::= <Factor> ( <VeryStrongOp> <Factor )*
    private Expression thing() throws SyntaxException {
        Expression e1 = null;
        Expression e2 = null;
        Token first = t;
        e1 = factor();
        while (isKind(VERY_STRONG_OPS)) {
            Token op = t;
            match(VERY_STRONG_OPS);
            e2 = factor();
            e1 = new BinaryExpression(first, e1, op, e2);
        }
        return e1;
    }

    /*
     IDENT |
     IDENT [ <Expression> ] |
     INT_LIT | true | false | STRING_LIT |
     ( <Expression> ) |
     ! <Factor> | -<Factor> |
     size(<Expression> ) |
     key(<Expression ) |
     value(<Expression >) |
     <ClosureEvalExpression> | <Closure> |
     <List> | <MapList>
     */
    private Expression factor() throws SyntaxException {
        Expression e = null;
        Token first = t;
        Token op = null;
        switch (t.kind) {
            case IDENTIFIER:
                Token identToken = t;
                match(IDENTIFIER);
                switch (t.kind) {
                    case LEFT_SQUARE:
                        consume();
                        //e = new ListElemExpression(first, identToken, expression());
                        match(RIGHT_SQUARE);
                        break;
                    case LEFT_BRACKET:
                        consume();
//                        e = new ClosureEvalExpression(first, identToken, expressionList());
                        match(RIGHT_BRACKET);
                        break;
                    default:
                        e = new IdentExpression(first, identToken);
                }
                break;
            case INT_LIT:
                e = new IntLitExpression(t, t.getIntVal());
                consume();
                break;
            case STRING_LIT:
                e = new StringLitExpression(t, t.getText());
                consume();
                break;
            case LEFT_BRACKET:
                consume();
                e = expression();
                match(RIGHT_BRACKET);
                break;
            case NOT:
                op = t;
                consume();
                e = new UnaryExpression(first, op, factor());
                break;
            case MINUS:
                op = t;
                consume();
                e = new UnaryExpression(first, op, factor());
                break;
            case LEFT_SQUARE:
                consume();
                //e = new ListExpression(first, expressionList());
                match(RIGHT_SQUARE);
                break;
            default:
                throw new SyntaxException(t, EXPRESSION_FIRST);
        }
        return e;
    }
}
