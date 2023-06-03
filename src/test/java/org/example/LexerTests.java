package org.example;

import static org.example.TokenUtils.tokenPartialEq;
import static org.example.TokenUtils.tokenEq;
import static org.example.TokenUtils.TokenType.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;

import org.example.TokenUtils.Token;
import org.junit.jupiter.api.Test;

public class LexerTests {
    @Test
    public void testCorrectSimpleTokens() {
        String src = "(){}+ =,;";
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
            assertTrue(tokenPartialEq(token, expected), errorMsg);
        }
    }

    @Test
    public void testIgnoresComments() {
        String src = "//Test Comment\n  ()  {}\n+ =,;";
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
            assertTrue(tokenPartialEq(token, expected), errorMsg);
        }
    }

    @Test
    public void testIgnoresWhiteSpace() {
        String src = "  ()  {}\n+=,;";
        Lexer lex = new Lexer(src);

        ArrayList<Token> expectedTokens = new ArrayList<>();
        expectedTokens.add(new SimpleToken(LParen));
        expectedTokens.add(new SimpleToken(RParen));
        expectedTokens.add(new SimpleToken(LBrace));
        expectedTokens.add(new SimpleToken(RBrace));
        expectedTokens.add(new SimpleToken(Plus_Equal));
        expectedTokens.add(new SimpleToken(Equal));
        expectedTokens.add(new SimpleToken(Comma));
        expectedTokens.add(new SimpleToken(Semicolon));

        for (Token expected : expectedTokens) {
            Token token = lex.nextToken();
            String errorMsg = "Expected: " + expected.getTokenType() + " Got: " + token.getTokenType();
            assertTrue(tokenPartialEq(token, expected), errorMsg);
        }
    }

    @Test
    public void testSplitsIdentifiers() {
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
            assertTrue(tokenEq(token, expected), errorMsg);
        }
    }

    @Test
    public void testPartEqInIdentifiers() {
        Lexer lex = new Lexer("aaa");
        Token token = lex.nextToken();

        ValueToken expected = new ValueToken(Identifier, "bbb");

        String expectedMsg = "Expected: '" + expected.getTokenType() + "'";
        String gotMsg = "Got: '" + token.getTokenType() + "'";

        if (!(token instanceof ValueToken)) {
            fail("'aaa' should be a ValueToken");
        }

        String errorMsg = expectedMsg + " " + gotMsg;
        assertTrue(tokenPartialEq(token, expected), errorMsg);
    }

    @Test
    public void testReturnsIllegalTokens() {
        Lexer lex = new Lexer("#~##");
        ArrayList<IllegalToken> errors = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            errors.add((IllegalToken) lex.nextToken());
        }

        errors.forEach(e -> e.print());

        assertTrue(errors.size() == 4);
    }

    @Test
    public void testPrintsErrors() {
        Lexer lex = new Lexer("#~##");
        for (int i = 0; i < 4; i++) {
            lex.nextToken();
        }
        lex.printErrors();
    }

    @Test
    public void testHandlesStrings() {
        Lexer lex = new Lexer("\"test\"");
        ValueToken tok = (ValueToken) lex.nextToken();
        ValueToken tok2 = new ValueToken(String, "test");
        assertTrue(tok instanceof ValueToken);
        assertTrue(tokenEq(tok, tok2));
    }

    @Test
    public void testHandlesWrongStrings() {
        Lexer lex = new Lexer("\"test");
        Token tok = lex.nextToken();
        assertTrue(tok instanceof IllegalToken);
        assertTrue(tokenPartialEq(tok, new IllegalToken('\0', 0)));
    }

    @Test
    public void testHandlesIntegers() {
        Lexer lex = new Lexer("1234 1234   //test \n 1234");

        Token t = lex.nextToken();

        int i = 0;
        while (!tokenEq(t, new SimpleToken(EOF))) {
            t = lex.nextToken();
            tokenEq(t, new ValueToken(Integer, "1234"));
            i += 1;
        }

        assertTrue(i == 3); // There are 3 tokens (3 integers)
    }

    public void testReadsUntilEOF() {
        Lexer lex = new Lexer("asfjlkqwjkl   =  let ");
        Token t = lex.nextToken();

        int i = 0;
        while (!tokenEq(t, new SimpleToken(EOF))) {
            t = lex.nextToken();
            i += 1;
        }

        assertTrue(i == 3); // There are 3 tokens: an ident, an equals and a let
    }
}
