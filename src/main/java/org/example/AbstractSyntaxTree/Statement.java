package org.example.AbstractSyntaxTree;

public abstract class Statement {

    abstract Void accept(StatementVisitor visitor);


    static class ExpressionStatement extends Statement {
        Expression expr;

        public ExpressionStatement(Expression expr) {
            this.expr = expr;
        }

        @Override
        public Void accept(StatementVisitor visitor) {
            return visitor.visitExpressionStatement(this);
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
    }
}
