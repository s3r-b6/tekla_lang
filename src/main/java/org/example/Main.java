package org.example;

import java.util.ArrayList;
import java.util.List;
import org.example.TokenUtils.Token;

public class Main {
    public static void main(String[] args) {
        List<Token> tokens = new ArrayList<>();

        Lexer lex = new Lexer("test test let a for + - =");
        List<Token> lineTokens = lex.readUntilNewlineOrEOF();

        if (lex.hadError()) {
            lex.printErrors();
        } else {
            tokens.addAll(lineTokens);
            tokens.forEach(t -> t.print()); // DEBUG
        }
    }
}
