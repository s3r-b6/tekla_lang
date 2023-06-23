package org.example.AbstractSyntaxTree;

import org.example.Lexer.TokenUtils;
import org.example.Lexer.TokenUtils.TokenType;
import org.example.Lexer.ValueToken;

import java.util.ArrayList;
import java.util.List;


/**
 * This is a recursive descent parser
 */
public class Parser {
    private static class ParseError extends RuntimeException {
        private final String message;

        final String RED = "\033[1;91m";
        final String NO_COLOR = "\033[0m";

        ParseError(String message) {
            this.message = message;
        }

        public void printError() {
            System.out.printf("  [%sSYNTAX ERROR%s]: %s %n", RED, NO_COLOR, message);
        }
    }

    private final List<TokenUtils.Token> tokens;
    private final List<ParseError> errors;
    private int current = 0;

    private boolean hadErrors = false;

    public Parser(List<TokenUtils.Token> tokens) {
        this.tokens = tokens;
        this.errors = new ArrayList<>();
    }

    public List<Statement> parse() {
        List<Statement> statements = new ArrayList<>();

        while (!isAtEnd()) statements.add(declaration());

        return statements;
    }


    private Statement declaration() {
        try {
            if (match(TokenType.Let)) return letStatement();
            return statement();
        } catch (ParseError err) {
            errors.add(err);
            hadErrors = true;
            synchronize();
            return null;
        }
    }

    private Statement statement() throws ParseError {
        if (match(TokenType.Print)) return printStatement();
        if (match(TokenType.LBrace)) return new Statement.BlockStatement(blockStatement());
        return expressionStatement();
    }

    private Statement letStatement() throws ParseError {
        //If it is an Identifier, it has to be a ValueToken. This is kind of ugly, but it should not fail ever?
        ValueToken<String> name = (ValueToken<String>) consume(TokenType.Identifier, "Expected an identifier after Let statement.");

        Expression initializer = null;
        if (match(TokenType.Equal)) initializer = expression();

        consume(TokenType.Semicolon, "Expected a ';' after the variable declaration");
        return new Statement.LetStatement(name, initializer);
    }

    private Statement printStatement() throws ParseError {
        consume(TokenType.LParen, "Expected '(' after print statement)");
        Expression expr = expression();
        consume(TokenType.RParen, "Expected ')' after print statement)");
        consume(TokenType.Semicolon, "Expected ';' after print statement)");

        return new Statement.PrintStatement(expr);
    }

    private ArrayList<Statement> blockStatement() throws ParseError {
        ArrayList<Statement> statements = new ArrayList<>();

        while (!check(TokenType.RBrace) && !isAtEnd()) {
            statements.add(declaration()); //traverse the parser
        }

        consume(TokenType.RBrace, "Expected '}' at the end of the block");
        return statements;
    }

    private Statement expressionStatement() throws ParseError {
        Expression expr = expression();
        consume(TokenType.Semicolon, "Expected ';' after expression");
        return new Statement.ExpressionStatement(expr);
    }

    private Expression expression() throws ParseError {
        return assignment();
    }

    private Expression assignment() throws ParseError {
        Expression expr = equality();

        if (match(TokenType.Equal)) {
            TokenUtils.Token equals = previous();

            //Recursively parse the right side of the expression
            Expression val = assignment();

            if (expr instanceof Expression.VarExpression) {
                ValueToken<String> name = ((Expression.VarExpression) expr).name;
                return new Expression.AssignExpression(name, val);
            }

            throw error(equals, "Invalid assignment target.");
        }

        return expr;
    }

    private Expression equality() throws ParseError {
        Expression expr = comparison();

        while (match(TokenType.Not_Equal, TokenType.Equal_Equal)) {
            TokenUtils.Token op = previous();
            Expression right = comparison();
            expr = new Expression.BinaryExpression(expr, op, right);
        }

        return expr;
    }

    private Expression comparison() throws ParseError {
        Expression expr = term();

        while (match(TokenType.Greater, TokenType.Greater_Equal, TokenType.Less, TokenType.Less_Equal)) {
            TokenUtils.Token op = previous();
            Expression right = term();
            expr = new Expression.BinaryExpression(expr, op, right);
        }

        return expr;
    }

    private Expression term() throws ParseError {
        Expression expr = factor();

        while (match(TokenType.Minus, TokenType.Plus)) {
            TokenUtils.Token op = previous();

            Expression right = factor();
            expr = new Expression.BinaryExpression(expr, op, right);
        }

        return expr;
    }

    private Expression factor() throws ParseError {
        Expression expr = unary();

        while (match(TokenType.Slash, TokenType.Star)) {
            TokenUtils.Token op = previous();
            Expression right = unary();
            expr = new Expression.BinaryExpression(expr, op, right);
        }

        return expr;
    }

    private Expression unary() throws ParseError {
        if (match(TokenType.Bang, TokenType.Minus)) {
            TokenUtils.Token op = previous();
            Expression right = unary();
            return new Expression.UnaryExpression(op, right);
        }

        return primary();
    }

    private Expression primary() throws ParseError {
        if (match(TokenType.False)) return new Expression.LiteralExpression(false);
        if (match(TokenType.True)) return new Expression.LiteralExpression(true);
        if (match(TokenType.Nil)) return new Expression.LiteralExpression(null);

        if (match(TokenType.Integer, TokenType.String)) {
            ValueToken tok = (ValueToken) previous();
            return new Expression.LiteralExpression(tok.getValue());
        }

        //If we get here, this *could* be an identifier
        if (match(TokenType.Identifier)) {
            ValueToken<String> tok = (ValueToken<String>) previous();
            return new Expression.VarExpression(tok);
        }

        if (match(TokenType.LParen)) {
            Expression expr = expression();
            consume(TokenType.RParen, "Expected ')' after expression.");
            return new Expression.GroupingExpression(expr);
        }

        throw error(peek(), "Expected an expression");
    }

    /**
     * This method discards tokens until it finds what could be a statement
     */
    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().getTokenType() == TokenType.Semicolon) return;

            switch (peek().getTokenType()) {
                case Function, Let, For, If, While, Return, Print -> {
                    return;
                }
            }

            advance();
        }
    }

    private TokenUtils.Token consume(TokenType type, String message) throws ParseError {
        if (check(type)) return advance();

        throw error(peek(), message);
    }

    private ParseError error(TokenUtils.Token token, String message) throws ParseError {
        if (token.getTokenType() == TokenType.EOF) {
            throw new ParseError(" at end " + message);
        } else {
            throw new ParseError(message + " at line: " + token.getPos() + " on token: " + token.getTokenType());
        }
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }


    private boolean check(TokenType type) {
        if (isAtEnd()) return false;

        return peek().getTokenType() == type;
    }

    private boolean isAtEnd() {
        return peek().getTokenType() == TokenType.EOF;
    }

    private TokenUtils.Token advance() {
        if (!isAtEnd()) current += 1;
        return previous();
    }

    private TokenUtils.Token peek() {
        return tokens.get(current);
    }

    private TokenUtils.Token previous() {
        return tokens.get(current - 1);
    }

    public boolean hadErrors() {
        return this.hadErrors;
    }

    public void printErrors() {
        for (ParseError err : errors) err.printError();
    }
}
