package compiler.ndk.parser;

import java.util.ArrayList;
import java.util.List;

import compiler.ndk.lexer.TokenStream;
import compiler.ndk.lexer.TokenStream.Kind;
import compiler.ndk.lexer.TokenStream.Token;
import static compiler.ndk.lexer.TokenStream.Kind.*;

import compiler.ndk.ast.blockElems.BlockElem;
import compiler.ndk.ast.blockElems.declarations.ClosureDec;
import compiler.ndk.ast.blockElems.declarations.VarDec;
import compiler.ndk.ast.expressions.*;
import compiler.ndk.ast.blocks.Block;
import compiler.ndk.ast.closures.Closure;
import compiler.ndk.ast.lValues.ExpressionLValue;
import compiler.ndk.ast.lValues.IdentLValue;
import compiler.ndk.ast.lValues.LValue;
import compiler.ndk.ast.programs.Program;
import compiler.ndk.ast.qualifiedNames.QualifiedName;
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
		for(SyntaxException e: exceptionList){
			sb.append(e.getMessage() + "\n");
		}
		return sb.toString();
	}
	
	//the token which is parsed currently
	private Token t;	 

	//local references to TokenStream objects for convenience
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

	//This is a convenient way to represent fixed sets of
	//token kinds.  You can pass these to isKind.
	static final Kind[] REL_OPS = { BAR, AND, EQUAL, NOTEQUAL, LESS_THAN, GREATER_THAN, LESS_EQUAL, GREATER_EQUAL };
	static final Kind[] WEAK_OPS = { PLUS, MINUS };
	static final Kind[] STRONG_OPS = { TIMES/*, DIV */};
	static final Kind[] VERY_STRONG_OPS = { LSHIFT, RSHIFT };
	//the FIRST set of simple type
	static final Kind[] SIMPLE_TYPE = { KEY_WORD_INT, KEY_WORD_BOOLEAN, KEY_WORD_STRING }; 
	//the FIRST set of statement
	static final Kind[] STATEMENT_FIRST = { IDENTIFIER, KEY_WORD_PRINT, KEY_WORD_WHILE, KEY_WORD_IF, /*MOD, KEY_WORD_RETURN*/ };
	//the FIRST set of expression
	static final Kind[] EXPRESSION_FIRST = { IDENTIFIER, INT_LIT, BL_TRUE, BL_FALSE, STRING_LIT, NL_NULL, LEFT_BRACKET, NOT, MINUS, KEY_WORD_SIZE, /*KEY_WORD_KEY, KEY_WORD_VALUE,*/ LEFT_BRACE, /*AT */};
	//List to store exception information
	List<SyntaxException> exceptionList = new ArrayList<SyntaxException>();
	
	public List<SyntaxException> getExceptionList(){
		return exceptionList;
	}
	
	public Program parse() {
		Program p = null;
		try{
			p = program();
			if(p != null){
				match(EOF);
			}
		}catch(SyntaxException e){
			exceptionList.add(e);			
		}
		if(exceptionList.isEmpty()){
			return p;
		}else{
			return null;
		}		
	}

	//<Program> ::= <ImportList> class IDENTIFIER <Block>
	private Program program() throws SyntaxException {
		Token first = t;
		Program p = null;
		String name = null;
		//List<QualifiedName> imports = ImportList();
		//try-catch deal with missing class and class name
		try{
			match(KEY_WORD_CLASS);
			name = t.getText();
			match(IDENTIFIER);
		}catch(SyntaxException e){
			exceptionList.add(e);	
			//loop exit when meeting the first "{"
			while(!isKind(LEFT_BRACE)){
				//if EOF occur, rethrow exception
				if(isKind(EOF)){
					throw new SyntaxException(t, t.kind);					
				}else{
					consume();
				}
			}
		}		
		Block block = block();
		p = new Program(first, /*imports,*/ name, block);
		return p;
	}

	//<ImportList> ::=( import IDENT ( . IDENT )* ;) *
	/*private List<QualifiedName> ImportList() throws SyntaxException {
		List<QualifiedName> imports = new ArrayList<QualifiedName>();
		QualifiedName qname = null;
		Token first = t;
		Token name = null;
		while(isKind(KEY_WORD_IMPORT)){
			String qualifiedname = null;
			consume();
			//try-catch deal with wrong import names
			try{
				name = t;
				match(IDENT);
				qualifiedname = name.getText();
				while(isKind(DOT)){
					consume();
					name = t;
					match(IDENT);	
					//qualified name contain all the idents with the "." replaced by "\"
					qualifiedname += "/"+name.getText();
				}
			}catch(SyntaxException e){
				exceptionList.add(e);	
				//consume token until ";" or "class" occur
				while(!isKind(SEMICOLON)){
					if(isKind(KEY_WORD_CLASS)){
						return null;
					}else if(isKind(EOF)){
						//if EOF occur, rethrow exception
						throw new SyntaxException(t, t.kind);
					}else{
						consume();
					}
				}
			}			
			match(SEMICOLON);
			qname = new QualifiedName(first, qualifiedname);
			imports.add(qname);
		}
		return imports;
	}*/

	//<Block> ::= { (<Declaration> ; | <Statement> ; )* }
	private Block block() throws SyntaxException {
		List<BlockElem> elems = new ArrayList<BlockElem>();
		BlockElem blockelem = null;
		Token first = t;
		//try-catch deal with missing first "{" of block
		try{
			match(LEFT_BRACE);
		}catch(SyntaxException e){
			exceptionList.add(e);
			//if "VAR" or statement_FIRST or ";" occur, start to parse block 
			while(!isKind(KEY_WORD_VAR) && !isKind(STATEMENT_FIRST) && !isKind(SEMICOLON)){
				if(isKind(EOF)){
					throw new SyntaxException(t, t.kind);
				}else if(isKind(RIGHT_BRACE)){
					//if "}" occur, exit loop
					break;
				}else{
					consume();
				}
			}
		}		
		while(isKind(KEY_WORD_VAR) || isKind(STATEMENT_FIRST) || isKind(SEMICOLON)){
			if(isKind(KEY_WORD_VAR)){
					blockelem = declaration();
			}else if(isKind(STATEMENT_FIRST)){
					blockelem = statement();
			}else{
				//statement may be empty
				consume();
				continue;
			}
			//if there is no exception in this block element
			if(blockelem != null){
				match(SEMICOLON);
				elems.add(blockelem);
			}			
		}		
		match(RIGHT_BRACE);
		return new Block(first, elems);				
	}
	
	//<Declaration> ::= VAR <VarDec> | VAR <ClosureDec>
	private BlockElem declaration() throws SyntaxException{
		BlockElem d = null;
		Closure c = null;
		Token first = t;
		//try-catch deal with declaration exception
		try{
			match(KEY_WORD_VAR);
			Token identToken = t;
			match(IDENTIFIER);
			if(isKind(COLON) || isKind(SEMICOLON)){
				//<VarDec> ::= IDENT ( : <Type> | empty ) ;
				if(isKind(COLON)){
					consume();
					d = new VarDec(first, identToken, type());
				}else{
					d = new VarDec(first, identToken, new UndeclaredType(identToken));
				}
			}/*else if(isKind(ASSIGN)){
				consume();
				//c equal to null if there is exception in closure
				c = closure();
				//<ClosureDec> ::= IDENT = <Closure>
				if(c != null){
					d = new ClosureDec(first, identToken, c);
				}else{
					return null;
				}				
			}*/else{
				throw new SyntaxException(t, t.kind);
			}
		}catch(SyntaxException e){
			exceptionList.add(e);	
			while(true){
				if(isKind(SEMICOLON)){
					//prevent match ";" two times in block loop
					match(SEMICOLON);
					return null;
				}else if(isKind(RIGHT_BRACE)){
					//if "}" occur, return 
					return null;
				}else if(isKind(EOF)){
					//if EOF occur, rethrow exception
					throw new SyntaxException(t, t.kind);
				}else{
					consume();
				}
			}			
		}		
		return d;
	}
	
	//<VarDec> ::= IDENT ( : <Type> | empty ) ;
	private VarDec varDec() throws SyntaxException{
		VarDec v = null;
		Token first = t;
		Token identToken = t;
		match(IDENTIFIER);
		if(isKind(COLON)){
			consume();
			v = new VarDec(first, identToken, type());
		}else{
			v = new VarDec(first, identToken, new UndeclaredType(identToken));
		}
		return v;
	}
		
	//<Type> ::= <SimpleType> | <KeyValueType> | <ListType>
	private Type type() throws SyntaxException{
		Token first = t;
		Type type = null;
		if(isKind(SIMPLE_TYPE)){
			Token typeToken = t;
			consume();
			//<SimpleType> ::= int | boolean | string
			type = new SimpleType(first, typeToken);
		}/*else if(isKind(AT)){
			consume();
			if(isKind(AT)){
				consume();
				match(LSQUARE);
				SimpleType st = new SimpleType(t, t);		
				match(SIMPLE_TYPE);					
				match(COLON);
				//<KeyValueType> ::= @@ [ <SimpleType> : <Type>]
				type = new KeyValueType(first, st, type());
				match(RSQUARE);
			}*/else if(isKind(LEFT_SQUARE)){
				consume();
				//<ListType> ::= @ [ <Type> ]
				type = new ListType(first, type());
				match(RIGHT_SQUARE);
			/*}else{
				Kind[] compound_type = { AT, LSQUARE };
				throw new SyntaxException(t, compound_type);
			}*/
		}else{
			Kind[] type_set = { KEY_WORD_INT, KEY_WORD_BOOLEAN, KEY_WORD_STRING/*, AT */};
			throw new SyntaxException(t, type_set);
		}
		return type;
	}	
	
	//<Closure> ::= { <FormalArgList> -> <StatementList> }
	/*private Closure closure() throws SyntaxException{
		Token first = t;
		Statement s = null;
		List<VarDec> formalArgs = new ArrayList<VarDec>();
		List<Statement> statements = new ArrayList<Statement>();
		//try-catch deal with exception lie between "{ <FormalArgList> ->"
		try{
			match(LBRACE);
			formalArgs = formalArgList();
			match(ARROW);
		}catch(SyntaxException e){
			exceptionList.add(e);	
			//if exception occur, throw token until meet FIRST set of statement to continue parsing 
			while(!isKind(STATEMENT_FIRST)){
				//if EOF occur, rethrow exception
				if(isKind(EOF)){
					throw new SyntaxException(t, t.kind);
				}else if(isKind(RBRACE)){
					//closure itself end with "}", consume it
					consume();
					return null;
				}else{
					consume();
				}
			}
		}		
		while(isKind(STATEMENT_FIRST)){
			s = statement();
			if(s != null){
				statements.add(s);
				match(SEMICOLON);
			}			
		}		
		match(RBRACE);		
		return new Closure(first, formalArgs, statements); 
	}
	
	//<FormalArgList> ::= empty | <VarDec> (, <VarDec>)*
	private List<VarDec> formalArgList() throws SyntaxException{
		List<VarDec> falist = new ArrayList<VarDec>();
		VarDec v = null;
		if(isKind(IDENT)){
			v = varDec();
			falist.add(v);
			while(isKind(COMMA)){
				consume();
				v = varDec();
				falist.add(v);
			}
		}	
		return falist;
	}*/
	
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
	private Statement statement() throws SyntaxException{
		Statement s = null;
		Token first = t;
		Expression e = null;
		Block block = null;
		try{
			switch(t.kind){
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
				try{
					if(isKind(TIMES)){
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
						s = new WhileStarStatement(first, e, block());
					}else if(isKind(LEFT_BRACKET)){
						consume();
						e = expression();
						match(RIGHT_BRACKET);
						s = new WhileStatement(first, e, block()); 
					}else{
						Kind[] while_set = { TIMES, LEFT_BRACKET };
						throw new SyntaxException(t, while_set);
					}			
				}catch(SyntaxException whileException){
					exceptionList.add(whileException);	
					//throw token until meet "}"
					while(!isKind(RIGHT_BRACE)){
						if(isKind(EOF)){
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
				try{
					match(LEFT_BRACKET);
					e = expression();
					match(RIGHT_BRACKET);
					block = block();
					if(isKind(KEY_WORD_ELSE)){
						consume();
						s = new IfElseStatement(first, e, block, block());
						break;
					}
					s = new IfStatement(first, e, block);
				}catch(SyntaxException ifException){
					exceptionList.add(ifException);	
					//throw token until meet "}"
					while(!isKind(RIGHT_BRACE)){
						if(isKind(EOF)){
							throw new SyntaxException(t, t.kind);
						}
						consume();
					}
					//closure itself end with "}", consume it
					match(RIGHT_BRACE);
				}		
				break;
			/*case KEY_WORD_RETURN:
				consume();
				s = new ReturnStatement(first, expression());
				break;
			case MOD:
				consume();
				s = new ExpressionStatement(first, expression());
				break;*/
			default:
				throw new SyntaxException(t, STATEMENT_FIRST);
			}
		}catch(SyntaxException e1){
			exceptionList.add(e1);	
			while(true){
				//if "}" occur, return
				if(isKind(RIGHT_BRACE)){
					return null;
				}else if(isKind(SEMICOLON)){
					//prevent match ";" two times in block loop
					match(SEMICOLON);
					return null;
				}else if(isKind(EOF)){
					//if EOF occur, rethrow exception
					throw new SyntaxException(t, t.kind);
				}else{
					consume();
				}
			}			
		}		
		return s;
	}
	
	//<LValue> ::= IDENT | IDENT [ <Expression> ]
	private LValue lValue() throws SyntaxException{
		LValue l = null;
		Token first = t;
		if(isKind(IDENTIFIER)){
			Token identToken = t;
			consume();
			if(isKind(LEFT_SQUARE)){
				consume();
				l = new ExpressionLValue(first, identToken, expression());
				match(RIGHT_SQUARE);
			}else{
				l = new IdentLValue(first, identToken);
			}
		}
		return l;
	}
	
	//<ExpressionList> ::= empty | <Expression> ( , <Expression> )*!!!!!!!!!
	private List<Expression> expressionList() throws SyntaxException{
		List<Expression> expressions= new ArrayList<Expression>();
		if(isKind(EXPRESSION_FIRST)){
			expressions.add(expression());
			while(isKind(COMMA)){
				match(COMMA);
				expressions.add(expression());
			}
		}
		return expressions;
	}
		
	//<KeyValueExpression> ::= <Expression> : <Expression>
	/*private KeyValueExpression keyValueExpression() throws SyntaxException{
		Expression key = null;
		Expression value = null;
		Token first = t;
		key = expression();
		match(COLON);
		value = expression();
		return new KeyValueExpression(first, key, value);
	}*/
	
	//<KeyValueList> ::= empty | <KeyValueExpression> ( , <KeyValueExpression> ) *
	/*private List<KeyValueExpression> keyValueList() throws SyntaxException{
		List<KeyValueExpression> kvexpressions= new ArrayList<KeyValueExpression>();
		if(isKind(EXPRESSION_FIRST)){
			kvexpressions.add(keyValueExpression());
			while(isKind(COMMA)){
				match(COMMA);
				kvexpressions.add(keyValueExpression());
			}
		}
		return kvexpressions;
	}*/
	
	//<Expression> ::= <Term> (<RelOp> <Term>)*
	private Expression expression() throws SyntaxException{
		Expression e1 = null;
		Expression e2 = null;
		Token first = t;
		e1 = term();
		while(isKind(REL_OPS)){
			Token op = t;
			match(REL_OPS);
			e2 = term();
			e1 = new BinaryExpression(first, e1, op, e2);
		}
		return e1;		
	}
	
	//<Term> ::= <Elem> (<WeakOp> <Elem>)*
	private Expression term() throws SyntaxException{
		Expression e1 = null;
		Expression e2 = null;
		Token first = t;
		e1 = element();
		while(isKind(WEAK_OPS)){
			Token op = t;
			match(WEAK_OPS);
			e2 = element();
			e1 = new BinaryExpression(first, e1, op, e2);
		}
		return e1;
	}
	
	//<Elem> ::= <Thing> ( <StrongOp> <Thing>)*
	private Expression element() throws SyntaxException{
		Expression e1 = null;
		Expression e2 = null;
		Token first = t;
		e1 = thing();		
		while(isKind(STRONG_OPS)){
			Token op = t;
			match(STRONG_OPS);
			e2 = thing();
			e1 = new BinaryExpression(first, e1, op, e2);
		}
		return e1;
	}

	//<Thing> ::= <Factor> ( <VeryStrongOp> <Factor )*
	private Expression thing() throws SyntaxException{
		Expression e1 = null;
		Expression e2 = null;
		Token first = t;
		e1 = factor();
		while(isKind(VERY_STRONG_OPS)){
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
	private Expression factor() throws SyntaxException{
		Expression e = null;
		Token first = t;
		Token op = null;
		switch(t.kind){
		case IDENTIFIER:
			Token identToken = t;
			match(IDENTIFIER);
			switch(t.kind){
			case LEFT_SQUARE:
				consume();
				e = new ListOrMapElemExpression(first, identToken, expression());
				match(RIGHT_SQUARE);
				break;
			case LEFT_BRACKET:
				consume();
				//<ClosureEvalExpression> ::= IDENT (<ExpressionList>)
				e = new ClosureEvalExpression(first, identToken, expressionList());
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
		case BL_TRUE:						
			e = new BooleanLitExpression(t, t.getBooleanVal());
			consume();
			break;
		case BL_FALSE:
			e = new BooleanLitExpression(t, t.getBooleanVal());
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
			e = new UnaryExpression(first, op,	factor());
			break;
		case MINUS:
			op = t;
			consume();
			e = new UnaryExpression(first, op,	factor());
			break;
		case KEY_WORD_SIZE:
			consume();
			match(LEFT_BRACKET);
			e = new SizeExpression(first, expression());			
			match(RIGHT_BRACKET);
			break;
		/*case KEY_WORD_KEY:
			consume();
			match(LBRACKET);
			e = new KeyExpression(first, expression());			
			match(RBRACKET);
			break;
		case KEY_WORD_VALUE:
			consume();
			match(LBRACKET);
			e = new ValueExpression(first, expression());			
			match(RBRACKET);
			break;*/
		/*case LBRACE:
			e = new ClosureExpression(first, closure());
			break;*/
		case LEFT_SQUARE /*AT*/:
			consume();
			/*if(isKind(LSQUARE)){
				consume();*/
				//<List> ::=@ [ <ExpressionList> ]
				e = new ListExpression(first, expressionList());
				match(RIGHT_SQUARE);
				break;
			/*}else if(isKind(AT)){
				consume();
				match(LSQUARE);
				//<MapList> ::= @@[ <KeyValueList> ]
				e = new MapListExpression(first, keyValueList());
				match(RSQUARE);
				break;
			}else{
				Kind[] list_set = { LSQUARE, AT };
				throw new SyntaxException(t, list_set);	
			}*/
		default:
			throw new SyntaxException(t, EXPRESSION_FIRST);		
		}
		return e;
	}
}
