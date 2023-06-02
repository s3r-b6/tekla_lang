package org.example;

import org.example.TokenUtils.Token;
import org.example.TokenUtils.TokenType;

public class ValueToken implements Token {
    private TokenType t;
    private String value;

    ValueToken(TokenType type, String value) {
        this.t = type;
        this.value = value;
    }

    public TokenType getTokenType() {
        return this.t;
    }

    public String getValue() {
        return this.value;
    }
}
