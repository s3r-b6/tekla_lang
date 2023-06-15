package org.example;

import static org.example.Lexer.TokenUtils.tokenPartialEq;
import static org.example.Lexer.TokenUtils.tokenEq;
import static org.example.Lexer.TokenUtils.isEOF;
import static org.example.Lexer.TokenUtils.TokenType.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.example.Lexer.IllegalToken;
import org.example.Lexer.Lexer;
import org.example.Lexer.SimpleToken;
import org.example.Lexer.TokenUtils.Token;
import org.example.Lexer.TokenUtils.TokenType;
import org.example.Lexer.ValueToken;
import org.junit.jupiter.api.Test;

public class LexerTests {
    public static void printTestInfo(String desc, String src) {
        String BLUE = "\033[1;94m";
        String NO_COLOR = "\033[0m";
        System.out.printf("\n[%sTEST%s] Testing %s\n", BLUE, NO_COLOR, desc);
        if (!src.equals("")) {
            System.out.printf("  [%sSOURCE_START%s]\n", BLUE, NO_COLOR);
            System.out.printf("    %s\n", src);
            System.out.printf("  [%sSOURCE_END%s]\n", BLUE, NO_COLOR);
        }
    }


    @Test
    public void testCorrectSimpleTokens() {
        String src = "(){}+ =,;";
        Lexer lex = new Lexer(src);

        printTestInfo("parses simple tokens", src);

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
            String errorMsg =
                    "Expected: " + expected.getTokenType() + " Got: " + token.getTokenType();
            assertTrue(tokenPartialEq(token, expected), errorMsg);
        }
    }

    @Test
    public void testIgnoresComments() {
        String src = "//Test Comment\n  ()  {}\n+ =,;";
        Lexer lex = new Lexer(src);

        printTestInfo("ignores comments", src);

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
            String errorMsg =
                    "Expected: " + expected.getTokenType() + " Got: " + token.getTokenType();
            assertTrue(tokenPartialEq(token, expected), errorMsg);
        }
    }

    @Test
    public void testIgnoresWhiteSpace() {
        String src = "  ()  {}\n+=,;";
        Lexer lex = new Lexer(src);

        printTestInfo("ignores whitespace", src);

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
            String errorMsg =
                    "Expected: " + expected.getTokenType() + " Got: " + token.getTokenType();
            assertTrue(tokenPartialEq(token, expected), errorMsg);
        }
    }

    @Test
    public void testSplitsIdentifiers() {
        String src = "let for if aaaaaa_aaaa";

        printTestInfo("parsing of", src);

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
                expectedMsg = "Expected: '" + expected.getTokenType() + "' value: '" + vt.getValue()
                        + "'";
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
    public void testCanParseCode() {
        String src = """
                let ten = 10;

                    let add = func(x, y) {
                      x + y;
                    };

                    let result = add(five, ten);
                    !-/*5;
                    5 < 10 > 5;
                    `

                    // [...]
                            """;

        printTestInfo("parses more complex strings", src);

        Lexer lex = new Lexer(src);

        ArrayList<Token> tokens = lex.readUntilEOF();

        TokenType[] expectedTypes = {Let, Identifier, Equal, Integer, Semicolon, Let, Identifier,
                Equal, Function, LParen, Identifier, Comma, Identifier, RParen, LBrace, Identifier,
                Plus, Identifier, Semicolon, RBrace, Semicolon, Let, Identifier, Equal, Identifier,
                LParen, Identifier, Comma, Identifier, RParen, Semicolon, Bang, Minus, Slash,
                Illegal, Integer, Semicolon, Integer, Less, Integer, Greater, Integer, Semicolon,
                Illegal, EOF};


        for (int i = 0; i < tokens.size(); i++) {
            TokenType tokenType = tokens.get(i).getTokenType();
            TokenType expectedType = expectedTypes[i];
            String failMsg = "\n\s\s[FAIL: " + i + "] Type " + tokenType.toString()
                    + " should match: " + expectedType;

            assertEquals(tokenType, expectedType, failMsg);
        }
    }

    @Test
    public void testPartEqInIdentifiers() {
        Lexer lex = new Lexer("aaa");
        Token token = lex.nextToken();

        printTestInfo("partial comparation of tokens", "aaa");

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

        printTestInfo("parses illegal tokens", "#~##");

        for (int i = 0; i < 4; i++) {
            errors.add((IllegalToken) lex.nextToken());
        }

        lex.printErrors();
        assertEquals(4, errors.size());
    }

    @Test
    public void testHandlesStrings() {
        Lexer lex = new Lexer("\"test\"");

        printTestInfo("handles a single String", "\"test\"");

        ValueToken tok = (ValueToken) lex.nextToken();
        ValueToken tok2 = new ValueToken(String, "test");
        assertTrue(tokenEq(tok, tok2));
    }

    @Test
    public void testHandlesWrongStrings() {
        Lexer lex = new Lexer("\"test");

        printTestInfo("handles a single non-terminated string", "\"test");

        Token tok = lex.nextToken();
        assertTrue(tok instanceof IllegalToken);
        assertTrue(tokenPartialEq(tok, new IllegalToken()));
    }

    @Test
    public void testHandlesIntegers() {
        Lexer lex = new Lexer("1234 1234   //test \n 1234");

        printTestInfo("hanles integers", "1234 1234   //test \n 1234");

        Token t = lex.nextToken();

        int i = 0;
        while (!isEOF(t)) {
            t = lex.nextToken();
            tokenEq(t, new ValueToken(Integer, "1234"));
            i += 1;
        }

        assertEquals(3, i); // There are 3 tokens (3 integers)
    }

    @Test
    public void testHandlesWrongIntegers() {
        Lexer lex = new Lexer("1234aaaa   1233! 12345");

        printTestInfo("handles invalid integers", "1234aaaa   1233! 12345");

        for (int i = 0; i < 2; i++) {
            Token tok = lex.nextToken();
            assertTrue(tok instanceof IllegalToken);
            assertTrue(tokenPartialEq(tok, new IllegalToken()));
        }

        assertTrue(lex.nextToken() instanceof ValueToken);
        assertTrue(tokenEq(lex.nextToken(), new SimpleToken(EOF)));
        assertTrue(tokenEq(lex.nextToken(), new SimpleToken(EOF)));

        lex.printErrors();
    }

    @Test
    public void testReadsUntilEOF() {
        Lexer lex = new Lexer("asfjlkqwjkl   =  let ");

        printTestInfo("reads EOF when pos >= length of src", "");
        Token t = lex.nextToken();

        int i = 0;
        while (!isEOF(t)) {
            t = lex.nextToken();
            i += 1;
        }

        assertEquals(3, i); // There are 3 tokens: an ident, an equals and a let
    }
}
