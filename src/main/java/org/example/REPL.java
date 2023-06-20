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
        if (args.length == 0) { // Interactive prompt
            System.out.println("Welcome to the Tekla REPL. Start inputting your commands.\nWrite EOF to end inputting commands\n\n");
            List<Token> tokens = getTokensFromUserInput();

            if (tokens.size() < 3) {
                System.out.println("[ERROR] Your input was too small.");
                tokens.forEach(Token::print);
            } else {
                Parser parser = new Parser(tokens);
                Expression expr = parser.parse();

                if (parser.hadErrors()) {
                    parser.printErrors();
                } else {
                    System.out.println(new AstPrinter().print(expr));
                    System.out.println(interpreter.interpret(expr));
                }
            }
        } else if (args.length == 1) { // Read from file

        } else { // Bad usage
            throw new RuntimeException("Invalid args (use with no args or with a file)");
        }
    }

    private static List<Token> getTokensFromUserInput() {
        List<Token> tokens = new ArrayList<>();
        while (true) {
            Scanner sc = new Scanner(System.in);
            System.out.print(">>> ");
            String line = sc.nextLine();


            Lexer lex = new Lexer(line.replace("exit;", ""));
            List<Token> lineTokens = lex.readSequenceOfTokens();

            if (lex.hadError()) {
                lex.printErrors();
            } else {
                tokens.addAll(lineTokens);
                lineTokens.forEach(Token::print); // DEBUG
                if (line.endsWith("exit")) break;
            }
        }
        return tokens;
    }
}
