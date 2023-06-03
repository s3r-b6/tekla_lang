package org.example;

import org.example.TokenUtils.TokenType;
import org.example.TokenUtils.Token;

public class IllegalToken implements Token {
    TokenType t;
    String value;
    String desc;
    int line;

    public IllegalToken(String desc, String value, int line) {
        this.t = TokenType.Illegal;
        this.line = line;
        this.value = value;
        this.desc = desc;
    }

    public IllegalToken(String desc, char c, int line) {
        this.t = TokenType.Illegal;
        this.line = line;
        this.value = String.valueOf(c);
        this.desc = desc;
    }

    public IllegalToken() {
        this.t = TokenType.Illegal;
        this.line = 0;
        this.desc = "Empty error";
        this.value = "";
    }

    public void print() {
        System.err.println("\s\s[ERROR] => " + this.toString());
    }

    public void printIndex(int num) {
        System.err.println("\s\s[ERROR: " + num + "] => " + this.toString());
    }

    @Override
    public String toString() {
        return String.format("Illegal token: %s '%s' in line: %d", this.desc, this.value, this.line);
    }

    public TokenType getTokenType() {
        return this.t;
    }
}
