package org.example.AbstractSyntaxTree;

public interface ExpressionVisitor<R> {
    R visitAssignExpression(Expression.AssignExpression assignExpr);

    R visitVarExpression(Expression.VarExpression varExpr);

    R visitBinary(Expression.BinaryExpression binExpr);

    R visitUnary(Expression.UnaryExpression unarExpr);

    R visitGrouping(Expression.GroupingExpression groupExpr);

    R visitLiteral(Expression.LiteralExpression litExpr);

}
