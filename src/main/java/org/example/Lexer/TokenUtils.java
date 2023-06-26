package org.example.Lexer;

import java.util.HashMap;

public class TokenUtils {
    public static boolean tokenPartialEq(Token t1, Token t2) {
        return (t1.getTokenType() == t2.getTokenType());
    }

    public static boolean tokenEq(Token t1, Token t2) {
        boolean areOfSameClass = (t1 instanceof SimpleToken && t2 instanceof SimpleToken)
                || (t1 instanceof ValueToken && t2 instanceof ValueToken);

        if (areOfSameClass) {
            if (t1 instanceof SimpleToken) {
                return tokenPartialEq(t1, t2);
            }

            ValueToken vt1 = (ValueToken) t1;
            ValueToken vt2 = (ValueToken) t2;

            return tokenPartialEq(vt1, vt2) && vt1.getValue().equals(vt2.getValue());
        }

        return false;
    }

    public static boolean isAlphabetic(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    public static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    public static boolean isEOF(Token t) {
        return (t instanceof SimpleToken && t.getTokenType() == TokenType.EOF);
    }

    public static HashMap<String, TokenType> createKeywordMap() {
        HashMap<String, TokenType> keywords = new HashMap<>();
        keywords.put("let", TokenType.Let);
        keywords.put("if", TokenType.If);
        keywords.put("else", TokenType.Else);
        keywords.put("for", TokenType.For);
        keywords.put("func", TokenType.Function);
        keywords.put("while", TokenType.While);
        keywords.put("return", TokenType.Return);
        keywords.put("true", TokenType.True);
        keywords.put("false", TokenType.False);
        keywords.put("print", TokenType.Print);
        keywords.put("break", TokenType.Break);
        keywords.put("continue", TokenType.Continue);
        keywords.put("nil", TokenType.Nil);
        return keywords;
    }

    public enum TokenType {
        Illegal, EOF,

        Let, Identifier, Function, Print,

        Integer, String, True, False,

        For, If, Else,
        While, Return,
        Bang,

        Break, Continue,

        And, Or, BitwiseAnd, BitwiseOr,

        Greater, Less, Equal, Plus, Minus, Slash, Star,
        Greater_Equal, Less_Equal, Equal_Equal, Star_Equal,
        Plus_Equal, Minus_Equal, Slash_Equal,
        Not_Equal, Nil,

        Comma, Semicolon,
        LParen, RParen, LBrace, RBrace,
    }

    public interface Token {
        TokenType getTokenType();

        int getPos();

        void print();
    }
}
