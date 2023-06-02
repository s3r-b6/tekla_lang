package org.example;

import static org.example.TokenType.*;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.example.Lexer.*;

import org.junit.Test;

public class Tests {
    @Test
    public void createsCorrectSimpleTokens() {
        String src = "(){}+=,;";
        Lexer lex = new Lexer(src);

        ArrayList<Token> expectedTokens = new ArrayList<>();
        expectedTokens.add(new SimpleToken(LParen));
        expectedTokens.add(new SimpleToken(RParen));
        expectedTokens.add(new SimpleToken(LBrace));
        expectedTokens.add(new SimpleToken(RBrace));
        expectedTokens.add(new SimpleToken(Plus));
        expectedTokens.add(new SimpleToken(Equal));
        expectedTokens.add(new SimpleToken(Comma));
        expectedTokens.add(new SimpleToken(Semicolon));

        for (Token expected : expectedTokens) {
            Token token = lex.nextToken();
            String errorMsg = "Expected: " + expected.getTokenType() + " Got: " + token.getTokenType();
            assertTrue(errorMsg, lex.tokenPartialEq(token, expected));
        }
    }

    @Test
    public void ignoresComments() {
        String src = "//Test Comment\n  ()  {}\n+=,;";
        Lexer lex = new Lexer(src);

        ArrayList<Token> expectedTokens = new ArrayList<>();
        expectedTokens.add(new SimpleToken(LParen));
        expectedTokens.add(new SimpleToken(RParen));
        expectedTokens.add(new SimpleToken(LBrace));
        expectedTokens.add(new SimpleToken(RBrace));
        expectedTokens.add(new SimpleToken(Plus));
        expectedTokens.add(new SimpleToken(Equal));
        expectedTokens.add(new SimpleToken(Comma));
        expectedTokens.add(new SimpleToken(Semicolon));

        for (Token expected : expectedTokens) {
            Token token = lex.nextToken();
            String errorMsg = "Expected: " + expected.getTokenType() + " Got: " + token.getTokenType();
            assertTrue(errorMsg, lex.tokenPartialEq(token, expected));
        }
    }

    @Test
    public void ignoresWhiteSpace() {
        String src = "  ()  {}\n+=,;";
        Lexer lex = new Lexer(src);

        ArrayList<Token> expectedTokens = new ArrayList<>();
        expectedTokens.add(new SimpleToken(LParen));
        expectedTokens.add(new SimpleToken(RParen));
        expectedTokens.add(new SimpleToken(LBrace));
        expectedTokens.add(new SimpleToken(RBrace));
        expectedTokens.add(new SimpleToken(Plus));
        expectedTokens.add(new SimpleToken(Equal));
        expectedTokens.add(new SimpleToken(Comma));
        expectedTokens.add(new SimpleToken(Semicolon));

        for (Token expected : expectedTokens) {
            Token token = lex.nextToken();
            String errorMsg = "Expected: " + expected.getTokenType() + " Got: " + token.getTokenType();
            assertTrue(errorMsg, lex.tokenPartialEq(token, expected));
        }
    }
}
