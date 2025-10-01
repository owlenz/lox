package com.owlenz.lox;

import static com.owlenz.lox.TokenType.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

class Scanner {
    private List<Token> tokens = new ArrayList<>();
    String source;
    // start pos of current lexeme
    private int start = 0; 
    private int current = 0; 
    private int line = 1;

    private static final Map<String, TokenType> keywords = new HashMap<>();

    static {
        keywords.put("or", OR);
        keywords.put("and", AND);
        keywords.put("for", FOR);
        keywords.put("while", WHILE);
        keywords.put("class", CLASS);
        keywords.put("fun", FUN);
        keywords.put("var", VAR);
        keywords.put("if", IF);
        keywords.put("else", ELSE);
        keywords.put("true", TRUE);
        keywords.put("false", FALSE);
        keywords.put("print", PRINT);
        keywords.put("nil", NIL);
    }

    
    Scanner(String source)
    {
        this.source = source;
    }

    private boolean isEOF(){
        return current >= source.length();
    }

    List<Token> scanTokens()
    {
        // loop over each character to make lexemes
        while(!isEOF()){
            start = current;
            scanToken();
        }
        return tokens;
    }

    private void addToken(TokenType type)
    {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, "", line));
    }

    private void addToken(TokenType type, Object literal)
    {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private char nextChar()
    {
        return source.charAt(current++);
    }

    private boolean match(char c)
    {
        if(!isEOF() && source.charAt(current) == c){
            current++;
            return true;
        }
        return false;
    }

    private char peek()
    {
        if(isEOF()) return '\0';
        return source.charAt(current);
    }
    private char peekNext()
    {
        if(current + 1 >= source.length()) return '\0';
        return source.charAt(current+1);
    }


    private void scanToken()
    {
        char c = nextChar();
        switch(c) {
        case '(': addToken(LEFT_PARE); break;
        case ')': addToken(RIGHT_PARE); break;
        case '{': addToken(LEFT_BRACE); break;
        case '}': addToken(RIGHT_BRACE); break;

        case ',': addToken(COMMA); break;
        case '.': addToken(DOT); break;
        case ';': addToken(SEMI); break;
        case ':': addToken(COLON); break;

        case '*': addToken(STAR); break;
        case '+': addToken(PLUS); break;
        case '-': addToken(MINUS); break;
        case '=':
            addToken(match('=') ? EQUAL_EQUAL : EQUAL);
            break;
        case '!':
            addToken(match('=') ? BANG_EQUAL : BANG);
            break;
        case '<':
            addToken(match('=') ? LESS_EQUAL : LESS);
            break;
        case '>':
            addToken(match('=') ? GREATER_EQUAL : GREATER);
            break;
        case '/':
            if(match('/'))
                while(peek() != '\n' && !isEOF()) nextChar();
            else if (match('*')) {
                blockComment();
            } else
                addToken(SLASH);
            break;

        case ' ':
        case '\r':
        case '\t':
            break;
        case '\n':
            line++;
            break;

        case '"':
            string();
            break;

        default:
            if(isDigit(c)){
                number();
            } else if(isAlpha(c)){
                identifier();
            }
            else
                Lox.error(line, "unexpected Charcter");
            break;
        }
    }

    private boolean isAlpha(char c){
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private boolean isDigit(char c){
        return c >= '0' && c <= '9';
    }

    private boolean isAlphaDig(char c){
        return isAlpha(c) || isDigit(c);
    }

    private void blockComment(){

    }

    private void identifier(){
        while(isAlphaDig(peek()))
            nextChar();

        String text = source.substring(start,current);
        TokenType type = keywords.get(text);
        if(type == null) type = IDENTIFIER;
        addToken(type);
    }

    private void number(){
        while(isDigit(peek()))
            nextChar();
        if(peek() == '.' && isDigit(peekNext())){
            nextChar();
            while(peek() != '\n' && isDigit(peek()))
                nextChar();
        }
        Double num = Double.parseDouble(source.substring(start,current));
        addToken(NUMBER, num);
    }

    private void string(){
        while(peek() != '"'){
            if(peek() == '\n') line++;
            nextChar();
        }
        nextChar();

        String string = source.substring(start+1, current-1);
        addToken(STRING, string);
    }
}
