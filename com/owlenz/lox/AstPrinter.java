package com.owlenz.lox;

import com.owlenz.lox.TokenType;


class AstPrinter implements Expr.Visitor<String> {

    String print(Expr expr){
        return expr.accept(this);
    }

    @Override
    public String visitLiteral(Expr.Literal expr){
        if(expr == null) return "";
        return expr.value.toString();
    }

    @Override
    public String visitBinary(Expr.Binary expr){
        if(expr == null) return "";
        return "(" + expr.op.lexeme + " " + expr.left.accept(this) + " " + expr.right.accept(this) + ")";
    }

    @Override
    public String visitUnary(Expr.Unary expr) {
        if(expr == null) return "";
        return "(" + expr.op.lexeme + " " + expr.exp.accept(this) + ")";
    }

    @Override
    public String visitParen(Expr.Paren expr) {
        if(expr == null) return "";
        return "(grouped " + expr.exp.accept(this) + ")";
    }

}
