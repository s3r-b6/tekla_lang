package org.example.AbstractSyntaxTree;

public class AstPrinter implements ExpressionVisitor<String> {

    public String print(Expression expr) {
        return expr.accept(this);
    }

    @Override
    public String visitAssignExpression(Expression.AssignExpression assignExpr) {
        //TODO:
        return null;
    }

    @Override
    public String visitVarExpression(Expression.VarExpression varExpr) {
        //TODO:
        return null;
    }

    @Override
    public String visitBinary(Expression.BinaryExpression binExpr) {
        return parenthesize(binExpr.operator.getTokenType().toString(), binExpr.left, binExpr.right);
    }

    @Override
    public String visitUnary(Expression.UnaryExpression unarExpr) {
        return parenthesize(unarExpr.operator.getTokenType().toString(), unarExpr.right);
    }

    @Override
    public String visitGrouping(Expression.GroupingExpression groupExpr) {
        return parenthesize("group", groupExpr.expr);
    }

    @Override
    public String visitLiteral(Expression.LiteralExpression litExpr) {
        if (litExpr.value == null) return "nil";
        return litExpr.value.toString();
    }

    @Override
    public String visitLogicalExpression(Expression.LogicalExpression logicExp) {
        return null;
    }

    private String parenthesize(String name, Expression... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expression expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }
}
