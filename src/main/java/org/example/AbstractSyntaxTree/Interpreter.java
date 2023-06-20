package org.example.AbstractSyntaxTree;

import org.example.Lexer.TokenUtils;

public class Interpreter implements ExpressionVisitor<Object> {
    static class RuntimeError extends RuntimeException {
        final TokenUtils.Token token;

        RuntimeError(TokenUtils.Token token, String message) {
            super(message);
            this.token = token;
        }
    }

    private boolean hadError = false;

    public String interpret(Expression expr) {
        try {
            Object value = evaluate(expr);
            return stringify(value);
        } catch (RuntimeError err) {
            this.hadError = true;
            return String.format("[ INTERPRETER ERROR ]: %s on line %d", err.getMessage(), err.token.getPos());
        }
    }

    public boolean hadError() {
        return this.hadError;
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
                    return (String) left + (String) right;
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
}
