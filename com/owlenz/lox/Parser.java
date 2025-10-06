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

    List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<Stmt>();
        while (!isEOF()) {
            try {
                statements.add(declaration());
            } catch (ParseError err) {
                return null;
            }
        }
        return statements;
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

    public Stmt declaration() {
        try {
            if (match(TokenType.VAR)) {
                return variable();
            } else {
                return statement();
            }
        } catch (ParseError err) {
            synchronize();
            return null;
        }
    }


    public Stmt variable() {
        Token name = consume(TokenType.IDENTIFIER, "Expected a variable name");
        Expr expression = null;
        if (match(TokenType.EQUAL)) {
            expression = expression();
        }
        consume(TokenType.SEMI, "Expect ; after statement");
        return new Stmt.Variable(name, expression);
    }

    public Stmt statement() {
        if (match(TokenType.PRINT)) {
            return printStmt();
        }
        return exprStmt();
    }

    public Stmt printStmt() {
        Expr exp = expression();
        consume(TokenType.SEMI, "Expect ; after statement");
        return new Stmt.Print(exp);
    }

    public Stmt exprStmt(){
        Expr exp = expression();
        consume(TokenType.SEMI, "Expect ; after statement");
        return new Stmt.Expression(exp); 
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
        Expr expr = assignment();

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

    private Expr assignment() {
        // if (match(TokenType.IDENTIFIER) && match(TokenType.EQUAL)) {
        //     Token token = tokens.get(it-2);
        //     System.out.println(token.toString());
        //     // consume(TokenType.EQUAL, "Missing equal");
        //     Expr expr = new Expr.Assignment(token, assignment());
        //     return expr;
        // }
        return equality();
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
            return new Expr.Literal(previous().literal);
        }

        if (match(TokenType.IDENTIFIER)) {
            return new Expr.Variable(previous());
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
        if (match(token)) {
            return previous();
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
