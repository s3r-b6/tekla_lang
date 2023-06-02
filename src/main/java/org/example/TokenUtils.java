package org.example;

public class TokenUtils {
    public enum TokenType {
        Illegal, EOF,

        Identifier,

        Integer, String,

        Greater, Less,
        Equal, Plus, Minus,
        Slash,
        Comma, Semicolon,
        LParen, RParen,
        LBrace, RBrace,
        Function, Let,
    }

    interface Token {
        public TokenType getTokenType();
    }

    public static boolean tokenPartialEq(Token t1, Token t2) {
        return (t1.getTokenType() == t2.getTokenType());
    }
}
