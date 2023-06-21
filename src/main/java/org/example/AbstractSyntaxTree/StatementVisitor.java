package org.example.AbstractSyntaxTree;

public interface StatementVisitor {
    Void visitExpressionStatement(Statement.ExpressionStatement statement);

    Void visitPrintStatement(Statement.PrintStatement statement);

    Void visitLetStatement(Statement.LetStatement letStatement);
}
