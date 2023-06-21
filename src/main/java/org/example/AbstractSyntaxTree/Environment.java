package org.example.AbstractSyntaxTree;

import org.example.Lexer.TokenUtils;
import org.example.Lexer.ValueToken;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private final Map<String, Object> values = new HashMap<>();

    public void define(String name, Object value) {
        values.put(name, value);
    }

    public Object get(ValueToken<String> name) {
        String identifier = name.getValue();
        if (values.containsKey(identifier)) {
            return values.get(identifier);
        }

        throw new Interpreter.RuntimeError(name,
                "Undefined variable '" + identifier + "'.");
    }

    public void assign(ValueToken<String> name, Object value) {
        if (values.containsKey(name.getValue())) {
            values.put(name.getValue(), value);
            return;
        }

        throw new RuntimeException("Tried to assign to an undefined variable '" + name.getValue() + "'.");
    }
}
