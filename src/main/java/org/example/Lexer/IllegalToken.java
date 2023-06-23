package org.example.Lexer;

import org.example.Lexer.TokenUtils.TokenType;
import org.example.Lexer.TokenUtils.Token;

public class IllegalToken implements Token {
    final String RED = "\033[1;91m";
    final String NO_COLOR = "\033[0m";
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
        System.err.printf("  [%sLEXING_ERROR%s] => %s \n", RED, NO_COLOR, this);
    }

    public void printIndexed(int num) {
        System.err.printf("  [%sLEXING_ERROR:%s %d] => %s \n", RED, NO_COLOR, num,
                this);
    }

    @Override
    public String toString() {
        return String.format("Illegal token: %s '%s' in line: %d", this.desc, this.value,
                this.line);
    }

    public TokenType getTokenType() {
        return this.t;
    }

    @Override
    public int getPos() {
        return this.line;
    }
}
