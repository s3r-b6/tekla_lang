package org.example.AbstractSyntaxTree;

public interface ExpressionVisitor<R> {
    R visitBinary(Expression.BinaryExpression binExpr);

    R visitUnary(Expression.UnaryExpression unarExpr);

    R visitGrouping(Expression.GroupingExpression groupExpr);

    R visitLiteral(Expression.LiteralExpression litExpr);

}
