package org.example;

import org.example.TokenUtils.TokenType;
import org.example.TokenUtils.Token;

public class IllegalToken implements Token {
    TokenType t;
    int line;
    char value;

    public IllegalToken(char value, int line) {
        this.t = TokenType.Illegal;
        this.line = line;
        this.value = value;
    }

    public void print() {
        System.err.println(this.toString());
    }

    public String toString() {
        return String.format("\s=> Illegal token: '%c' in line: %d", this.value, this.line);
    }

    public TokenType getTokenType() {
        return this.t;
    }
}
