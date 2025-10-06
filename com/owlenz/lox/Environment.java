package com.owlenz.lox;

import java.util.HashMap;
import java.util.Map;

class Environment {
    private final Map<String, Object> values = new HashMap<>();

    public void assign(String string, Object value) {
        // if (!values.containsKey(string)) {
            values.put(string, value);
        // } else {
        // }
    }

    public Object get(Token token) {
        if (values.containsKey(token.lexeme)) 
            return values.get(token.lexeme);
        else
            throw new RuntimeError(token, "undefined variable '" + token.lexeme +'\'');
                
    }
}
