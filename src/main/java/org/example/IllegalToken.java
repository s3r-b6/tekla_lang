package org.example;

import org.example.TokenUtils.TokenType;
import org.example.TokenUtils.Token;

public class IllegalToken implements Token {
    TokenType t;
    int line;
    String value;

    public IllegalToken(String value, int line) {
        this.t = TokenType.Illegal;
        this.line = line;
        this.value = value;
    }

    public IllegalToken(char c, int line) {
        this.t = TokenType.Illegal;
        this.line = line;
        this.value = String.valueOf(c);
    }

    public void print() {
        System.err.println("\s\s[ERROR] => " + this.toString());
    }

    public void printIndex(int num) {
        System.err.println("\s\s[ERROR: " + num + "] => " + this.toString());
    }

    @Override
    public String toString() {
        return String.format("Illegal token: '%s' in line: %d", this.value, this.line);
    }

    public TokenType getTokenType() {
        return this.t;
    }
}
