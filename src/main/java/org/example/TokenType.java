package org.example;

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
