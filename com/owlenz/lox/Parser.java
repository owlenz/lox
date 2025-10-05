package com.owlenz.lox;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private static class ParseError extends RuntimeException{}

    List<Token> tokens;
    private int it = 0;
    boolean err = false;

    List<Boolean> ter_open = new ArrayList<Boolean>();
    private int ter_it = 0;
    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    Expr parse() {
        try {
            return expression();
        } catch (ParseError err) {
            return null;
        }
    }

    Token currentToken() {
        return tokens.get(it);
    }

    private void advance() {
        if (!isEOF())
            it++;
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (currentToken().type == type) {
                advance();
                return true;
            }
        }
        return false;
    }
    private boolean isEOF() {
        return currentToken().type == TokenType.EOF;
    }

    public Expr expression() {
        return comma();
    }

    public Expr comma(){
        Expr expr = ternary();

        while(match(TokenType.COMMA)){
            expr = ternary();
        }
        return expr;
    }

    private Expr ternary() {
        Expr expr = equality();

        // while (match(TokenType.Q_MARK)) {
        //     ter_open.set(ter_it++, true);
        //     Token op = tokens.get(it - 1);
        //     expr = new Expr.Ternary(
        //             expr,
        //             op,
        //             ternary());
        // }
        return expr;
    }

    private Expr equality() {
        Expr expr = comparison();

        while (match(TokenType.EQUAL_EQUAL, TokenType.BANG_EQUAL)) {
            Token op = tokens.get(it - 1);
            expr = new Expr.Binary(
                    expr,
                    op,
                    comparison());
        }

        return expr;
    }

    private Expr comparison() {
        Expr expr = term();

        while (match(TokenType.LESS_EQUAL, TokenType.GREATER_EQUAL,
                TokenType.LESS, TokenType.GREATER)) {
            Token op = tokens.get(it - 1);
            expr = new Expr.Binary(
                    expr,
                    op,
                    term());
        }

        return expr;
    }

    private Expr term() {
        Expr expr = factor();

        while (match(TokenType.PLUS, TokenType.PLUS_PLUS, TokenType.MINUS)) {
            Token op = tokens.get(it - 1);

            expr = new Expr.Binary(
                    expr,
                    op,
                    factor());
        }

        return expr;
    }

    private Expr factor() {
        Expr expr = unary();

        while (match(TokenType.SLASH, TokenType.STAR)) {
            Token op = tokens.get(it - 1);
            expr = new Expr.Binary(
                    expr,
                    op,
                    unary());
        }

        return expr;
    }


    private Expr unary() {
        if(match(TokenType.BANG, TokenType.MINUS)){
            Token op = tokens.get(it - 1);
            return new Expr.Unary(op, unary());
        } else {
            return primary();
        }
    }

    private Expr primary() {
        if(match(TokenType.NIL))
            return new Expr.Literal(null);
        if(match(TokenType.FALSE))
            return new Expr.Literal(false);
        if(match(TokenType.TRUE))
            return new Expr.Literal(true);

        if (match(TokenType.NUMBER, TokenType.STRING)) {
            return new Expr.Literal(tokens.get(it - 1).literal);
        }

        if (match(TokenType.LEFT_PARE)) {
            Expr expr = expression();
            // refactor
            if (currentToken().type != TokenType.RIGHT_PARE) {
                System.out.printf("error: no enclosing parentheses\n");
                err = true;
            }
            advance();
            return new Expr.Paren(expr);
        }

        if(match(TokenType.COMMA)){
            Expr expr = expression();
        }
        
        throw error("Expect Expression\n", currentToken());
    }

    private Token consume(TokenType token, String message) throws ParseError {
        if (match(TokenType.RIGHT_PARE)) {
        }
        throw error(message, currentToken());
    }

    private Token previous() {
        return tokens.get(it - 1);
    }

    private void synchronize() {
        advance();
        // look for statement closure
        while (!isEOF()) {
            if (previous().type == TokenType.SEMI)
                return;

            switch (currentToken().type) {
                case TokenType.CLASS:
                case TokenType.IF:
                case TokenType.FOR:
                case TokenType.ELSE:
                case TokenType.WHILE:
                case TokenType.PRINT:
                case TokenType.FUN:
                case TokenType.VAR:
                case TokenType.RETURN:
                    return;
                default:
                    advance();
            }
        }

    }

    private ParseError error(String message, Token token) {
        Lox.error(token, message);
        return new ParseError();
    }
}
