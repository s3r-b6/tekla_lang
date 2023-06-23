package org.example.AbstractSyntaxTree;

import org.example.Lexer.TokenUtils;

import java.util.ArrayList;
import java.util.List;

public class Interpreter implements ExpressionVisitor<Object>, StatementVisitor {

    private final List<RuntimeError> errors = new ArrayList<>();
    private Environment env = new Environment();
    private boolean hadError = false;

    public void interpret(List<Statement> statements) {
        //If only an expression is inputted, evaluate it and print it as if it were inside a print st
        if (statements.size() == 1 && statements.get(0) instanceof Statement.ExpressionStatement) {
            Object val = evaluate(((Statement.ExpressionStatement) statements.get(0)).expr);
            System.out.println(stringify(val));
            return;
        }
        try {
            for (Statement st : statements) {
                //DEBUG: System.out.println(st.toString());
                execute(st);
            }
        } catch (RuntimeError err) {
            this.hadError = true;
            this.errors.add(err);
        }
    }

    public void print(List<Statement> statements) {
        try {
            for (Statement st : statements) System.out.println(st.toString());
        } catch (RuntimeError err) {
            this.hadError = true;
            this.errors.add(err);
        }
    }

    @Override
    public Void visitExpressionStatement(Statement.ExpressionStatement statement) {
        evaluate(statement.expr);
        return null;
    }

    @Override
    public Void visitPrintStatement(Statement.PrintStatement statement) {
        Object val = evaluate(statement.expr);
        System.out.println(stringify(val));
        return null;
    }

    @Override
    public Void visitLetStatement(Statement.LetStatement letStatement) {
        Object value = null;
        if (letStatement.initializer != null) {
            value = evaluate(letStatement.initializer);
        }

        env.define(letStatement.name.getValue(), value);
        return null;
    }

    @Override
    public Void visitBlockStatement(Statement.BlockStatement blockStatement) {
        executeBlock(blockStatement.statementList, new Environment(env));
        return null;
    }

    @Override
    public Void visitIfStatement(Statement.IfStatement ifStatement) {
        if (isTruthy(evaluate(ifStatement.condit))) {
            execute(ifStatement.thenBranch);
        } else if (ifStatement.elseBranch != null) {
            execute(ifStatement.elseBranch);
        }

        return null;
    }

    @Override
    public Void visitWhileStatement(Statement.WhileStatement whileStatement) {
        //TODO:
        return null;
    }

    private void executeBlock(List<Statement> statementList, Environment environment) {
        Environment prevEnv = this.env;

        try {
            this.env = environment;

            for (Statement st : statementList) execute(st);
        } finally {
            this.env = prevEnv;
        }
    }

    @Override
    public Object visitAssignExpression(Expression.AssignExpression assignExpression) {
        Object value = evaluate(assignExpression.value);
        env.assign(assignExpression.name, value);
        return value;
    }

    @Override
    public Object visitLogicalExpression(Expression.LogicalExpression logicExpr) {
        Object left = evaluate(logicExpr.left);

        //This could be done so that print(false || 2) printed 2 and not true,
        //but I don't really like that (a logical expression, when evaluated,
        //returns always true or false)
        if (logicExpr.operator.getTokenType() == TokenUtils.TokenType.Or) {
            if (isTruthy(left)) return true;
        } else {
            if (!isTruthy(left)) return false;
        }

        Object right = evaluate(logicExpr.right);
        return isTruthy(right);
    }

    @Override
    public Object visitVarExpression(Expression.VarExpression varExpr) {
        return env.get(varExpr.name);
    }

    @Override
    public Object visitBinary(Expression.BinaryExpression binExpr) {
        Object left = evaluate(binExpr.left);
        Object right = evaluate(binExpr.right);

        switch (binExpr.operator.getTokenType()) {
            case Minus -> {
                checkNumberOperands(binExpr.operator, left, right);
                return (double) left - (double) right;
            }
            case Slash -> {
                checkNumberOperands(binExpr.operator, left, right);
                return (double) left / (double) right;
            }
            case Star -> {
                checkNumberOperands(binExpr.operator, left, right);
                return (double) left * (double) right;
            }
            case Plus -> {
                if (left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                }

                if (left instanceof String && right instanceof String) {
                    return left + (String) right;
                }

                throw new RuntimeError(binExpr.operator, "Operands must be two numbers or two strings.");
            }
            case Greater -> {
                checkNumberOperands(binExpr.operator, left, right);
                return (double) left > (double) right;
            }
            case Greater_Equal -> {
                checkNumberOperands(binExpr.operator, left, right);
                return (double) left >= (double) right;
            }
            case Less -> {
                checkNumberOperands(binExpr.operator, left, right);
                return (double) left < (double) right;
            }
            case Less_Equal -> {
                checkNumberOperands(binExpr.operator, left, right);
                return (double) left <= (double) right;
            }

            case Not_Equal -> {
                checkNumberOperands(binExpr.operator, left, right);
                return !isEqual(left, right);
            }

            case Equal -> {
                checkNumberOperands(binExpr.operator, left, right);
                return isEqual(left, right);
            }
        }

        return null;
    }

    @Override
    public Object visitUnary(Expression.UnaryExpression unarExpr) {
        Object right = evaluate(unarExpr.right);

        switch (unarExpr.operator.getTokenType()) {
            case Minus -> {
                checkNumberOperand(unarExpr.operator, right);
                return -(double) right;
            }

            case Bang -> {
                return !isTruthy(right);
            }
        }

        return null;
    }

    @Override
    public Object visitGrouping(Expression.GroupingExpression groupExpr) {
        return evaluate(groupExpr.expr);
    }

    @Override
    public Object visitLiteral(Expression.LiteralExpression litExpr) {
        return litExpr.value;
    }

    private Object evaluate(Expression expr) {
        return expr.accept(this);
    }

    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean) object;

        return true;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;

        return a.equals(b);
    }

    private void checkNumberOperand(TokenUtils.Token operator, Object operand) {
        if (!(operand instanceof Double)) throw new RuntimeError(operator, "Operand must be a number");
    }

    private void checkNumberOperands(TokenUtils.Token operator, Object a, Object b) {
        if (!(a instanceof Double && b instanceof Double)) throw new RuntimeError(operator, "Operand must be a number");
    }

    private String stringify(Object object) {
        if (object == null) return "nil";

        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        return object.toString();
    }

    private void execute(Statement st) {
        st.accept(this);
    }

    public boolean hadError() {
        return this.hadError;
    }

    public void printErrors() {
        for (RuntimeError err : errors) err.printError();
    }

    static class RuntimeError extends RuntimeException {
        final String RED = "\033[1;91m";
        final String NO_COLOR = "\033[0m";

        final TokenUtils.Token token;

        RuntimeError(TokenUtils.Token token, String message) {
            super(message);
            this.token = token;
        }

        public void printError() {
            System.out.printf("  [%sINTERPRETER ERROR%s]: %s on line %d%n", RED, NO_COLOR, super.getMessage(), this.token.getPos());
        }
    }
}
