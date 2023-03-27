package com.mini.lisp;



import com.mini.lisp.exception.RuntimeError;

import java.util.HashMap;
import java.util.Map;

public class Environment {

    final Environment enclosing;
    //< enclosing-field
    private final Map<String, Object> values = new HashMap<>();
    //> environment-constructors
    Environment() {
        enclosing = null;
    }

    Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }
//> environment-get-enclosing

        if (enclosing != null) return enclosing.get(name);
//< environment-get-enclosing

        throw new RuntimeError(name,"Undefined variable '" + name.lexeme + "'.");
    }
    Environment ancestor(int distance) {
        Environment environment = this;
        for (int i = 0; i < distance; i++) {
            environment = environment.enclosing; // [coupled]
        }

        return environment;
    }
    Object getAt(int distance, String name) {
        return ancestor(distance).values.get(name);
    }
    void define(String name, Object value) {
        values.put(name, value);
    }
}
