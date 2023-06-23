package org.example.AbstractSyntaxTree;

import org.example.Lexer.ValueToken;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public abstract class Statement {

    public abstract Void accept(StatementVisitor visitor);

    public abstract String toString();

    static class LetStatement extends Statement {
        ValueToken<String> name;
        Expression initializer;

        LetStatement(ValueToken<String> name, Expression init) {
            this.name = name;
            this.initializer = init;
        }

        @Override
        public Void accept(StatementVisitor visitor) {
            visitor.visitLetStatement(this);
            return null;
        }

        @Override
        public String toString() {
            AstPrinter printer = new AstPrinter();
            return String.format("Let statement: name: %s, initializer: %s", name.getValue(), printer.print(initializer));
        }
    }


    static class ExpressionStatement extends Statement {
        Expression expr;

        public ExpressionStatement(Expression expr) {
            this.expr = expr;
        }

        @Override
        public Void accept(StatementVisitor visitor) {
            return visitor.visitExpressionStatement(this);
        }

        @Override
        public String toString() {
            AstPrinter printer = new AstPrinter();
            return String.format("Expression statement: expression: %s", printer.print(expr));
        }
    }

    static class PrintStatement extends Statement {
        Expression expr;

        public PrintStatement(Expression expr) {
            this.expr = expr;
        }

        @Override
        public Void accept(StatementVisitor visitor) {
            return visitor.visitPrintStatement(this);
        }

        @Override
        public String toString() {
            AstPrinter printer = new AstPrinter();
            return String.format("Print statement: expression: %s", printer.print(expr));
        }
    }

    static class BlockStatement extends Statement {
        List<Statement> statementList;

        public BlockStatement(List<Statement> statements) {
            this.statementList = statements;
        }

        @Override
        public Void accept(StatementVisitor visitor) {
            return visitor.visitBlockStatement(this);
        }

        @Override
        public String toString() {
            StringBuilder blockString = new StringBuilder();
            blockString.append("Block statement: ");

            for (Statement st : statementList) blockString.append("\n").append(st.toString());

            return blockString.toString();
        }
    }
}
