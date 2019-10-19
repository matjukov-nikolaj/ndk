package cop5555sp15.ast.expressions;

import java.util.List;

import cop5555sp15.TokenStream.Token;
import cop5555sp15.ast.ASTVisitor;

public class MapListExpression extends Expression {
	public List<KeyValueExpression> mapList;
	
	public MapListExpression(Token firstToken, List<KeyValueExpression> mapList) {
		super(firstToken);
		this.mapList = mapList;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitMapListExpression(this,arg);
	}

}
