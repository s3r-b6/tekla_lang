package org.example;

public class TokenUtils {
    public enum TokenType {
        Illegal, EOF,

        Identifier,

        Integer, String,

        For, If, Else,
        While, Return,
        Function, Let,

        True, False, Bang,

        Greater, Less, Equal, Plus, Minus, Slash,
        Greater_Equal, Less_Equal, Equal_Equal,
        Plus_Equal, Minus_Equal, Slash_Equal,
        Not_Equal,

        Comma, Semicolon,

        LParen, RParen,
        LBrace, RBrace,
    }

    interface Token {
        public TokenType getTokenType();
    }

    public static boolean tokenPartialEq(Token t1, Token t2) {
        return (t1.getTokenType() == t2.getTokenType());
    }

    public static boolean tokenEq(Token t1, Token t2) {
        boolean areOfSameClass = (t1 instanceof SimpleToken && t2 instanceof SimpleToken) ||
                (t1 instanceof ValueToken && t2 instanceof ValueToken);

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
}
