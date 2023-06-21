package org.example.AbstractSyntaxTree;

public interface StatementVisitor {
    Void visitExpressionStatement(Statement.ExpressionStatement statement);

    Void visitPrintStatement(Statement.PrintStatement statement);

    Object visitVarStatement(Statement.VarStatement varStatement);

    Void visitLetStatement(Statement.LetStatement letStatement);
}
