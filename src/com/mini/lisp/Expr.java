package com.mini.lisp;

import java.util.List;

public abstract class Expr {
    interface Visitor<R>{
        R visitLiteralExpr(Literal expr);
        R visitVariableExpr(Variable expr);
        R visitCallExpr(Call expr);
        R visitDefineExpr(Define expr);
        R visitLambdaExpr(Lambda expr);

    }
    abstract <R> R accept(Visitor<R> visitor);

    static class Literal extends Expr {
        final Object value;
        Literal(Object value) {
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }

    }

    static class Variable extends Expr {
        final Token name;
        Variable(Token name) {
            this.name = name;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpr(this);
        }

    }
    static class Call extends Expr{
        final Expr callee;
        final Token paren;
        final List<Expr> arguments;
        Call(Expr callee, Token paren, List<Expr> arguments) {
            this.callee = callee;
            this.paren = paren;
            this.arguments = arguments;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitCallExpr(this);
        }

    }


    static class Define extends Expr{

        final Token name;
        final Expr value;
        Define(Token name,Expr expr){
            this.name=name;
            this.value=expr;
        }
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitDefineExpr(this);
        }
    }

    static class Lambda extends Expr{

        final List<Token> arguments;
        final List<Expr> body;
        Lambda(List<Token> arguments,List<Expr> body){
            this.arguments=arguments;
            this.body=body;
        }
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLambdaExpr(this);
        }
    }
}
