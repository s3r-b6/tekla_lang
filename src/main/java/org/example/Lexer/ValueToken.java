package org.example.Lexer;

import org.example.Lexer.TokenUtils.Token;
import org.example.Lexer.TokenUtils.TokenType;

public class ValueToken implements Token {
    private final TokenType t;
    private final String value;

    public ValueToken(TokenType type, String value) {
        this.t = type;
        this.value = value;
    }

    public TokenType getTokenType() {
        return this.t;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public void print() {
        System.out.printf("[TOKEN] TokenType: %s Value: %s\n", this.t, this.value);
    }
}
