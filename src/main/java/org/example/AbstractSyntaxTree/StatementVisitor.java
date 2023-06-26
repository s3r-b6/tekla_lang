package org.example.AbstractSyntaxTree;

public interface StatementVisitor {
    Void visitExpressionStatement(Statement.ExpressionStatement statement);

    Void visitPrintStatement(Statement.PrintStatement statement);

    Void visitLetStatement(Statement.LetStatement letStatement);

    Void visitBlockStatement(Statement.BlockStatement blockStatement) throws Interpreter.ControlFlow;

    Void visitIfStatement(Statement.IfStatement ifStatement) throws Interpreter.ControlFlow;

    Void visitBreakStatement(Statement.BreakStatement breakStatement) throws Interpreter.ControlFlow;

    Void visitContinueStatement(Statement.ContinueStatement continueStatement) throws Interpreter.ControlFlow;

    Void visitWhileStatement(Statement.WhileStatement whileStatement);

}
