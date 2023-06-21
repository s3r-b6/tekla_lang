package org.example;

import org.example.AbstractSyntaxTree.AstPrinter;
import org.example.AbstractSyntaxTree.Expression;
import org.example.AbstractSyntaxTree.Interpreter;
import org.example.AbstractSyntaxTree.Parser;
import org.example.Lexer.Lexer;
import org.example.Lexer.SimpleToken;
import org.example.Lexer.TokenUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTests {

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
    public void testExpressions() {
        Expression expr = new Expression.BinaryExpression(
                new Expression.UnaryExpression(new SimpleToken(0, TokenUtils.TokenType.Minus), new Expression.LiteralExpression(123)),
                new SimpleToken(0, TokenUtils.TokenType.Plus),
                new Expression.GroupingExpression(new Expression.LiteralExpression(45.23))
        );
        printTestInfo("parses a binary expression statement", "The tokenized equivalent of: -123 + (45.23) ");

        assertEquals("(Plus (Minus 123) (group 45.23))", new AstPrinter().print(expr));
    }


    @Test
    public void testInterpreter() {
        String[] src = new String[]{
                "7 / 2 + 7 -4 *2",
                "2 * 2 * 2 -4 *2",
                "7 - 7 + 7 *1 -7",
                "15 - 2 + 7 -1 *2",
                "!true", "!false",
                "\"test\" + \"test\"",
                "!(!true)", "!(!false)"
        };
        String[] exp = new String[]{
                "2.5", "0", "0", "18",
                "false", "true", "testtest",
                "true", "false"

        };

        for (int i = 0; i < src.length; i++) {
            Lexer lex = new Lexer(src[i]);
            List<TokenUtils.Token> tokens = lex.readUntilEOF();

            Parser parser = new Parser(tokens);

            //Expression expr = parser.parse();
            Interpreter interpreter = new Interpreter();

            //assertEquals(exp[i], interpreter.interpret(expr));
        }
    }


    @Test
    public void testError() {
        String[] src = new String[]{
                "7-/ 2 + 7 -4 *2",
                "7---2 + 7 -4 *2",
                "7-/ 2 7 let -4 *2",
        };

        for (String str : src) {
            Lexer lex = new Lexer(str);
            List<TokenUtils.Token> tokens = lex.readUntilEOF();

            Parser parser = new Parser(tokens);

//            Expression expr = parser.parse();
            Interpreter interpreter = new Interpreter();
            //           interpreter.interpret(expr);
            assertTrue(interpreter.hadError());
        }
    }
}
