package org.example.AbstractSyntaxTree;

public interface StatementVisitor {
    Void visitExpressionStatement(Statement.ExpressionStatement statement);

    Void visitPrintStatement(Statement.PrintStatement statement);

    Void visitLetStatement(Statement.LetStatement letStatement);

    Void visitBlockStatement(Statement.BlockStatement blockStatement);

    Void visitIfStatement(Statement.IfStatement ifStatement);

    Void visitWhileStatement(Statement.WhileStatement whileStatement);
}
