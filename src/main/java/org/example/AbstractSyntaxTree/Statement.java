package org.example.AbstractSyntaxTree;

import org.example.Lexer.ValueToken;

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
            if (this.initializer != null) {
                return String.format("Let statement: name: %s, initializer: %s", name.getValue(), printer.print(initializer));
            }
            return String.format("Let statement: name: %s, initializer: null", name.getValue());
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

    static class IfStatement extends Statement {
        Expression condit;
        Statement thenBranch;
        Statement elseBranch;

        IfStatement(Expression condit, Statement thenBranch) {
            this.condit = condit;
            this.thenBranch = thenBranch;
            this.elseBranch = null;
        }

        IfStatement(Expression condit, Statement thenBranch, Statement elseBranch) {
            this.condit = condit;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }


        @Override
        public Void accept(StatementVisitor visitor) {
            return visitor.visitIfStatement(this);
        }

        @Override
        public String toString() {
            StringBuilder blockString = new StringBuilder();
            blockString.append("If statement: ");

            blockString.append("\n").append(this.condit);
            blockString.append("\n").append(this.thenBranch);

            if (elseBranch != null) {
                blockString.append("\n").append(this.elseBranch);
            }

            return blockString.toString();
        }
    }

    static class WhileStatement extends Statement {
        Expression condition;
        Statement body;

        WhileStatement(Expression expr, Statement body) {
            this.condition = expr;
            this.body = body;
        }

        @Override
        public Void accept(StatementVisitor visitor) {
            visitor.visitWhileStatement(this);
            return null;
        }

        @Override
        public String toString() {
            AstPrinter printer = new AstPrinter();
            return String.format("While statement: condition: %s body: %s", printer.print(condition), body.toString());
        }
    }
}
