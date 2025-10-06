package com.owlenz.lox;

import sun.misc.Signal;

class RuntimeError extends RuntimeException{
    final Token token;
    final Signal signal;

    RuntimeError(Token token, String message){
        super(message);
        this.token = token;
        this.signal = null; 
    }

    RuntimeError(Token token, String message, Signal sig){
        super(message);
        this.token = token;
        this.signal = sig;
    }
}
