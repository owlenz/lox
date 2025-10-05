package com.owlenz.lox;

abstract class Expr {

    interface Visitor<R> {
        R visitLiteral(Literal literal);

        R visitUnary(Unary unary);

        R visitBinary(Binary binary);

        R visitParen(Paren paren);

        // R visitTernary(Ternary ternary);
    }

    abstract <R> R accept(Visitor<R> visitor);

    static class Literal extends Expr {
        final Object value;

        Literal(Object value) {
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteral(this);
        }

    }

    static class Unary extends Expr {
        final Token op;
        final Expr exp;

        Unary(Token operator, Expr exp) {
            this.op = operator;
            this.exp = exp;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnary(this);
        }

    }

    static class Binary extends Expr {
        final Expr left;
        final Token op;
        final Expr right;

        Binary(Expr left, Token op, Expr right) {
            this.left = left;
            this.op = op;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinary(this);
        }

    }

    static class Paren extends Expr {
        final Expr exp;

        Paren(Expr exp) {
            this.exp = exp;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitParen(this);
        }
    }

    // static class Ternary extends Expr {
    //     final Expr left;
    //     final Token op;
    //     final Expr right;

    //     Ternary(Expr left, Token op, Expr right) {
    //         this.left = left;
    //         this.op = op;
    //         this.right = right;
    //     }

    //     @Override
    //     <R> R accept(Visitor<R> visitor) {
    //         return visitor.visitTernary(this);
    //     }
    // }
}
