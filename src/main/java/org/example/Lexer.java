package org.example;

import static org.example.TokenUtils.TokenType.*;
import org.example.TokenUtils.Token;

public class Lexer {
    private String src;
    private int startPos;
    private int position;
    private char ch;

    Lexer(String src) {
        this.startPos = 0;
        this.position = 0;
        this.ch = 0;
        this.src = src;

        this.readChar(); // currPos = 0; nextPos =1;
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
            // Special case. It could be a /, or it could be a //.......
            case '/' -> {
                this.readChar();
                if ('/' != this.ch) {
                    return new SimpleToken(Slash);
                }

                while (this.ch != '\n') {
                    this.readChar();
                }
                this.readChar(); // Also move the pointer past the \n (the switch would do it anyway)

                return nextToken();
            }

            // I kind of don't like this solution.
            // Advances the pointer and recursively returns the next valid Token.
            case ' ' -> {
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
                if ((this.ch >= 'a' && this.ch <= 'z') || (this.ch >= 'A' && this.ch <= 'Z')) {
                    StringBuilder identifier = new StringBuilder(this.ch);

                    // While we read valid identifier-chars
                    while ((this.ch >= 'a' && this.ch <= 'z') || (this.ch >= 'A' && this.ch <= 'Z') || this.ch == '_') {
                        identifier.append(this.ch);
                        this.readChar();
                    }

                    System.out.println("Identifier: " + identifier);

                    // FIX: This is a placeholder!!
                    return new SimpleToken(Identifier);
                    // look in hashmap

                    // if not a keyword, it is an identifier
                }
            }
        }

        if (t == null) {
            throw new RuntimeException("Invalid token '" + this.ch + "'");
        }

        this.readChar();
        return t;
    }

    // Move the pointer a single char.
    private void readChar() {
        if (this.position >= this.src.length()) {
            this.ch = 0; // EOF
        } else {
            this.ch = this.src.charAt(this.position); // Whatever char
        }

        this.startPos = this.position;
        this.position += 1;
    }
}
