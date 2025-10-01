package com.owlenz.lox;

class Token {
    private TokenType type;
    private String lexeme;
    private int line;
    private Object literal;

    Token(TokenType type, String lexeme, Object literal, int line)
    {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
        this.literal = literal;
    }

    public String toString(){
        return type + " " + lexeme + " " + literal + " " + line;
    }
}
