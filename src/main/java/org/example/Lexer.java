package org.example;

import static org.example.TokenUtils.TokenType.*;
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

    private int line;
    private int position;
    private char currChar;
    private String source;

    private boolean hadError = false;
    private ArrayList<IllegalToken> errorList = new ArrayList<>();

    Lexer(String src) {
        this.position = 0;
        this.currChar = 0;
        this.line = 0;
        this.source = src;

        this.consumeChar(); // currPos = 0; nextPos =1;
    }

    /*
     * 1-length tokens that don't have special cases are assigned to t; The rest are
     * directly returned. This is mainly because the switch consumes a char at the
     * end, and complex cases consume a token to check for the special cases.
     * If t is null at the end of the switch, it means the char is an IllegalToken
     */
    public Token nextToken() {
        Token t = null;
        switch (this.currChar) {
            case '(' -> t = new SimpleToken(LParen);
            case ')' -> t = new SimpleToken(RParen);
            case '{' -> t = new SimpleToken(LBrace);
            case '}' -> t = new SimpleToken(RBrace);
            case ',' -> t = new SimpleToken(Comma);
            case ';' -> t = new SimpleToken(Semicolon);
            case 0 -> t = new SimpleToken(EOF);

            case '<' -> {
                return nextMatches('=')
                        ? new SimpleToken(Less_Equal)
                        : new SimpleToken(Less);
            }
            case '>' -> {
                return nextMatches('=')
                        ? new SimpleToken(Greater_Equal)
                        : new SimpleToken(Greater);
            }
            case '!' -> {
                return nextMatches('=')
                        ? new SimpleToken(Not_Equal)
                        : new SimpleToken(Bang);
            }
            case '=' -> {
                return nextMatches('=')
                        ? new SimpleToken(Equal_Equal)
                        : new SimpleToken(Equal);
            }
            case '+' -> {
                return nextMatches('=')
                        ? new SimpleToken(Plus_Equal)
                        : new SimpleToken(Plus);
            }
            case '-' -> {
                return nextMatches('=')
                        ? new SimpleToken(Minus_Equal)
                        : new SimpleToken(Minus);
            }

            case '/' -> {
                return handleSlash();
            }
            case '"' -> {
                return handleString();
            }

            // Since they have to return something, whitespace chars recursively return the
            // next valid token, and are ignored this way
            case ' ' -> {
                this.consumeChar();
                return nextToken();
            }
            case '\t' -> {
                this.consumeChar();
                return nextToken();
            }
            case '\n' -> {
                this.line += 1;
                this.consumeChar();
                return nextToken();
            }
            case '\r' -> {
                this.consumeChar();
                return nextToken();
            }

            default -> {
                if (isAlphabetic(this.currChar)) {
                    return handleAlphaToken();
                } else if (isDigit(this.currChar)) {
                    return handleNumberToken();
                }
            }
        }

        if (t == null) {
            t = new IllegalToken("Unknown token ", this.currChar, this.line);
            errorList.add((IllegalToken) t);
            hadError = true;
        }

        this.consumeChar();
        return t;
    }

    public boolean nextMatches(char c) {
        this.consumeChar();
        return this.currChar == c;
    }

    public void printErrors() {
        if (!this.hadError) {
            System.out.println("[ERRORS] No errors found while parsing");
            return;
        }

        System.err.printf("[ERRORS] %d errors found while parsing the file:%n", errorList.size());
        for (int i = 0; i < errorList.size(); i++) {
            errorList.get(i).printIndex(i);
        }

    }

    private Token handleNumberToken() {
        Token t;
        StringBuilder value = new StringBuilder(this.currChar);
        boolean isValid = true;

        while (this.currChar != ' ' && this.position <= this.source.length()) {
            if (!isDigit(this.currChar) && this.currChar != '\0') {
                isValid = false;
            }
            value.append(this.currChar);
            this.consumeChar();
        }

        if (!isValid) {
            t = new IllegalToken("Invalid number", value.toString(), this.line);
            errorList.add((IllegalToken) t);
            hadError = true;
        } else {
            t = new ValueToken(Integer, value.toString());
        }
        return t;
    }

    private Token handleAlphaToken() {
        // Identifier case
        StringBuilder identifier = new StringBuilder(this.currChar);
        while (isAlphabetic(this.currChar) || this.currChar == '_') {
            identifier.append(this.currChar);
            this.consumeChar();
        }

        String identifierStr = identifier.toString();
        if (keywords.containsKey(identifierStr)) {
            return keywords.get(identifierStr);
        }
        // We have something that could be a valid identifier
        return new ValueToken(Identifier, identifier.toString());
    }

    private Token handleString() {
        this.consumeChar(); // Skip the first

        StringBuilder lit = new StringBuilder();
        while (this.currChar != '"' && this.position < this.source.length()) {
            lit.append(this.currChar);
            this.consumeChar();
        }

        // File ended without string termination
        if (this.currChar != '"') {
            return new IllegalToken("Unterminated String: ", lit.toString(), this.line);
        }

        return new ValueToken(String, lit.toString());
    }

    private Token handleSlash() {
        this.consumeChar();
        if ('/' == this.currChar) {
            while (this.currChar != '\n') {
                this.consumeChar();
            }

            return nextToken();
        } else if ('=' == this.currChar) {
            return new SimpleToken(Slash_Equal);
        } else {
            return new SimpleToken(Slash);
        }
    }

    private boolean isAlphabetic(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    // Move the pointer a single char.
    private void consumeChar() {
        if (this.position >= this.source.length()) {
            this.currChar = 0; // EOF
        } else {
            this.currChar = this.source.charAt(this.position); // Whatever char
        }

        this.position += 1;
    }
}
