package org.example.Lexer;

import org.example.Lexer.TokenUtils.Token;
import org.example.Lexer.TokenUtils.TokenType;

public class ValueToken<R> implements Token {
    private final TokenType t;
    private final R value;
    private final int line;

    public ValueToken(int line, TokenType type, R value) {
        this.t = type;
        this.value = value;
        this.line = line;
    }

    public TokenType getTokenType() {
        return this.t;
    }

    @Override
    public int getPos() {
        return this.line;
    }

    public R getValue() {
        return this.value;
    }

    @Override
    public void print() {
        System.out.printf("[TOKEN] TokenType: %s Value: %s\n", this.t, this.value);
    }
}
