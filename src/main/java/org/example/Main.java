package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.example.Lexer.Lexer;
import org.example.Lexer.SimpleToken;
import org.example.Lexer.TokenUtils;
import org.example.Lexer.TokenUtils.Token;

public class Main {
    public static void main(String[] args) {
        List<Token> tokens = new ArrayList<>();
        if (args.length == 0) { // Interactive prompt
            System.out.println("Welcome to the Tekla REPL. Start inputting your commands.\nWrite EOF to end inputting commands\n\n");
            getTokensFromUserInput(tokens);
            if (tokens.size() < 3) {
                System.out.println("[ERROR] Yur input was too small.");
                tokens.forEach(Token::print);
            }

        } else if (args.length == 1) { // Read from file

        } else { // Bad usage
            throw new RuntimeException("Invalid args (use with no args or with a file)");
        }
    }

    private static void getTokensFromUserInput(List<Token> tokens) {
        while (true) {
            Scanner sc = new Scanner(System.in);
            String line = sc.nextLine();

            if (line.equals("EOF")) {
                tokens.add(new SimpleToken(TokenUtils.TokenType.EOF));
                break;
            }

            Lexer lex = new Lexer(line);

            List<Token> lineTokens = lex.readSequenceOfTokens();
            if (lex.hadError()) {
                lex.printErrors();
            } else {
                tokens.addAll(lineTokens);
                lineTokens.forEach(Token::print); // DEBUG
            }
        }
    }
}
