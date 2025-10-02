package com.owlenz.lox;

abstract class Expr {

	interface Visitor<R> {
		R visitLiteral(Literal literal);
		R visitUnary(Unary unary);
		R visitBinary(Binary binary);
		R visitParen(Paren paren);
	}

	abstract <R> R accept(Visitor<R> visitor);

	static class Literal extends Expr{
		final Object value;

		Literal(Object value){
			this.value = value;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitLiteral(this);
		}

	}
	static class Unary extends Expr{
		final Token operator;
		final Expr exp;

		Unary(Token operator, Expr exp){
			this.operator = operator;
			this.exp = exp;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitUnary(this);
		}

	}
	static class Binary extends Expr{
		final Expr exp;
		final Token op;
		final Expr exp1;

		Binary(Expr exp, Token op, Expr exp1){
			this.exp = exp;
			this.op = op;
			this.exp1 = exp1;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitBinary(this);
		}

	}
	static class Paren extends Expr{
		final Expr exp;

		Paren(Expr exp){
			this.exp = exp;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitParen(this);
		}

	}
}
