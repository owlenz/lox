package com.owlenz.lox;

import sun.misc.Signal;
import java.util.List;

class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Object> {

    private Environment environment = new Environment();

    public void interpret(List<Stmt> statements) {
        for (Stmt stmt : statements) {
            try {
                execute(stmt);
            } catch (RuntimeError err) {
                Lox.runtimeError(err);
            }
        }
    }

    public Object execute(Stmt stmt) {
        return stmt.accept(this);
    }

    public Object evaluate(Expr expression) {
        return expression.accept(this);
    }

    @Override
    public Object visitLiteral(Expr.Literal literal) {
        Object lit = literal.value;
        return lit;
    }

    private boolean isTruthful(Object val) {
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
        } else if (val instanceof Boolean) {
            return (boolean) val;
        }
        return true;
    }

    @Override
    public Object visitUnary(Expr.Unary unary) {
        Object right = evaluate(unary.exp);
        switch (unary.op.type) {
            case TokenType.MINUS:
                checkNumberOperands(unary.op, right);
                return -(double) right;
            case TokenType.BANG:
                return !isTruthful(right);
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

    private Object checkViableComparison(Token token, Object left, Object right) throws RuntimeError {
        try {
            checkNumberOperands(token,left,right);
            switch(token.type){
            case TokenType.GREATER:
                return (double) left > (double) right;
            case TokenType.GREATER_EQUAL:
                return (double) left >= (double) right;
            case TokenType.LESS:
                return (double) left < (double) right;
            case TokenType.LESS_EQUAL:
                return (double) left <= (double) right;
            default:
                return null; 
            }
                
        } catch (RuntimeError err) {
            // if (!(left instanceof Double) || !(right instanceof Double)) {
            // }
            return null; 
        }
    }

    private void checkDivisionByZero(Token token, Object left, Object right) throws RuntimeError {
        if ((double) right == (double) 0) {
            Signal sig = new Signal("FPE");
            throw new RuntimeError(token, "Division by zero is forbidden", sig);
        }
    }

    @Override
    public Object visitBinary(Expr.Binary binary) {
        Object left = evaluate(binary.left), right = evaluate(binary.right);
        switch (binary.op.type) {
            case TokenType.MINUS:
                checkNumberOperands(binary.op, left, right);
                return (double) left - (double) right;
            case TokenType.PLUS:
                checkNumberOperands(binary.op, left, right);
                return (double) left + (double) right;
            case TokenType.SLASH:
                checkNumberOperands(binary.op, left, right);
                checkDivisionByZero(binary.op, left, right);
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
                // checkViableComparison(binary.op, left, right);
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
        return evaluate(paren.exp);
    }

    @Override
    public Object visitExpression(Stmt.Expression stmt) {
        evaluate(stmt.exp);
        return null;
    }

    @Override
    public Object visitPrint(Stmt.Print stmt) {
        Object value =  evaluate(stmt.exp);
        // TODO: do it 
        boolean REPL = true;
        System.out.printf(value.toString() + (REPL ? '\n' : "" ));
        return null;
    }

    @Override
    public Object visitVariable(Stmt.Variable variable) {
        Object value = evaluate(variable.init);
        environment.assign(variable.name.lexeme, value);
        return null;
    }

    @Override
    public Object visitvarExpression(Stmt.varExpression varexpression) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitvarExpression'");
    }

    @Override
    public Object visitVariable(Expr.Variable variable) {
        Object value = environment.get(variable.name);
        return value;
    }

    @Override
    public Object visitAssignment(Expr.Assignment assignment) {
        Object value = evaluate(assignment.exp);
        environment.assign(assignment.name.lexeme, value);
        return value;
    }


}
