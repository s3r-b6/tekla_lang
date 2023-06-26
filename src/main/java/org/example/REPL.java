package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import org.example.AbstractSyntaxTree.*;
import org.example.Lexer.Lexer;
import org.example.Lexer.SimpleToken;
import org.example.Lexer.TokenUtils;
import org.example.Lexer.TokenUtils.Token;

public class REPL {
    private static final Interpreter interpreter = new Interpreter();

    public static void main(String[] args) {
        if (args.length == 0) interactivePrompt();
        else if (args.length == 1) { // Read from file

        } else { // Bad usage
            throw new RuntimeException("Invalid args (use with no args or with a file)");
        }

    }

    private static void interactivePrompt() {
        System.out.println("Welcome to the Tekla REPL. Start inputting your commands.\nWrite exit to end inputting commands\n");
        while (true) {
            List<Token> tokens = getTokensFromUserInput();

            if (tokens == null) break;
            //DEBUG: tokens.forEach(Token::print);

            run(tokens);
        }
    }

    private static void run(List<Token> tokens) {
        Parser parser = new Parser(tokens);
        List<Statement> statements = parser.parse();

        if (parser.hadErrors()) {
            parser.printErrors();
        } else {
            if (statements.size() > 0) {
                //interpreter.print(statements);
                interpreter.interpret(statements);
                if (interpreter.hadError()) interpreter.printErrors();
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
