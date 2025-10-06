package com.owlenz.lox;

abstract class Stmt {

	interface Visitor<R> {
		R visitExpression(Expression expression);
		R visitVariable(Variable variable);
		R visitvarExpression(varExpression varexpression);
		R visitPrint(Print print);
	}

	abstract <R> R accept(Visitor<R> visitor);

	static class Expression extends Stmt{
		final Expr exp;

		Expression(Expr exp){
			this.exp = exp;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitExpression(this);
		}

	}
	static class Variable extends Stmt{
		final Token name;
		final Expr init;

		Variable(Token name, Expr init){
			this.name = name;
			this.init = init;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitVariable(this);
		}

	}
	static class varExpression extends Stmt{
		final Expr exp;

		varExpression(Expr exp){
			this.exp = exp;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitvarExpression(this);
		}

	}
	static class Print extends Stmt{
		final Expr exp;

		Print(Expr exp){
			this.exp = exp;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitPrint(this);
		}

	}
}
