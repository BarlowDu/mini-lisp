package com.mini.lisp;



import com.mini.lisp.exception.RuntimeError;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class Resolver implements Expr.Visitor<Void>{

    private Stack<Set<String>> scopes=new Stack<Set<String>>();

    private final Interpreter interpreter;
    public Resolver(Interpreter interpreter){
        this.interpreter=interpreter;
    }
    @Override
    public Void visitLiteralExpr(Expr.Literal expr) {
        return null;
    }

    @Override
    public Void visitVariableExpr(Expr.Variable expr) {
        //expr.accept(this);
        resolveLocal(expr,expr.name);
        return null;
    }

    @Override
    public Void visitCallExpr(Expr.Call expr) {
        resolve(expr.callee);
        for (Expr argument : expr.arguments) {
            resolve(argument);
        }
        return null;
    }

    @Override
    public Void visitDefineExpr(Expr.Define expr) {
        resolve(expr.value);
        resolveLocal(expr, expr.name);
        return null;
    }

    @Override
    public Void visitLambdaExpr(Expr.Lambda expr) {
        resolveFunction(expr);
        return null;
    }

    private void resolve(Expr expr) {
        expr.accept(this);
    }

    public void resolve(List<Expr> exprs){
        for(Expr expr:exprs){
            resolve(expr);
        }
    }

    private void beginScope(){
        scopes.push(new HashSet<String>());
    }

    private void declare(Token name) {
        if (scopes.isEmpty()) return;
        Set<String> scope = scopes.peek();
        if (scope.contains(name.lexeme)) {
            throw new RuntimeError(name,"Already a variable "+name.lexeme+" with this name in this scope.");
        }

//< duplicate-variable
        scope.add(name.lexeme);
    }

    public void endScope(){
        scopes.pop();
    }

    private void resolveFunction(Expr.Lambda lambda){

        beginScope();
        for (Token param : lambda.arguments) {
            declare(param);
        }
        resolve(lambda.body);
        endScope();
    }

    private void resolveLocal(Expr expr, Token name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).contains(name.lexeme)) {
                interpreter.resolve(expr, scopes.size() - 1 - i);
                return;
            }
        }
    }
}
