package org.example;

import static org.example.TokenType.*;

public class Lexer {
    private String src;
    private int currPos;
    private int nextPos;
    private char ch;

    interface Token {
        public TokenType getTokenType();
    }

    public boolean tokenPartialEq(Token t1, Token t2) {
        return (t1.getTokenType() == t2.getTokenType());
    }

    public static class SimpleToken implements Token {
        public TokenType t;

        SimpleToken(TokenType t) {
            this.t = t;
        }

        public TokenType getTokenType() {
            return this.t;
        }
    }

    public static class ValueToken implements Token {
        public TokenType t;
        String value;

        ValueToken(TokenType type, String value) {
            this.t = type;
            this.value = value;
        }

        public TokenType getTokenType() {
            return this.t;
        }
    }

    Lexer(String src) {
        this.currPos = 0;
        this.nextPos = 0;
        this.ch = 0;
        this.src = src;

        this.readChar(); // currPos = 0; nextPos =1;
    }

    // Move the pointer. 1 look-ahead
    private void readChar() {
        if (this.nextPos >= this.src.length()) {
            this.ch = 0; // EOF
        } else {
            this.ch = this.src.charAt(this.nextPos); // Whatever char
        }

        this.currPos = this.nextPos;
        this.nextPos += 1;
    }

    public Token nextToken() {
        Token t = null;
        switch (this.ch) {
            case '(' -> t = new SimpleToken(LParen);
            case ')' -> t = new SimpleToken(RParen);
            case '{' -> t = new SimpleToken(LBrace);
            case '}' -> t = new SimpleToken(RBrace);
            case '+' -> t = new SimpleToken(Plus);
            case '=' -> t = new SimpleToken(Equal);
            case ',' -> t = new SimpleToken(Comma);
            case ';' -> t = new SimpleToken(Semicolon);

            case '/' -> { // Special case
                char c1 = this.ch;
                this.readChar();
                if (c1 == this.ch) {
                    // It is a comment!
                    while (this.ch != '\n') {
                        this.readChar();
                    }

                    // Again ignore this symbol and return the next normal token
                    return nextToken();
                } else {
                    return new SimpleToken(Slash);
                }
            }

            case ' ' -> {
                // I kind of don't like this solution but it does work.
                // Advances the pointer and recursively returns the next non-whitespace char.
                this.readChar();
                return nextToken();
            }
            case '\t' -> {
                this.readChar();
                return nextToken();
            }
            case '\n' -> {
                this.readChar();
                return nextToken();
            }
            case '\r' -> {
                this.readChar();
                return nextToken();
            }
            case 0 -> t = new SimpleToken(EOF);
            default -> {
                if ((this.ch >= 'a' && this.ch <= 'z') || (this.ch >= 'a' && this.ch <= 'z')) {
                    // look in hashmap

                    // it is an identifier
                }
            }
        }

        if (t == null) {
            throw new RuntimeException("Invalid token " + this.ch);
        }

        this.readChar();
        return t;
    }

}
