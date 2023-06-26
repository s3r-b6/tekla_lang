package org.example.AbstractSyntaxTree;

import org.example.Lexer.TokenUtils;
import org.example.Lexer.ValueToken;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    final Environment enclosing;
    private final Map<String, Object> values;


    Environment() {
        values = new HashMap<>();
        enclosing = null;
    }

    Environment(Environment enclosing) {
        values = new HashMap<>();
        this.enclosing = enclosing;
    }

    public void define(String name, Object value) {
        values.put(name, value);
    }

    public Object get(ValueToken<String> name) {
        String identifier = name.getValue();
        if (values.containsKey(identifier)) {
            return values.get(identifier);
        }

        if (enclosing != null) {
            return enclosing.get(name);
        }

        throw new Interpreter.RuntimeError(name, "Undefined variable '" + identifier + "'.");
    }

    public void assign(ValueToken<String> name, Object value) {
        if (values.containsKey(name.getValue())) {
            values.put(name.getValue(), value);
            return;
        }

        Environment currEnv = this.enclosing;
        while (currEnv != null) {
            if (currEnv.values.containsKey(name.getValue())) {
                currEnv.values.put(name.getValue(), value);
                return;
            }

            currEnv = currEnv.enclosing;
        }

        throw new RuntimeException("Tried to assign to an undefined variable '" + name.getValue() + "'.");
    }
}
