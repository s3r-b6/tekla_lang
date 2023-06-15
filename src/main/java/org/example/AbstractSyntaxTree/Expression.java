package org.example.AbstractSyntaxTree;


import org.example.Lexer.TokenUtils;

public abstract class Expression {

    abstract <R> R accept(ExpressionVisitor<R> visitor);

    public static class UnaryExpression extends Expression {
        final TokenUtils.Token operator;
        final Expression right;

        public UnaryExpression(TokenUtils.Token operator, Expression right) {
            this.right = right;
            this.operator = operator;
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitUnary(this);
        }
    }

    public static class LiteralExpression extends Expression {
        final Object value;

        public LiteralExpression(Object value) {
            this.value = value;
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitLiteral(this);
        }
    }

    public static class BinaryExpression extends Expression {
        final Expression left;
        final TokenUtils.Token operator;
        final Expression right;

        public BinaryExpression(Expression left, TokenUtils.Token operator, Expression right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitBinary(this);
        }
    }

    public static class GroupingExpression extends Expression {
        Expression expr;

        public GroupingExpression(Expression expr) {
            this.expr = expr;
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitGrouping(this);
        }

    }
}