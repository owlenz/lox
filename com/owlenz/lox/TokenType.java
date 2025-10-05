package com.owlenz.lox;

enum TokenType {
    // single-char
    LEFT_PARE, RIGHT_PARE, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, SEMI, COLON, STAR, SLASH, MINUS, Q_MARK,
    // single- or double-char
    EQUAL, EQUAL_EQUAL,
    BANG, BANG_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,
    PLUS, PLUS_PLUS,

    //
    STRING, NUMBER, IDENTIFIER,

    AND, OR, FOR, WHILE, VAR, FUN, TRUE, FALSE,
    IF, ELSE, NIL, PRINT, RETURN, SUPER, THIS, EOF,
    CLASS;
}
