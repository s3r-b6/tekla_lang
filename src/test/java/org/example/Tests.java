package org.example;

import static org.example.TokenUtils.tokenPartialEq;
import static org.example.TokenUtils.tokenEq;
import static org.example.TokenUtils.TokenType.*;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.example.TokenUtils.Token;
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
            assertTrue(errorMsg, tokenPartialEq(token, expected));
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
            assertTrue(errorMsg, tokenPartialEq(token, expected));
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
            assertTrue(errorMsg, tokenPartialEq(token, expected));
        }
    }

    @Test
    public void splitsIdentifiers() {
        String src = "let for if aaaaaa_aaaa";
        Lexer lex = new Lexer(src);
        ArrayList<Token> expectedTokens = new ArrayList<>();
        expectedTokens.add(new SimpleToken(Let));
        expectedTokens.add(new SimpleToken(For));
        expectedTokens.add(new SimpleToken(If));
        expectedTokens.add(new ValueToken(Identifier, "aaaaaa_aaaa"));

        for (Token expected : expectedTokens) {
            Token token = lex.nextToken();

            String expectedMsg, gotMsg;
            if (expected instanceof ValueToken) {
                ValueToken vt = (ValueToken) expected;
                expectedMsg = "Expected: '" + expected.getTokenType() + "' value: '" + vt.getValue() + "'";
            } else {
                expectedMsg = "Expected: '" + expected.getTokenType() + "'";
            }

            if (token instanceof ValueToken) {
                ValueToken vt = (ValueToken) token;
                gotMsg = "Got: '" + token.getTokenType() + "' value: '" + vt.getValue() + "'";
            } else {
                gotMsg = "Got: '" + token.getTokenType() + "'";
            }

            String errorMsg = expectedMsg + " " + gotMsg;
            assertTrue(errorMsg, tokenEq(token, expected));
        }
        assertTrue(true);
    }
}
