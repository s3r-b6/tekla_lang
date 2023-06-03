package org.example;

import static org.example.TokenUtils.TokenType.*;
import org.example.TokenUtils.TokenType;
import org.example.TokenUtils.Token;

import java.util.ArrayList;
import java.util.HashMap;

public class Lexer {
    private static HashMap<String, SimpleToken> keywords = new HashMap<>();
    static {
        keywords.put("let", new SimpleToken(Let));
        keywords.put("if", new SimpleToken(If));
        keywords.put("for", new SimpleToken(For));
        keywords.put("func", new SimpleToken(For));
        keywords.put("while", new SimpleToken(While));
        keywords.put("return", new SimpleToken(Return));
        keywords.put("true", new SimpleToken(True));
        keywords.put("false", new SimpleToken(False));
    }

    private boolean hadError = false;
    private ArrayList<IllegalToken> errorList = new ArrayList<>();
    private String src;
    private int line;

    private int position;

    private char ch;

    Lexer(String src) {
        this.position = 0;
        this.line = 0;
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
            case ',' -> t = new SimpleToken(Comma);
            case ';' -> t = new SimpleToken(Semicolon);

            case '"' -> {
                this.readChar(); // Skip the first

                StringBuilder lit = new StringBuilder();
                while (this.ch != '"') {
                    lit.append(this.ch);
                    this.readChar();
                }

                return new ValueToken(String, lit.toString());
            }

            case '!' -> {
                this.readChar();
                if (this.ch == '=') {
                    return new SimpleToken(Not_Equal);
                }

                return new SimpleToken(Bang);
            }
            case '=' -> {
                this.readChar();
                if (this.ch == '=') {
                    return new SimpleToken(Equal_Equal);
                }

                return new SimpleToken(Equal);
            }
            case '+' -> {
                this.readChar();
                if (this.ch == '=') {
                    return new SimpleToken(Plus_Equal);
                }

                return new SimpleToken(Plus);
            }
            case '-' -> {
                this.readChar();
                if (this.ch == '-') {
                    return new SimpleToken(Minus_Equal);
                }

                return new SimpleToken(Minus);
            }

            // Comments and slash
            case '/' -> {
                return handleSlash();
            }

            // Whitespace
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
                this.line += 1;
                return nextToken();
            }
            case '\r' -> {
                this.readChar();
                return nextToken();
            }

            case 0 -> t = new SimpleToken(EOF);

            default -> {
                if (!isAlphabetic(this.ch)) {
                    break;
                }

                // Identifier case
                StringBuilder identifier = new StringBuilder(this.ch);
                while (isAlphabetic(this.ch) || this.ch == '_') {
                    identifier.append(this.ch);
                    this.readChar();
                }

                String identifierStr = identifier.toString();
                if (keywords.containsKey(identifierStr)) {
                    return keywords.get(identifierStr);
                }
                // We have something that could be a valid identifier
                return new ValueToken(Identifier, identifier.toString());
            }
        }

        if (t == null) {
            t = new IllegalToken(this.ch, this.line);
            errorList.add((IllegalToken) t);
            hadError = true;
        }

        this.readChar();

        return t;
    }

    public void printErrors() {
        if (!this.hadError) {
            System.out.println("No errors found while parsing");
            return;
        }

        System.err.printf("%d errors found while parsing the file:%n", errorList.size());
        for (IllegalToken it : errorList) {
            it.print();
        }
    }

    private Token handleSlash() {
        this.readChar();
        if ('/' == this.ch) {
            while (this.ch != '\n') {
                this.readChar();
            }

            return nextToken();
        } else if ('=' == this.ch) {
            return new SimpleToken(Slash_Equal);
        } else {
            return new SimpleToken(Slash);
        }
    }

    private boolean isAlphabetic(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    // Move the pointer a single char.
    private void readChar() {
        if (this.position >= this.src.length()) {
            this.ch = 0; // EOF
        } else {
            this.ch = this.src.charAt(this.position); // Whatever char
        }

        this.position += 1;
    }
}
