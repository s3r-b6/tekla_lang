package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.example.AbstractSyntaxTree.AstPrinter;
import org.example.AbstractSyntaxTree.Expression;
import org.example.AbstractSyntaxTree.Interpreter;
import org.example.AbstractSyntaxTree.Parser;
import org.example.Lexer.Lexer;
import org.example.Lexer.SimpleToken;
import org.example.Lexer.TokenUtils;
import org.example.Lexer.TokenUtils.Token;

public class REPL {
    private static final Interpreter interpreter = new Interpreter();

    public static void main(String[] args) {
        // Interactive prompt
        if (args.length == 0) interactivePrompt();
        else if (args.length == 1) { // Read from file

        } else { // Bad usage
            throw new RuntimeException("Invalid args (use with no args or with a file)");
        }

    }

    private static void interactivePrompt() {
        System.out.println("Welcome to the Tekla REPL. Start inputting your commands.\nWrite EOF to end inputting commands\n\n");
        while (true) {
            List<Token> tokens = getTokensFromUserInput();

            if (tokens == null) break;

            tokens.forEach(Token::print);
            if (tokens.size() <= 2) continue;

            Parser parser = new Parser(tokens);
            Expression expr = parser.parseNext();

            if (parser.hadErrors()) {
                parser.printErrors();
            } else {
                System.out.printf("[EXPRESSION]: %s %n\t[RESULT]: %s%n", new AstPrinter().print(expr), interpreter.interpret(expr));
            }
        }
    }

    private static List<Token> getTokensFromUserInput() {
        List<Token> tokens = new ArrayList<>();
        Scanner sc = new Scanner(System.in);
        System.out.print(">>> ");
        String line = sc.nextLine();

        if (line.equals("exit")) return null;

        Lexer lex = new Lexer(line);
        List<Token> lineTokens = lex.readSequenceOfTokens();

        if (lex.hadError()) lex.printErrors();
        else tokens.addAll(lineTokens);

        tokens.add(new SimpleToken(0, TokenUtils.TokenType.EOF));
        return tokens;
    }
}
