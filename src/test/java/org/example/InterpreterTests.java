package org.example;

import org.example.AbstractSyntaxTree.*;
import org.example.Lexer.Lexer;
import org.example.Lexer.SimpleToken;
import org.example.Lexer.TokenUtils;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTests {

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
                "print(7 / 2 + 7 -4 *2);",
                "print(2 * 2 * 2 -4 *2);",
                "print(7 - 7 + 7 *1 -7);",
                "print(15 - 2 + 7 -1 *2);",
                "print(!true);", "print(!false);",
                "print(\"test\" + \"test\");",
                "print(!(!true));", "print(!(!false));"
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

            List<Statement> statements = parser.parse();
            Interpreter interpreter = new Interpreter();
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            interpreter.interpret(statements);

            assertEquals(exp[i], outContent.toString().replace("\n", ""));
        }
    }


    @Test
    public void testInvalidOperations() {
        String[] src = new String[]{
                "print(7-/ 2);",
                "print(7-/ 2 7 -4 *2);",
        };

        for (String str : src) {
            Lexer lex = new Lexer(str);
            List<TokenUtils.Token> tokens = lex.readUntilEOF();

            Parser parser = new Parser(tokens);
            List<Statement> statements = parser.parse();
            assertTrue(parser.hadErrors());
            Interpreter interpreter = new Interpreter();
            //If it had errors, the interpreter should not run it, so naturally it throws
            assertThrows(NullPointerException.class, () -> interpreter.interpret(statements));
        }
    }

    @Test
    public void testAssignments() {
        String[] src = {
                "let x = 5; print(x+5);",
                "let x = 2; let x = 100; print(x);",
                "let x = 3 + 4 + 2; print(x-1);"
        };
        String[] exp = {
                "10\n",
                "100\n",
                "8\n"
        };
        for (int i = 0; i < src.length; i++) {
            Lexer lex = new Lexer(src[i]);

            List<TokenUtils.Token> tokens = lex.readUntilEOF();
            Parser parser = new Parser(tokens);

            List<Statement> statements = parser.parse();
            Interpreter interpreter = new Interpreter();
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            interpreter.interpret(statements);

            assertEquals(exp[i], outContent.toString());
        }
    }

    @Test
    public void testBlocks() {
        String src = """
                let a = 2;
                {
                    let a = 3;
                    {
                        let a = 4;
                        print(a);
                    }
                    print(a);
                }
                print(a);
                """;

        Lexer lex = new Lexer(src);
        Parser parser = new Parser(lex.readUntilEOF());
        Interpreter interpreter = new Interpreter();

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        interpreter.interpret(parser.parse());

        assertEquals("4\n3\n2\n", outContent.toString());
    }

    @Test
    public void testConditionalOr() {
        String[] src = {
                "print(false || 2);",
                "print(!false || 2);",
                "print(true || 2);",
                "print(!true|| 2);",
                "if(!false || false) { print(true); } else { print(false); }",
                "if(false || true) { print(true); } else { print(false); }",
                "if(false || !true) { print(true); } else { print(false); }",
                "if(!(!false) || !true) { print(true); } else { print(false); }",
        };

        String[] exp = {
                "true\n",
                "true\n",
                "true\n",
                "true\n",
                "true\n",
                "true\n",
                "false\n",
                "false\n"
        };

        for (int i = 0; i < src.length; i++) {
            Lexer lex = new Lexer(src[i]);

            List<TokenUtils.Token> tokens = lex.readUntilEOF();
            Parser parser = new Parser(tokens);

            List<Statement> statements = parser.parse();
            Interpreter interpreter = new Interpreter();
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            interpreter.interpret(statements);

            assertEquals(exp[i], outContent.toString());
        }
    }

    @Test
    public void testConditionalAnd() {
        String[] src = {
                "print(false && 2);",
                "print(!false && 2);",
                "print(true && 2);",
                "print(!true && 2);",
                "if(!false && true) { print(true); } else { print(false); }",
                "if(false && true) { print(true); } else { print(false); }",
                "if(!(!true) && !false) { print(true); } else { print(false); }",
                "if((true) && !false) { print(true); } else { print(false); }",
                "if(true) { print(2); } "
        };

        String[] exp = {
                "false\n",
                "true\n",
                "true\n",
                "false\n",
                "true\n",
                "false\n",
                "true\n",
                "true\n",
                "2\n",
        };

        for (int i = 0; i < src.length; i++) {
            Lexer lex = new Lexer(src[i]);

            List<TokenUtils.Token> tokens = lex.readUntilEOF();
            Parser parser = new Parser(tokens);

            List<Statement> statements = parser.parse();
            Interpreter interpreter = new Interpreter();
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            interpreter.interpret(statements);

            assertEquals(exp[i], outContent.toString());
        }
    }

    @Test
    public void testForLoop() {
        String src = "let a = 0; let temp; for (let b = 1; a < 10000; b = temp + b) {print(a);temp = a;a = b;}";
        String exp = """
                0
                1
                1
                2
                3
                5
                8
                13
                21
                34
                55
                89
                144
                233
                377
                610
                987
                1597
                2584
                4181
                6765
                """;

        Lexer lex = new Lexer(src);

        List<TokenUtils.Token> tokens = lex.readUntilEOF();
        Parser parser = new Parser(tokens);

        List<Statement> statements = parser.parse();
        Interpreter interpreter = new Interpreter();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        interpreter.interpret(statements);

        assertEquals(exp, outContent.toString());
    }

    @Test
    public void testControlFlow() {
        String[] src = {
                """
                let a = 2;
                while (true) {
                    print(a);
                    break;
                }
                """,
                """
                let a = 1;
                while (true) {
                    if (a == 4){
                        break;
                    }
                a = a + 1;
                print(a);
                }
                """,
                """
                let a = 1;
                while (a<4) {
                    a = a + 1;
                    print(a);
                }
                """,
                """
                for (let i =0; i<=10; i=i+1){ if (i==2 || i==3) { i=i+1; continue; } if (i == 9) { break; } print(i); }
                """
        };
        String[] exp = {
                """
                2
                """,
                """
                2
                3
                4
                """,
                """
                2
                3
                4
                """,
                """
                0
                1
                4
                5
                6
                7
                8
                """
        };

        for (int i = 0; i < src.length; i++) {
            Lexer lex = new Lexer(src[i]);

            List<TokenUtils.Token> tokens = lex.readUntilEOF();
            Parser parser = new Parser(tokens);
            Interpreter interpreter = new Interpreter();
            List<Statement> statements = parser.parse();
            if (parser.hadErrors()) parser.printErrors();
            else {
                ByteArrayOutputStream outContent = new ByteArrayOutputStream();
                System.setOut(new PrintStream(outContent));

                interpreter.interpret(statements);
                if (interpreter.hadError()) interpreter.printErrors();

                assertEquals(exp[i], outContent.toString());
            }
        }
    }
}