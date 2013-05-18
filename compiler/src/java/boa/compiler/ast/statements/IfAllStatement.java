package boa.compiler.ast.statements;

import boa.compiler.ast.Component;
import boa.compiler.ast.expressions.Expression;
import boa.compiler.visitors.AbstractVisitor;
import boa.compiler.visitors.AbstractVisitorNoArg;

/**
 * 
 * @author rdyer
 */
public class IfAllStatement extends Statement {
	protected Component var;
	protected Expression condition;
	protected Block body;

	public Component getVar() {
		return var;
	}

	public Expression getCondition() {
		return condition;
	}

	public Block getBody() {
		return body;
	}

	public IfAllStatement(final Component var, final Expression condition, final Block body) {
		var.setParent(this);
		condition.setParent(this);
		body.setParent(this);
		this.var = var;
		this.condition = condition;
		this.body = body;
	}

	/** {@inheritDoc} */
	@Override
	public <A> void accept(final AbstractVisitor<A> v, A arg) {
		v.visit(this, arg);
	}

	/** {@inheritDoc} */
	@Override
	public void accept(final AbstractVisitorNoArg v) {
		v.visit(this);
	}

	public IfAllStatement clone() {
		final IfAllStatement s = new IfAllStatement(var.clone(), condition.clone(), body.clone());
		copyFieldsTo(s);
		return s;
	}
}
