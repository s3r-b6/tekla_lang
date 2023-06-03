package org.example;

import org.example.TokenUtils.Token;
import org.example.TokenUtils.TokenType;

public class SimpleToken implements Token {
    private TokenType t;

    SimpleToken(TokenType t) {
        this.t = t;
    }

    public TokenType getTokenType() {
        return this.t;
    }

    @Override
    public void print() {
        System.out.printf("[TOKEN] TokenType: %s\n", this.t);
    }
}
