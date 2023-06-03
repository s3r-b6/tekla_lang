package org.example;

import static org.example.TokenUtils.TokenType.*;
import static org.example.TokenUtils.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Lexer {
    private static HashMap<String, SimpleToken> keywords = createKeywordMap();

    private int line;
    private int position;
    private char currChar;
    private final String source;

    private boolean hadError = false;
    private final ArrayList<IllegalToken> errorList = new ArrayList<>();

    Lexer(String src) {
        this.position = 0;
        this.currChar = '\0';
        this.line = 0;
        this.source = src;

        this.consumeChar(); // currPos = 0; nextPos =1;
    }

    /*
     * Every single case consumes a char (this is, the cursor is moved 1pos), either itself, or, the
     * method that is being called to handle the case and then returns the proper Token.
     *
     * For cases that are not ignored (i.e., non-whitespace), value is returned directly. For cases
     * that are ignored, value is returned recursively.
     */
    public Token nextToken() {
        switch (this.currChar) {
            case '(' -> {
                this.consumeChar();
                return new SimpleToken(LParen);
            }
            case ')' -> {
                this.consumeChar();
                return new SimpleToken(RParen);
            }
            case '{' -> {
                this.consumeChar();
                return new SimpleToken(LBrace);
            }
            case '}' -> {
                this.consumeChar();
                return new SimpleToken(RBrace);
            }
            case ',' -> {
                this.consumeChar();
                return new SimpleToken(Comma);
            }
            case ';' -> {
                this.consumeChar();
                return new SimpleToken(Semicolon);
            }
            case 0 -> {
                this.consumeChar();
                return new SimpleToken(EOF);
            }

            case '<' -> {
                if (nextMatches('=')) {
                    return new SimpleToken(Less_Equal);
                } else {
                    return new SimpleToken(Less);
                }
            }
            case '>' -> {
                if (nextMatches('=')) {
                    return new SimpleToken(Greater_Equal);
                } else {
                    return new SimpleToken(Greater);
                }
            }
            case '!' -> {
                if (nextMatches('=')) {
                    return new SimpleToken(Not_Equal);
                } else {
                    return new SimpleToken(Bang);
                }
            }
            case '=' -> {
                if (nextMatches('=')) {
                    return new SimpleToken(Equal_Equal);
                } else {

                    return new SimpleToken(Equal);
                }
            }
            case '+' -> {
                if (nextMatches('=')) {
                    return new SimpleToken(Plus_Equal);
                } else {
                    return new SimpleToken(Plus);
                }
            }
            case '-' -> {
                if (nextMatches('=')) {
                    return new SimpleToken(Minus_Equal);
                } else {
                    return new SimpleToken(Minus);
                }
            }

            case '/' -> {
                return handleSlash();
            }
            case '"' -> {
                return handleString();
            }

            // Since they have to return something (not null), whitespace chars recursively return
            // the next valid token
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

        // If we ended up here, it is not a valid char
        IllegalToken t = new IllegalToken("Unknown token ", this.currChar, this.line);

        hadError = true;
        errorList.add(t);
        this.consumeChar();

        return t;
    }

    public boolean nextMatches(char c) {
        this.consumeChar();
        return this.currChar == c;
    }

    public void printErrors() {
        final String RED = "\033[1;91m";
        final String NO_COLOR = "\033[0m";

        if (!this.hadError) {
            System.out.printf("\n[%sPARSING_ERRORS%s] No errors found while parsing\n", RED,
                    NO_COLOR);
            return;
        }

        System.err.printf("\n[%sPARSING_ERRORS%s] %d errors found while parsing the file:\n", RED,
                NO_COLOR, errorList.size());
        for (int i = 0; i < errorList.size(); i++) {
            errorList.get(i).printIndexed(i + 1);
        }
        System.out.println();
    }

    private Token handleNumberToken() {
        Token t;
        StringBuilder value = new StringBuilder(this.currChar);
        boolean isValid = true;

        while (this.currChar != ' ' && this.currChar != ';'
                && this.position <= this.source.length()) {
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

    // Move the pointer a single char.
    private void consumeChar() {
        if (this.position >= this.source.length()) {
            this.currChar = '\0'; // EOF
        } else {
            this.currChar = this.source.charAt(this.position); // Whatever char
        }

        this.position += 1;
    }
}
