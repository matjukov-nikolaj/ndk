package compiler.ndk.ast.closures;

import java.util.List;

import compiler.ndk.lexer.TokenStream.Token;
import compiler.ndk.ast.visitor.ASTNode;
import compiler.ndk.ast.visitor.ASTVisitor;
import compiler.ndk.ast.blockElems.declarations.VarDec;
import compiler.ndk.ast.blockElems.statements.Statement;

public class Closure extends ASTNode {
	public List<VarDec>formalArgList;
	public List<Statement> statementList;

	public Closure(Token firstToken, List<VarDec> formalArgList,
			List<Statement> statementList) {
		super(firstToken);
		this.formalArgList = formalArgList;
		this.statementList = statementList;
	}
	
	String JVMType;
	List<String> argTypes;
	String resultType;
	
	void setJVMType(String JVMType){
		this.JVMType = JVMType;
	}

	String getJVMType(){
		assert JVMType != null: "Getting JVMType of a closure that has not yet been visited by the type checker";
		return JVMType;
	}
	
	List<String> getArgTypes(){
		assert argTypes != null: "Getting argTypes of a closure that has not yet been visited by the type checker";	
		return argTypes;
	}
	
	void setArgTypes(List<String> argTypes){
		this.argTypes = argTypes;
	}
	
	String getResultType(){
		assert resultType != null: "Getting result type of a closure that has not yet been visited by the type checker";
		return resultType;
	}
	
	void setResultType(String resultType){
		this.resultType = resultType;
	}
	/** end of new*/

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitClosure(this, arg);
	}

}
