package com.owlenz.lox;

abstract class Expr {

	interface Visitor<R> {
		R visitLiteral(Literal literal);
		R visitVariable(Variable variable);
		R visitUnary(Unary unary);
		R visitBinary(Binary binary);
		R visitAssignment(Assignment assignment);
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
	static class Variable extends Expr{
		final Token name;

		Variable(Token name){
			this.name = name;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitVariable(this);
		}

	}
	static class Unary extends Expr{
		final Token op;
		final Expr exp;

		Unary(Token op, Expr exp){
			this.op = op;
			this.exp = exp;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitUnary(this);
		}

	}
	static class Binary extends Expr{
		final Expr left;
		final Token op;
		final Expr right;

		Binary(Expr left, Token op, Expr right){
			this.left = left;
			this.op = op;
			this.right = right;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitBinary(this);
		}

	}
	static class Assignment extends Expr{
		final Token name;
		final Expr exp;

		Assignment(Token name, Expr exp){
			this.name = name;
			this.exp = exp;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitAssignment(this);
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
