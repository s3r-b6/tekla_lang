package org.example.Lexer;

import static org.example.Lexer.TokenUtils.TokenType.*;
import static org.example.Lexer.TokenUtils.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Lexer {
    private static final HashMap<String, TokenType> keywords = createKeywordMap();
    private final String source;
    // Illegal tokens right now are created on unknown chars, on invalid strings
    // and on invalid numbers
    private final ArrayList<IllegalToken> errorList = new ArrayList<>();
    private int line;
    private int position;
    private char currChar;
    private boolean hadError = false;

    public Lexer(String src) {
        this.position = 0;
        this.currChar = '\0';
        this.line = 0;
        this.source = src;

        this.consumeChar(); // currPos = 0; nextPos =1;
    }

    /*
     * Every single case consumes a char (this is, the cursor is moved 1pos), either it does move
     * the pointer, or, the method that is being called to handle the case moves it.
     *
     * For cases that are not ignored (i.e., non-whitespace), value is returned directly. For cases
     * that are ignored, value is returned recursively (to not return null: A token is either a
     * valid token or a IllegalToken).
     */
    public Token nextToken() {
        switch (this.currChar) {
            case '(' -> {
                this.consumeChar();
                return new SimpleToken(this.line, LParen);
            }
            case ')' -> {
                this.consumeChar();
                return new SimpleToken(this.line, RParen);
            }
            case '{' -> {
                this.consumeChar();
                return new SimpleToken(this.line, LBrace);
            }
            case '}' -> {
                this.consumeChar();
                return new SimpleToken(this.line, RBrace);
            }
            case ',' -> {
                this.consumeChar();
                return new SimpleToken(this.line, Comma);
            }
            case ';' -> {
                this.consumeChar();
                return new SimpleToken(this.line, Semicolon);
            }
            case 0 -> {
                this.consumeChar();
                return new SimpleToken(this.line, EOF);
            }

            case '<' -> {
                if (nextMatches('=')) {
                    consumeChar();
                    return new SimpleToken(this.line, Less_Equal);
                } else {
                    return new SimpleToken(this.line, Less);
                }
            }
            case '>' -> {
                if (nextMatches('=')) {
                    consumeChar();
                    return new SimpleToken(this.line, Greater_Equal);
                } else {
                    return new SimpleToken(this.line, Greater);
                }
            }
            case '!' -> {
                if (nextMatches('=')) {
                    consumeChar();
                    return new SimpleToken(this.line, Not_Equal);
                } else {
                    return new SimpleToken(this.line, Bang);
                }
            }
            case '=' -> {
                if (nextMatches('=')) {
                    consumeChar();
                    return new SimpleToken(this.line, Equal_Equal);
                } else {
                    return new SimpleToken(this.line, Equal);
                }
            }
            case '+' -> {
                if (nextMatches('=')) {
                    consumeChar();
                    return new SimpleToken(this.line, Plus_Equal);
                } else {
                    return new SimpleToken(this.line, Plus);
                }
            }
            case '*' -> {
                if (nextMatches('=')) {
                    consumeChar();
                    return new SimpleToken(this.line, Star_Equal);
                } else {
                    return new SimpleToken(this.line, Star);
                }
            }
            case '-' -> {
                if (nextMatches('=')) {
                    consumeChar();
                    return new SimpleToken(this.line, Minus_Equal);
                } else {
                    return new SimpleToken(this.line, Minus);
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
            case ' ', '\t', '\r' -> {
                this.consumeChar();
                return nextToken();
            }
            case '\n' -> {
                this.line += 1;
                this.consumeChar();
                return nextToken();
            }

            default -> {
                if (isAlphabetic(this.currChar)) {
                    return handleAlphaToken();
                } else if (isDigit(this.currChar)) {
                    return handleNumberToken();
                } else if (this.currChar == '&' || this.currChar == '|') {
                    return handleAmperAndSlash();
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


    public ArrayList<Token> readUntilEOF() {
        ArrayList<Token> list = new ArrayList<>();

        Token t = this.nextToken();

        while (!isEOF(t)) {
            list.add(t);
            t = this.nextToken();
        }
        list.add(t);

        return list;
    }

    public ArrayList<Token> readSequenceOfTokens() {
        ArrayList<Token> list = new ArrayList<>();

        int line = this.line;
        Token t = this.nextToken();

        while (line == this.line && !isEOF(t)) {
            list.add(t);
            t = this.nextToken();
        }
        // No EOF -> This is used to read lines in the REPL
        return list;
    }

    public boolean hadError() {
        return this.hadError;
    }

    public boolean nextMatches(char c) {
        this.consumeChar();
        return this.currChar == c;
    }

    public void printErrors() {
        final String RED = "\033[1;91m";
        final String NO_COLOR = "\033[0m";

        if (!this.hadError) {
            System.out.printf("\n[%sPARSING_ERRORS%s] No errors found while parsing\n", RED, NO_COLOR);
            return;
        }

        System.err.printf("\n[%sPARSING_ERRORS%s] %d errors found while parsing the file:\n", RED, NO_COLOR, errorList.size());
        for (int i = 0; i < errorList.size(); i++) {
            errorList.get(i).printIndexed(i + 1);
        }
        System.out.println();
    }

    private Token handleNumberToken() {
        Token t;
        boolean isValid = true;
        StringBuilder value = new StringBuilder();

        value.append(this.currChar);
        this.consumeChar();

        //This skips the chars that could be right after a number
        while (this.currChar != ' ' && this.currChar != '/' && this.currChar != '*'
                && this.currChar != '-' && this.currChar != '+' && this.currChar != ';'
                && this.currChar != ')' && this.currChar != '|' && this.currChar != '&'
                && this.currChar != '='
                && this.position <= this.source.length()) {

            if (!isDigit(this.currChar)) isValid = false;

            value.append(this.currChar);
            this.consumeChar();
        }

        if (!isValid) {
            t = new IllegalToken("Invalid number", value.toString(), this.line);
            errorList.add((IllegalToken) t);
            hadError = true;
        } else {
            t = new ValueToken<>(this.line, Integer, Double.parseDouble(value.toString()));
        }
        return t;
    }

    private Token handleAlphaToken() {
        // Identifier case
        StringBuilder identifier = new StringBuilder();
        while (isAlphabetic(this.currChar) || isDigit(this.currChar) || this.currChar == '_') {
            identifier.append(this.currChar);
            this.consumeChar();
        }

        String identifierStr = identifier.toString();
        if (keywords.containsKey(identifierStr)) {
            return new SimpleToken(this.line, keywords.get(identifierStr));
        }
        // We have something that could be a valid identifier
        return new ValueToken<>(this.line, Identifier, identifier.toString());
    }

    private Token handleString() {
        this.consumeChar(); // Skip the first

        StringBuilder lit = new StringBuilder();

        // A string should only span 1 line and should always be terminated
        while (this.currChar != '"' && this.currChar != '\n' && this.position < this.source.length()) {
            lit.append(this.currChar);
            this.consumeChar();
        }

        // File ended without string termination
        if (this.currChar != '"') {
            this.hadError = true;
            IllegalToken t = new IllegalToken("Unterminated String: ", lit.toString(), this.line);
            errorList.add(t);
            return t;
        }

        consumeChar();
        return new ValueToken<>(this.line, String, lit.toString());
    }

    private Token handleSlash() {
        this.consumeChar();
        if ('/' == this.currChar) {
            while (this.currChar != '\n') {
                this.consumeChar();
            }

            return nextToken();
        } else if ('=' == this.currChar) {
            return new SimpleToken(this.line, Slash_Equal);
        } else {
            return new SimpleToken(this.line, Slash);
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

    private Token handleAmperAndSlash() {
        char prev = this.currChar;
        this.consumeChar();

        if (prev == '&') {
            if (this.currChar == prev) {
                this.consumeChar();
                return new SimpleToken(this.line, And);
            } else {
                return new SimpleToken(this.line, BitwiseAnd);
            }
        } else {
            if (this.currChar == prev) {
                this.consumeChar();
                return new SimpleToken(this.line, Or);
            } else {
                return new SimpleToken(this.line, BitwiseOr);
            }
        }
    }
}
