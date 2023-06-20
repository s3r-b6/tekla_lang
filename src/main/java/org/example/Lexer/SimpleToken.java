package org.example.Lexer;

import org.example.Lexer.TokenUtils.Token;
import org.example.Lexer.TokenUtils.TokenType;

public class SimpleToken implements Token {
    private final TokenType t;
    private final int line;

    public SimpleToken(int line, TokenType t) {
        this.t = t;
        this.line = line;
    }

    public TokenType getTokenType() {
        return this.t;
    }

    @Override
    public int getPos() {
        return this.line;
    }

    @Override
    public void print() {
        System.out.printf("[TOKEN] TokenType: %s\n", this.t);
    }
}
