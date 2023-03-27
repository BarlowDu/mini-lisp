package com.mini.lisp;



import com.mini.lisp.exception.SyntaxException;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Parser {


    private boolean match(Token token, TokenType... types) {
        for (TokenType type : types) {
            if (type == token.type) {
                return true;
            }
        }
        return false;
    }

    public List<Expr> stmt(List<Token> tokens) throws SyntaxException {
        List<Expr> exprs = new ArrayList<>();
        int startIndex = 0;
        int endIndex = tokens.size() - 1;
        int index = startIndex;
        Stack<Token> stack = new Stack<>();
        for (; index <= endIndex; index++) {
            Token token = tokens.get(index);
            if (match(token, TokenType.RIGHT_PAREN)) {
                if (stack.isEmpty()) {
                    throw new SyntaxException("Bad word", token.line);
                }
                while (!stack.isEmpty()) {
                    Token temp = stack.pop();
                    if (match(temp, TokenType.LEFT_PAREN)) {
                        break;
                    }
                }
                if (stack.isEmpty()) {
                    List<Token> subTokens = tokens.subList(startIndex, index + 1);
                    startIndex = index + 1;
                    exprs.add(expression(subTokens));
                }
            } else if (match(token, TokenType.LEFT_PAREN)) {
                stack.push(token);
            } else {
                if (stack.isEmpty()) {
                    startIndex = index + 1;
                    exprs.add(primary(token));
                }
            }


        }
        if (!stack.isEmpty()) {
            throw new SyntaxException("read-syntax: expected a `)` to close `(`", stack.elementAt(0).line);
        }

        return exprs;
    }

    private Expr expression(List<Token> tokens) throws SyntaxException {
        //最简形式:(incr)
        if (tokens.size() < 3) {
            throw new SyntaxException("arguments mismatch", tokens.get(0).line);
        }
        Token name = tokens.get(1);

        int index = 1;
        if (match(name, TokenType.RIGHT_PAREN,
                TokenType.INTEGER,
                TokenType.LONG,
                TokenType.DECIMAL,
                TokenType.STRING,
                TokenType.TRUE,
                TokenType.FALSE,
                TokenType.NULL)) {
            throw new SyntaxException("not a procedure", name.line);
        }
        Expr callName = null;
        if (match(name, TokenType.IDENTIFIER)) {
            if ("define".equals(name.lexeme)) {
                return define(tokens);
            } else if ("lambda".equals(name.lexeme)) {
                return lambda(tokens);
            }
            callName = primary(name);
            index = 2;
        }
        if (match(name, TokenType.LEFT_PAREN)) {
            Stack<Token> stack = new Stack<>();
            for (; index < tokens.size(); index++) {
                Token token = tokens.get(index);
                if (match(token, TokenType.LEFT_PAREN)) {
                    stack.push(token);
                } else if (match(token, TokenType.RIGHT_PAREN)) {
                    while (!stack.isEmpty()) {
                        Token temp = stack.pop();
                        if (match(temp, TokenType.LEFT_PAREN)) {
                            break;
                        }
                    }
                    if (stack.isEmpty()) {
                        callName = expression(tokens.subList(1, index + 1));
                        index++;
                        break;
                    }
                }


            }
        }
        //未获取到函数名或函数定义
        if (callName == null) {
            throw new SyntaxException("not a procedure; expected a procedure that can be applied to arguments", name.line);
        }
        if (callName instanceof Expr.Define) {
            throw new SyntaxException("define: not allowed in an expression context", name.line);
        }
        //TODO 会报错
        List<Expr> arguments = null;
        if (index >= tokens.size() - 1) {
            arguments = new ArrayList<>();
        } else {
            arguments = stmt(tokens.subList(index, tokens.size() - 1));
        }

        for(Expr arg :arguments){
            if(arg instanceof Expr.Define){
                throw new SyntaxException("define: not allowed in an expression context",((Expr.Define) arg).name.line);
            }
        }
        return new Expr.Call(callName, name, arguments);


    }

    private Expr.Define define(List<Token> tokens) throws SyntaxException {
        //最简形式:(define a 1)
        if (tokens.size() < 5) {
            throw new SyntaxException("bad syntax", tokens.get(0).line);
        }
        Token name = tokens.get(2);
        if (!match(name, TokenType.IDENTIFIER)) {
            throw new SyntaxException("bad syntax", name.line);
        }
        Expr val = null;
        if (!match(tokens.get(3), TokenType.LEFT_PAREN)) {
            val = primary(tokens.get(3));
        } else {
            List<Expr> exprs = stmt(tokens.subList(3, tokens.size() - 1));
            if (exprs.isEmpty()) {

                throw new SyntaxException("missing expression after identifier", name.line);
            }
            if (exprs.size() > 1) {
                throw new SyntaxException("multiple expressions after identifier", name.line);
            }
            val = exprs.get(0);
        }


        return new Expr.Define(name, val);
    }

    private Expr.Lambda lambda(List<Token> tokens) throws SyntaxException {
        //最简形式:(lambda (a) a)
        if (tokens.size() < 7) {
            throw new SyntaxException("bad syntax", tokens.get(0).line);
        }

        if (!match(tokens.get(2), TokenType.LEFT_PAREN)) {
            throw new SyntaxException("arguments define mismatch", tokens.get(2).line);
        }
        int index = 3;
        List<Token> arguments = new ArrayList<>();
        for (; index < tokens.size(); index++) {
            Token token = tokens.get(index);
            if (match(token, TokenType.IDENTIFIER)) {
                arguments.add(token);
            } else if (match(token, TokenType.RIGHT_PAREN)) {
                break;
            } else {
                throw new SyntaxException("arguments define wrong", tokens.get(0).line);
            }
        }
        //括号内只有一个表达示
        if (index >= tokens.size() - 1) {
            throw new SyntaxException("not lambda body", tokens.get(0).line);
        }
        List<Expr> body = stmt(tokens.subList(index + 1, tokens.size() - 1));


        return new Expr.Lambda(arguments, body);
    }


    private Expr primary(Token token) throws SyntaxException {
        if (match(token, TokenType.IDENTIFIER)) {

            return new Expr.Variable(token);
        }
        if (match(token, TokenType.INTEGER,
                TokenType.LONG,
                TokenType.DECIMAL,
                TokenType.STRING,
                TokenType.TRUE,
                TokenType.FALSE,
                TokenType.NULL)) {
            return new Expr.Literal(token.literal);
        }

        throw new SyntaxException("Bad word", token.line);
    }


}
