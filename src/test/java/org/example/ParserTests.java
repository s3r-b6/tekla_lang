package org.example;

import org.example.AbstractSyntaxTree.AstPrinter;
import org.example.AbstractSyntaxTree.Expression;
import org.example.Lexer.SimpleToken;
import org.example.Lexer.TokenUtils;
import org.junit.jupiter.api.Test;

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
                new Expression.UnaryExpression(new SimpleToken(TokenUtils.TokenType.Minus), new Expression.LiteralExpression(123)),
                new SimpleToken(TokenUtils.TokenType.Plus),
                new Expression.GroupingExpression(new Expression.LiteralExpression(45.23))
        );
        printTestInfo("parses a binary expression statement", "The tokenized equivalent of: -123 + (45.23) ");

        assertEquals("(Plus (Minus 123) (group 45.23))", new AstPrinter().print(expr));
    }
}
