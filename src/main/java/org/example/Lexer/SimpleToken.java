package org.example.Lexer;

import org.example.Lexer.TokenUtils.Token;
import org.example.Lexer.TokenUtils.TokenType;

public class SimpleToken implements Token {
    private final TokenType t;

    public SimpleToken(TokenType t) {
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
