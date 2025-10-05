// package com.owlenz.lox;

import javax.management.RuntimeErrorException;

class Interpreter implements Expr.Visitor<Object> {


    public Object interpret(Expr expression) {
        return expression.accept(this);
    }
    
    @Override
    public Object visitLiteral(Expr.Literal literal) {
        Object lit = literal.value;
        return lit;
    }


    private boolean isTruthful(Expr.Literal exp){
        Object val = interpret(exp);
        if (val instanceof String) {
            if (((String) val).length() == 0) {
                return false;
            } else {
                return true;
            }
        } else if (val instanceof Number) {
            if ((Number) val == (Number) 0) {
                return false;
            } else {
                return true;
            }
        } else if (val == null) {
            return false;
        } else if (val instanceof Boolean){
            return(boolean) val;
        }
        return true;
    }
    
    @Override
    public Object visitUnary(Expr.Unary unary) {
        Object right = interpret(unary.exp);
        switch (unary.op.type) {
            case TokenType.MINUS:
                checkNumberOperands(unary.op, right);
                return -(double) right;
            case TokenType.BANG:

                return !(boolean) interpret(unary.exp);
            default:
                return null;
        }
    }

    private boolean isEqual(Object left, Object right) {
        if (left == null && right == null) {
            return true;
        } else if (left == null) {
            return false;
        }
        return left.equals(right);
    }

    private void checkNumberOperands(Token token, Object... vals) throws RuntimeError {
        for (Object val : vals) {
            if (val instanceof Number) {
                return;
            }
        }
        throw new RuntimeError(token, "Operands must be 2 numbers");
    }

    @Override
    public Object visitBinary(Expr.Binary binary) {
        Object left = interpret(binary.left), right = interpret(binary.right);
        switch (binary.op.type) {
            case TokenType.MINUS:
                checkNumberOperands(binary.op, left, right);
                return (double) left - (double) right;
            case TokenType.PLUS:
                checkNumberOperands(binary.op, left, right);
                return (double) left + (double) right;
            case TokenType.SLASH:
                checkNumberOperands(binary.op, left, right);
                return (double) left / (double) right;
            case TokenType.STAR:
                checkNumberOperands(binary.op, left, right);
                return (double) left * (double) right;

            case TokenType.PLUS_PLUS:
                if (left instanceof String && right instanceof String) {
                    return (String) left + (String) right;
                }
                throw new RuntimeError(binary.op, "Operands must be 2 Strings");
            case TokenType.GREATER:
                checkNumberOperands(binary.op, left, right);
                return (double) left > (double) right;
            case TokenType.GREATER_EQUAL:
                checkNumberOperands(binary.op, left, right);
                return (double) left >= (double) right;
            case TokenType.LESS:
                checkNumberOperands(binary.op, left, right);
                return (double) left < (double) right;
            case TokenType.LESS_EQUAL:
                checkNumberOperands(binary.op, left, right);
                return (double) left <= (double) right;
            case TokenType.BANG_EQUAL:
                return !isEqual(left, right);
            case TokenType.EQUAL_EQUAL:
                return isEqual(left, right);
            
            default:
                return null;
        }
    }

    @Override
    public Object visitParen(Expr.Paren paren) {
        return interpret(paren.exp);
    }



}
